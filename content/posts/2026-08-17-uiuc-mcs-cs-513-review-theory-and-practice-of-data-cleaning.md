---
id: "4"
title: "UIUC MCS - CS 513 Review - Theory and Practice of Data Cleaning"
slug: uiuc-mcs-cs-513-review-theory-and-practice-of-data-cleaning
date: 2026-08-17T12:00:00Z
excerpt: Approachable machine problems and a meaningful two-phase project on a real civic dataset, in a course whose rigor does not yet match the importance of its subject.
tags:
  - UIUC Master of Computer Science
  - Data Science
author: iabdinur
coverImage: /images/covers/cs-513.svg
featured: false
draft: false
---

## Overview

* **TLDR:** The team project is genuinely valuable, and the tools covered are worth knowing. However, the rest of the course is considerably lighter than one would expect at the 500 level, with six short machine problems, no exams, and a selection of tools that feels a decade out of date. I recommend it as a way to balance a demanding semester rather than as a source of deep learning.
    
* **Difficulty:** Easy
    
* **Opinion:** Disliked
    
* **Weekly workload:** 4 hours
    
* **Semester:** Summer 2026
    

## Class Content

### Lecture Content

The course is organized into nine topic weeks, followed by three weeks dedicated to the project.

1. **Intro to data cleaning.** An overview of data cleaning and a definition of data quality.
2. **Validation and transformation.** Regular expressions, in both theory and practice.
3. **Data profiling and exploratory analysis.** An introduction to OpenRefine.
4. **Relational model and queries.**
5. **Datalog and integrity constraints.**
6. **Integrity constraints and SQL.** Specifically SQLite.
7. **Workflows.** Workflow automation and provenance.
8. **Provenance.** Data provenance in greater depth.
9. **YesWorkflow.**
10. **Data cleaning project (weeks 10-12).** Including a session on taxonomy alignment.

The title promises both theory and practice. In fairness, the course does deliver some theory, as Datalog and integrity constraints receive a genuinely formal treatment. Overall, however, the balance leans heavily toward tool walkthroughs.

No textbook is required. Instead, the syllabus recommends a collection of research readings on data quality, including Rahm and Do's *Data Cleaning: Problems and Current Approaches* and Abedjan et al.'s *Detecting Data Errors: Where Are We and What Needs to Be Done?* These readings offer a valuable foundation for students who wish to explore the field more deeply than the course itself does.

### MPs

The course includes six machine problems, most of which are Jupyter notebooks. None of them required more than an evening to complete.

* **MP1 - Regular expressions.** Four separate problems: matching IP addresses, standard date formats, non-standard date formats, and email addresses.
* **MP2 - OpenRefine.** Clean a messy Airbnb dataset into a target clean version and export the operation history as a recipe, so that the cleaning process is reproducible rather than performed by hand.
* **MP3 - Datalog.** Two problems, one on family relations and one on publications, written as Datalog rule files with a verification script.
* **MP4 - SQLite.** An introduction to SQLite, followed by integrity constraints.
* **MP5 - Recursive queries.** Ancestor relations, plus two additional problems.
* **MP6 - Python and pandas.**

The progression is thoughtfully designed, moving from regular expressions for field-level cleaning, to OpenRefine for interactive bulk cleaning, to Datalog and SQL for constraint checking, and finally to pandas for programmatic work. The challenge is depth. Each MP introduces a tool and then concludes well before the point at which the tool becomes difficult. I would have welcomed more opportunities to grapple with the material, because meaningful learning so often takes place in that struggle.

### The Team Project

The project is announced in week 7 and is completed in two phases, in teams of three, using a real dataset of each team's choosing. In my term, Phase I was due on July 12 and Phase II on August 2. This is the most valuable part of the course.

**Phase I focuses on planning.** Each team selects a dataset, describes its origin and structure, and identifies its data quality problems. My team selected the Chicago Food Inspections dataset from the City of Chicago open data portal, a flat, denormalized table in which each row represents one inspection. The dataset has been continuously updated since 2010, and our snapshot contained 298,869 rows spanning January 2010 to October 2025. We documented nine distinct quality problems and committed to a clear use case: ranking Chicago ZIP codes by their restaurant failed-inspection rate over the past three years, so that the health department could direct inspectors to the areas of highest risk. The project also asks each team to frame three use cases: one that requires no cleaning at all, the main use case that cleaning serves, and one that no amount of cleaning can satisfy. This framing makes the scope of the cleaning effort defensible rather than arbitrary.

**Phase II focuses on execution.** Each team carries out its plan and documents the process so that others can reproduce it from the pinned snapshot onward. We organized our work into three stages, each addressing a portion of the nine problems identified in Phase I.

* **Text standardization in OpenRefine.** We used GREL to trim and collapse whitespace across name and address fields, applied key collision and nearest neighbor clustering to consolidate fourteen spellings of Chicago into a single value while preserving genuine suburbs such as Evanston and Schaumburg, and used fingerprint clustering to reduce the number of distinct facility types from 520 to 424. We deliberately stopped there, because many of the remaining labels represent genuine license categories.
* **Typing, repair, and normalization in Python.** We converted ZIP codes to five-character text, normalized inspection dates to ISO format, and split composite risk labels into a level and a category. We also repaired a subtle identity problem: 769 rows carried a license number of zero, which would otherwise have merged unrelated businesses into a single false establishment. The results were loaded into a normalized schema with separate tables for establishments and their locations.
* **Violation parsing with regular expressions.** The raw dataset stores every violation for an inspection in a single nested text field. We parsed it into 974,597 individual violation records and tagged each one by era, because the Chicago Department of Public Health renumbered its violation codes on July 1, 2018, so the same code can carry two different meanings.

Finally, we expressed each Phase I problem as a denial constraint and ran the same integrity queries against both the raw and the cleaned data, which allowed us to demonstrate measurable improvement rather than simply assert it. We documented the full pipeline with a YesWorkflow model alongside the OpenRefine operation history. Within the team, I was responsible for profiling, the Python parsing and schema work, and quantifying the changes between the raw and cleaned datasets.

Committing to a use case at the outset is what makes the project successful. It transforms "clean this data," an unbounded task, into "clean this data sufficiently to answer this question," which reflects how data cleaning works in practice. Fifteen years of manual data entry by hundreds of inspectors produces exactly the kind of complexity that no tutorial dataset can replicate. Furthermore, working with civic data connected the project to a purpose beyond the classroom.

### Exams

There are no exams. The project serves in their place.

### Grading

The grading consists of two components, and the distribution explains a great deal about the character of the course:

| Component | Weight |
| --- | --- |
| Homework | 75% |
| Data cleaning project | 25% |

The homework is auto-graded with **unlimited attempts**, which means that three quarters of the grade is effectively secured through persistence alone. The project is graded by hand and is the only work that receives individual review. Discussion takes place on Piazza. The course runs for twelve weeks and carries four credit hours. The grading scale is also notably generous, with an A- beginning at 85% and a B at 75%.

## My Takeaways

I am grateful for the experience of completing the project, although I would not recommend this course as a place to develop deep expertise.

Data cleaning is one of the most impactful skills in the field. The course syllabus itself notes that some surveys report data scientists spending around 80% of their time cleaning and preparing data, and a course that approached it with full rigor could be among the most valuable in the program. This course has the right vision but does not fully realize it. The MPs serve as introductions rather than substantive exercises, and there is no exam. At four hours per week, it asks less of students than any other course I have taken in the program, despite its 500-level designation.

The choice of tools compounds this challenge. OpenRefine and YesWorkflow are reasonable teaching vehicles, but a 2026 course on data cleaning that engages with the modern Python data ecosystem only through a single pandas assignment leaves much of the most relevant material unexplored. Students would benefit greatly from a more ambitious treatment of a skill they will use throughout their careers.

That said, the 500-level options in the program are limited, and a lighter course has real value when balanced against more demanding ones. I took this course alongside CS 421 and CS 519, both of which required more than twelve hours per week, and the combination worked precisely because this course asked so little. In conclusion, students who schedule it with that purpose in mind will be well served, provided they do not expect it to be the defining course of their semester.

### More Reviews

I have also written reviews of [**CS 411**](/post/uiuc-mcs-cs-411-review-database-systems), [**CS 421**](/post/uiuc-mcs-cs-421-review-programming-languages-and-compilers), [**CS 427**](/post/uiuc-mcs-cs-427-review-software-engineering), and [**CS 519**](/post/uiuc-mcs-cs-519-review-scientific-visualization).
