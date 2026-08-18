package com.wexa.graph.service;


import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.springframework.stereotype.Service;

import com.wexa.graph.model.EdgeDto;
import com.wexa.graph.model.GraphVisualizationData;
import com.wexa.graph.model.NodeDto;
import com.wexa.graph.model.RoleRecommendation;
import com.wexa.graph.model.SkillGap;

import java.util.*;

@Service
public class SkillGraphService {

    private final Driver driver;

    public SkillGraphService(Driver driver) {
        this.driver = driver;
    }

    public boolean isDatabaseConnected() {
        try {
            driver.verifyConnectivity();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<RoleRecommendation> getRecommendedRoles(String personId) {
        String cypher = """
            MATCH (p:Person {id: $personId})-[:HAS_SKILL]->(s:Skill)<-[:REQUIRES_SKILL]-(r:Role)
            WITH p, r, count(s) AS matchedSkills
            MATCH (r)-[:REQUIRES_SKILL]->(totalReq:Skill)
            WITH r, matchedSkills, count(totalReq) AS totalSkills
            RETURN r.id AS roleId, 
                   r.title AS title, 
                   matchedSkills, 
                   totalSkills,
                   (toInteger(matchedSkills) * 100 / toInteger(totalSkills)) AS matchPercentage
            ORDER BY matchPercentage DESC
            """;

        try (var session = driver.session()) {
            return session.executeRead(tx ->
                tx.run(cypher, Map.of("personId", personId)).list(r ->
                    new RoleRecommendation(
                        r.get("roleId").asString(),
                        r.get("title").asString(),
                        r.get("matchedSkills").asLong(),
                        r.get("totalSkills").asLong(),
                        r.get("matchPercentage").asDouble()
                    )
                )
            );
        }
    }

    public List<SkillGap> getSkillGapAnalysis(String personId, String targetRoleId) {
        String cypher = """
            MATCH (r:Role {id: $targetRoleId})-[:REQUIRES_SKILL]->(missing:Skill)
            WHERE NOT EXISTS {
                MATCH (p:Person {id: $personId})-[:HAS_SKILL]->(missing)
            }
            OPTIONAL MATCH (missing)-[:COVERED_BY]->(c:Course)
            RETURN missing.id AS skillId, 
                   missing.name AS skillName, 
                   c.title AS courseTitle, 
                   c.url AS courseUrl
            """;

        try (var session = driver.session()) {
            return session.executeRead(tx ->
                tx.run(cypher, Map.of("personId", personId, "targetRoleId", targetRoleId)).list(r ->
                    new SkillGap(
                        r.get("skillId").asString(),
                        r.get("skillName").asString(),
                        r.get("courseTitle").isNull() ? "N/A" : r.get("courseTitle").asString())
                )
            );
        }
    }

    public GraphVisualizationData getVisualGraphData() {
        String cypher = """
            MATCH (a)-[r]->(b)
            RETURN a.id AS fromId, 
                   coalesce(a.name, a.title) AS fromLabel, 
                   labels(a)[0] AS fromType,
                   b.id AS toId, 
                   coalesce(b.name, b.title) AS toLabel, 
                   labels(b)[0] AS toType,
                   type(r) AS relType
            LIMIT 50
            """;

        List<NodeDto> nodes = new ArrayList<>();
        List<EdgeDto> edges = new ArrayList<>();
        Set<String> seenNodes = new HashSet<>();

        try (var session = driver.session()) {
            session.executeRead(tx -> {
                var result = tx.run(cypher);
                while (result.hasNext()) {
                    Record record = result.next();
                    String fromId = record.get("fromId").asString();
                    String toId = record.get("toId").asString();

                    if (seenNodes.add(fromId)) {
                        nodes.add(new NodeDto(fromId, record.get("fromLabel").asString(), record.get("fromType").asString()));
                    }
                    if (seenNodes.add(toId)) {
                        nodes.add(new NodeDto(toId, record.get("toLabel").asString(), record.get("toType").asString()));
                    }

                    edges.add(new EdgeDto(fromId, toId, record.get("relType").asString()));
                }
                return null;
            });
        }
        return new GraphVisualizationData(nodes, edges);
    }
}