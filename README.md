# 🚀 SkillPath — CognoDB-Powered Career Graph & Skill Gap Engine

SkillPath is a full-stack graph application built with **Spring Boot 3** and **CognoDB (Cloud Graph Database)**. It models skills, roles, developer profiles, and learning courses as an interconnected graph network to provide real-time role recommendations, multi-hop skill gap analysis, and dynamic network visualisations.

## 🛠️ Tech Stack

* **Backend:** Java 17, Spring Boot 3.x, Spring Web, Spring Boot DevTools
* **Database:** CognoDB Cloud (Neo4j/Bolt 5.x protocol) via official Neo4j Java Driver
* **Frontend:** Thymeleaf, Tailwind CSS
* **Graph Rendering:** Vis.js Network Library
* **Build & Ops:** Maven, Docker, Render

---

## 💡 Why a Graph Database Over Relational SQL?

Calculating skill matches and career paths in a traditional Relational Database Management System (RDBMS) requires complex, multi-table `JOIN` operations across `users`, `user_skills`, `skills`, `role_skills`, `roles`, and `course` tables. As depth increases (e.g., finding prerequisite skill dependencies or multi-hop pathing), SQL performance degrades exponentially due to Cartesian product expansion.

**Advantages of CognoDB / Graph DB for this use case:**
1. **Index-Free Adjacency:** Graph traversals follow direct physical pointers between nodes in $O(1)$ time per step, regardless of total dataset size.
2. **Native Pattern Matching:** Multi-hop queries (e.g., `Person -> Skill -> Role` and `Role -> Skill -> Course`) are expressed cleanly in declarative Cypher without fragile join tables.
3. **Flexible Schema:** Adding new entity types (e.g., `Certifications`, `Projects`) requires zero structural migration or table alter scripts.

---
### 📊 Graph Data Model

```text

  (Person) ---[:HAS_SKILL]---> (Skill) ---[:PREREQUISITE_FOR]---> (Skill)
                                  ^                                  |
                                  |                             [:COVERED_BY]
                          [:REQUIRES_SKILL]                          v
                                  |                               (Course)
                               (Role)Y]->(:Course)

```

# Node Labels & Attributes
* Person — {id: String, name: String}
* Skill — {id: String, name: String}
* Role — {id: String, title: String}
* Course — {id: String, title: String, url: String}

Relationship Types
* Person - :HAS_SKILL -> Skill
* Role - :REQUIRES_SKILL -> Skill
* Skill - :PREREQUISITE_FOR -> Skill
* Skill - :COVERED_BY -> Course
