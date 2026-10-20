---
id: "5"
title: "UIUC MCS - CS 411 Review - Database Systems"
slug: uiuc-mcs-cs-411-review-database-systems
date: 2026-06-15T12:00:00Z
excerpt: A well-organized introduction to MySQL, MongoDB, and Neo4j that builds valuable breadth across three database paradigms, while leaving room for greater depth.
tags:
  - UIUC Master of Computer Science
  - System Design
author: iabdinur
coverImage: /images/covers/cs-411.svg
featured: false
draft: false
---

## Overview

* **TLDR:** A well-organized and thoughtfully structured introduction to relational, document, and graph databases. The breadth of the course is its greatest strength, as students finish the semester having designed and queried data in three distinct systems, although the depth of coverage does not always match the importance of the topics.
    
* **Difficulty:** Easy
    
* **Opinion:** Mixed
    
* **Weekly workload:** 5 hours
    
* **Semester:** Spring 2026
    

## Class Content

### Lecture Content

The course is officially organized into eight topics. Because several of these topics extend across more than one week of video, the semester unfolds as thirteen lecture blocks:

1. **Conceptual Data Modeling.** The ER model, including relationships, subclasses, and constraints, as well as UML.
2. **Physical Data Modeling.** The relational model, translating ER designs and subclasses into relations, and post-relational modeling for object-based and NoSQL systems.
3. **Computing on Data.** Relational algebra, from the basic and renaming operators to derived operators, along with MapReduce for key-value data.
4. **Designing Schemas.** Functional dependencies, keys, attribute closures, Boyce-Codd and third normal forms, lossless and dependency-preserving decomposition, and multivalued dependencies.
5. **Querying Databases: The Relational Way** - two blocks. The basic form of SQL, null values, and subqueries, followed by set operations, duplicates, and aggregates.
6. **Querying Databases: The Non-relational Ways** - two blocks. Document queries in MongoDB, including complex structures, aggregates, and the case for joins, followed by graph queries in Neo4j.
7. **Manipulating Databases** - two blocks. Organizing and modifying databases, indexes, and views, followed by key constraints, checks and assertions, and triggers.
8. **Accessing and Indexing Data** - three blocks. Accessing data and ISAM indexes, B+ trees, and static, extensible, and linear hash tables.

This structure is intentional. Topics 1 through 7 approach databases from the user's perspective, focusing on how to design and use a database system effectively. Topic 8 then shifts to the system's perspective and examines how a database is built. The semester spans sixteen weeks, with one lecture block in most weeks, spring break in week 7, project demonstrations in week 15, and exams in week 16. Each topic is delivered through six to ten short videos, each between 5 and 20 minutes long, which makes the material accessible and easy to revisit.

No textbook is required. *Database Systems: The Complete Book* (2nd edition) by Garcia-Molina, Ullman, and Widom is recommended as a reference, and I found it particularly valuable for the indexing material, although it was largely unnecessary for the rest of the course. The prerequisite is CS 225 Data Structures, and students are expected to be comfortable programming in major languages such as Python and Java. The machine problems are completed in Python, while the final project may be built in any language.

The indexing material represents the most technically rich portion of the semester. In my view, it is also the most compressed, as it occupies only the final topic of the course, even though it spans three lecture blocks.

### MPs

The course includes approximately five machine problems, released approximately every two weeks and each budgeted at one to two hours. Together they represent the largest share of the grade at 40%, and they build incrementally toward the end-of-semester project. The unifying idea is that students build similar solutions in three very different systems, which provides a meaningful opportunity to experience the strengths and limitations of each paradigm firsthand.

* **MP1 - Setting up database environments** (due week 3). Install and configure MySQL, MongoDB, and Neo4j, load a small toy dataset called "FridayNight" into each system, and run a query against all three to confirm that the setup works.
* **MP2 - Implementing and using relational algebra** (due week 5). Implement the relational operators directly, and then compose them into expressions that answer real questions, including several expressions that are equivalent to one another. This assignment is completed in a Colab notebook.
* **MP3 - Designing schemas and creating databases** (due week 7). Design a schema for the same data in all three systems, create the databases, load the data, and query it.
* **MP4 - Querying MySQL** (due week 10). Load the Academic World dataset from a provided `AcademicWorld.sql` file and write SQL queries against it. All students work from the same standardized schema, which ensures that queries are comparable.
* **MP5 - Querying MongoDB and Neo4j** (due week 12). The same exercise, extended to the document and graph systems.

The submission format is unusual, and I came to value it greatly. Rather than pushing code to a repository and waiting for an autograder, students complete each MP in an online working document. Course staff can view the document and answer questions while students are still working, and the expected output is published in advance so that students can check their progress as they go. Every problem also carries a time estimate. For example, the MySQL section of MP1 is budgeted at 40 minutes and the data load in MP4 at 60 minutes, and I found these estimates to be realistic.

Furthermore, where a question was ambiguous, students were explicitly permitted to state an interpretation and answer under it. This policy removed much of the uncertainty that often surrounds grading expectations, and it reflected a genuine respect for students as capable and independent thinkers.

### Assignment Quizzes

Thirteen assignment quizzes run alongside the MPs, one for each lecture block. They are graded on completion with unlimited attempts, so they function as practice opportunities rather than formal assessments, and together they account for 10% of the grade. Solutions are released afterward, and they provide the most valuable exam preparation available, as the exam questions closely resemble these quizzes rather than the lectures.

### Exam Quizzes

The course includes three exam quizzes, and importantly, they are **not** cumulative. Each one covers two to three topics:

* Exam 1 - Topics 1, 2, 3
* Exam 2 - Topics 4, 5, 6
* Exam 3 - Topics 7, 8

The retry policy is a distinctive feature of the course. Students receive one retry per quiz, and the final score is the higher of the two attempts, with the retry score capped at 92%. Because the course awards an A for any total of 90% or above, a retry can recover a weak first attempt up to an A, but no further. Dividing the material into three parts keeps the review load manageable and ensures that a difficult stretch early in the semester does not carry forward to the end. This design creates space for students to recover, reflect, and continue learning.

### The Project

The final third of the semester is dedicated to a project, completed in a team of two, in which students build an end-to-end database application. Students may also choose to work alone, with the same scope and expectations. The MPs are designed to prepare students for this project. By the time it begins, students have already set up all three systems, designed schemas for each, and queried them, so the project focuses on integrating these components into a working application rather than learning new material.

### Grading

The grading structure is worth understanding before planning your time, because the weights do not always align with the effort required:

| Component | Weight |
| --- | --- |
| Assignment quizzes (13) | 10% |
| Exam quizzes (3) | 30% |
| MPs (about 5) | 40% |
| Project | 20% |

Letter grades follow a standard scale, with 90% and above earning an A, 80% a B, 70% a C, and 60% a D. The course also offers several extra credit opportunities, including additional MP questions and forum participation. In particular, the top 5% of students by Campuswire reputation, which reflects the number and quality of their questions and answers, receive an additional 2% on their overall grade. In addition, the course provides a grade-matching guarantee: if the section's raw average falls below that of the highest-graded section of the same class, scores are curved upward to match.

## My Takeaways

I deeply appreciate that this course covers MongoDB and Neo4j alongside MySQL. Many database courses treat the word "database" as a synonym for "relational" and leave students to discover document and graph stores in the workplace. Designing schemas three times, once for each paradigm, provided a far deeper understanding of the trade-offs than lectures alone could have offered. Connecting the same data to three different models served as a bridge between theoretical concepts and practical application.

My primary reservation concerns depth. At five hours per week, this is one of the lighter courses in the program, and that is evident in the places where the material stops short. Indexing and query execution are covered only in the final topic of the semester, which is precisely where a database course becomes most challenging and rewarding, and that is also where the course concludes. Moreover, the MPs ask students to write queries that work, but rarely to reason about why one execution plan outperforms another.

In conclusion, I recommend this course for its breadth and for an MP format that genuinely supports learning throughout the process. Students should expect a comprehensive survey rather than an in-depth systems course. It pairs well with a more demanding course, and that is how I would choose to schedule it again.
