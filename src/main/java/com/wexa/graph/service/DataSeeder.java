package com.wexa.graph.service;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private final Driver driver;

    public DataSeeder(Driver driver) {
        this.driver = driver;
    }

    @Override
    public void run(String... args) {
        log.info("Running initial script against CognoDB...");
        try (Session session = driver.session()) {
            String seedCypher = """
                MERGE (p1:Person {id: 'dev_alex'}) ON CREATE SET p1.name = 'Alex Rivera'
                MERGE (p2:Person {id: 'dev_sarah'}) ON CREATE SET p2.name = 'Sarah Chen'

                MERGE (s1:Skill {id: 'skill_java'}) ON CREATE SET s1.name = 'Java'
                MERGE (s2:Skill {id: 'skill_spring'}) ON CREATE SET s2.name = 'Spring Boot'
                MERGE (s3:Skill {id: 'skill_cypher'}) ON CREATE SET s3.name = 'Neo4j'
                MERGE (s4:Skill {id: 'skill_docker'}) ON CREATE SET s4.name = 'Docker'
                MERGE (s5:Skill {id: 'skill_k8s'}) ON CREATE SET s5.name = 'Kubernetes'
                MERGE (s6:Skill {id: 'skill_react'}) ON CREATE SET s6.name = 'React'

                MERGE (r1:Role {id: 'role_backend'}) ON CREATE SET r1.title = 'Backend Engineer'
                MERGE (r2:Role {id: 'role_graph_arch'}) ON CREATE SET r2.title = 'Graph Architect'
                MERGE (r3:Role {id: 'role_devops'}) ON CREATE SET r3.title = 'DevOps Engineer'

                MERGE (c1:Course {id: 'course_neo4j'}) ON CREATE SET c1.title = 'Mastering Cypher & Graphs'
                MERGE (c2:Course {id: 'course_k8s'}) ON CREATE SET c2.title = 'Kubernetes in Action'
                MERGE (c3:Course {id: 'course_java'}) ON CREATE SET c3.title = 'Java Fundamentals'
                MERGE (c4:Course {id: 'course_spring_boot'}) ON CREATE SET c4.title = 'Spring Boot MasterClass'
                MERGE (c5:Course {id: 'course_docker'}) ON CREATE SET c5.title = 'Docker For All'

                MERGE (p1)-[:HAS_SKILL]->(s1)
                MERGE (p1)-[:HAS_SKILL]->(s2)
                MERGE (p1)-[:HAS_SKILL]->(s4)
                MERGE (p2)-[:HAS_SKILL]->(s1)
                MERGE (p2)-[:HAS_SKILL]->(s6)

                MERGE (r1)-[:REQUIRES_SKILL]->(s1)
                MERGE (r1)-[:REQUIRES_SKILL]->(s2)
                MERGE (r1)-[:REQUIRES_SKILL]->(s4)
                MERGE (r2)-[:REQUIRES_SKILL]->(s1)
                MERGE (r2)-[:REQUIRES_SKILL]->(s2)
                MERGE (r2)-[:REQUIRES_SKILL]->(s3)
                MERGE (r3)-[:REQUIRES_SKILL]->(s4)
                MERGE (r3)-[:REQUIRES_SKILL]->(s5)

                MERGE (s3)-[:COVERED_BY]->(c1)
                MERGE (s5)-[:COVERED_BY]->(c2)
                MERGE (s2)-[:COVERED_BY]->(c4)
                MERGE (s1)-[:COVERED_BY]->(c3)
                MERGE (s4)-[:COVERED_BY]->(c5)
                """;

            session.executeWriteWithoutResult(tx -> tx.run(seedCypher).consume());
            log.info("CognoDB seed complete.");
        } catch (Exception e) {
            log.warn("Database connection issue during seeding: {}", e.getMessage());
        }
    }
}