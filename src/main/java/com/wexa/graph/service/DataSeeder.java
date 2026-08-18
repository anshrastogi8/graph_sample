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
                MERGE (p1:Person {id: 'dev_alex', name: 'Alex Rivera'})
                MERGE (p2:Person {id: 'dev_sarah', name: 'Sarah Chen'})

                MERGE (s1:Skill {id: 'skill_java', name: 'Java'})
                MERGE (s2:Skill {id: 'skill_spring', name: 'Spring Boot'})
                MERGE (s3:Skill {id: 'skill_cypher', name: 'Neo4j'})
                MERGE (s4:Skill {id: 'skill_docker', name: 'Docker'})
                MERGE (s5:Skill {id: 'skill_k8s', name: 'Kubernetes'})
                MERGE (s6:Skill {id: 'skill_react', name: 'React'})

                MERGE (r1:Role {id: 'role_backend', title: 'Backend Engineer'})
                MERGE (r2:Role {id: 'role_graph_arch', title: 'Graph Architect'})
                MERGE (r3:Role {id: 'role_devops', title: 'DevOps Engineer'})

                MERGE (c1:Course {id: 'course_neo4j', title: 'Mastering Cypher & Graphs'})
                MERGE (c2:Course {id: 'course_k8s', title: 'Kubernetes in Action'})

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

                MERGE (s2)-[:PREREQUISITE_FOR]->(s3)
                MERGE (s3)-[:COVERED_BY]->(c1)
                MERGE (s5)-[:COVERED_BY]->(c2)
                """;

            session.executeWriteWithoutResult(tx -> tx.run(seedCypher).consume());
            log.info("CognoDB seed complete.");
        } catch (Exception e) {
            log.warn("Database connection issue during seeding: {}", e.getMessage());
        }
    }
}