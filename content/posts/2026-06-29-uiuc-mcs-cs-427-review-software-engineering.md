---
id: "2"
title: "UIUC MCS - CS 427 Review - Software Engineering"
slug: uiuc-mcs-cs-427-review-software-engineering
date: 2026-06-29T12:00:00Z
excerpt: A real open-source codebase, a team-based Android project, and LLM coding agents come together in the most demanding and rewarding course I have taken.
tags:
  - UIUC Master of Computer Science
  - Software Engineering
author: iabdinur
coverImage: /images/covers/cs-427.svg
featured: false
draft: false
---

## Overview

* **TLDR:** This is the most demanding course I have taken in the MCS program so far, and the one in which I have grown the most. The machine problems place students inside a real open-source codebase rather than a simplified one, and the team project is a genuine multi-person Android development effort rather than an individual assignment with additional steps.
    
* **Difficulty:** Hard
    
* **Opinion:** Enjoyed
    
* **Weekly workload:** 12+ hours
    
* **Semester:** Spring 2026
    

If you have read older reviews of this course, I encourage you to check their dates carefully. The version I completed differs meaningfully from the one described in reviews from a few years ago, and the fourth-credit-hour track in particular is entirely new.

## Class Content

### Lecture Content

The lectures progress through the software lifecycle in approximately the order in which it unfolds on a real project:

1. **Introduction to Software Engineering.** The origins of the discipline, software and software projects, and the role of the software engineer.
2. **Software Process.** Plan-driven and agile processes, choosing a process, project planning, scheduling with Gantt and PERT charts, and risk management.
3. **Requirements Engineering.** Types of requirements, elicitation, use cases, and requirements validation.
4. **Software Design and Architecture.** Abstraction, cohesion, modularity, architectural styles, components and connectors, MVC, and Conway's Law.
5. **OOP Patterns I.** Class diagrams and their relationships, moving from requirements to a class diagram, and sequence diagrams.
6. **OOP Patterns II.** Creational, structural, and behavioral design patterns, including Observer, Composite, Interpreter, Iterator, and Strategy.
7. **Software Testing I.** Testing activities, the relationship between faults and failures, black-box and white-box testing, and code coverage.
8. **JUnit and Parameterized Tests.** Unit testing, assertions, assumptions, and parameterized tests.
9. **Debugging.** Problem tracking, reproducing failures, failing-input simplification, coverage-based fault localization, assertions, and design by contract.
10. **Software Maintenance.** Size, complexity, and object-oriented metrics, refactoring, and code smells.

Spring break falls in week 9, the agentic project occupies week 15, and the final exam takes place in week 16.

Object-oriented patterns and testing each receive more than one week of coverage, which aligns well with how extensively the MPs rely on them later in the semester. The lecture videos total one to three hours each week, and the syllabus recommends spending at least as much time again digesting the material. In addition, the instructor holds live office hours or hosts guest talks in most weeks, which are optional but particularly helpful for the MPs and the project. No textbook is required, although the syllabus recommends Sommerville's *Software Engineering* and Pressman's *Software Engineering: A Practitioner's Approach* for further reading.

### Quizzes

Most weeks conclude with a graded quiz. Students may attempt each quiz several times with no time limit, and the highest score counts toward the final grade, so the quizzes reward careful review rather than speed. Together they account for 15% of the grade for three-credit students and 11.25% for four-credit students.

### MPs

The course includes five machine problems, released every two to three weeks, and the course estimates three to six hours for each, which matched my experience. Four of the five are built on [Jsoup](https://jsoup.org), a widely used Java HTML parsing library, through a course-maintained fork. This single decision is what makes the MPs so valuable. Rather than implementing a simplified data structure, students run coverage tools against a library that other developers genuinely depend on, and they work to identify where its tests are incomplete.

* **MP1 - Java and build system basics.** Clone the course's Jsoup fork and become comfortable building it and running its tests from the command line.
* **MP2 - Design patterns.** Use [JavaParser](https://javaparser.org) to transform Java source code into an abstract syntax tree and traverse it. JavaParser is itself built around the Visitor pattern, so students use the pattern in order to study the pattern.
* **MP3 - Testing.** Collect code coverage on Jsoup with [Clover](https://openclover.org), identify the areas with weak coverage, and improve the tests.
* **MP4 - Debugging.** Implement Spectrum-Based Fault Localization, ranking statements by suspiciousness using the Ochiai formula, so that a developer can begin debugging at the most likely source of the fault. This assignment is delivered as its own Maven starter project rather than running against Jsoup.
* **MP5 - Code smells and static analysis.** Refactor `TokenQueue.java` until it satisfies a strict Checkstyle-style rule set, including line and method length limits, no magic numbers, one statement per line, a single return per method, mandatory braces, and no empty blocks.

MP1 through MP4 each include an optional bonus problem, covering continuous integration, randomized `HashMap` iteration order, mutation testing, and a particularly unusual exercise about writing code whose only purpose is to pass its tests. These problems are open-ended by design and come with looser instructions than the main MPs. They cannot raise a grade above 100%, they only recover points lost elsewhere, and they do not appear in the gradebook. For these reasons, I encourage students to approach them as opportunities for learning rather than as insurance.

### The Team Project

The team project is an Android weather application, written in Java and developed across five milestones with a team of six to eight assigned members. It accounts for 40% of the grade for three-credit students and 30% for four-credit students, making it the single largest component in either case.

1. **Team formation and project setup.** Teams are assigned automatically from a CATME survey that covers availability and preferences, and once formed, they are final. Each member takes on a role as Manager, Tech Lead, or Developer. Teams are formed within a single credit-hour section.
2. **Requirements engineering.** Define use cases and design the application before implementing it. Peer evaluations begin at this stage and continue throughout the project.
3. **Implementation, phase 1.** User login and authentication, adding and removing cities from a personal list, and per-user UI themes.
4. **Implementation, phase 2.** Weather details for a selected city, an interactive map, and an LLM-powered "weather insights" screen that suggests questions and answers them. The deliverables include a demonstration video of no more than five minutes that walks through the complete flow.
5. **Validation and testing.** Write Espresso instrumented tests by hand for most of the functionality, and use LLM-generated tests for the remainder.

The coordination is the true assignment. The features themselves are not especially difficult, but five sequenced iterations with fixed teams, assigned roles, and recurring peer evaluations provide a far more authentic simulation of collaborative work than most coursework offers. I encourage students to budget time for meetings and not only for coding. This experience gave me a deeper appreciation for how much learning takes place beyond the code itself, in planning, communication, and upholding a shared standard of quality.

### The Fourth Credit Hour

Students in the four-credit version complete an additional assignment worth 25% of their grade. Older versions of the syllabus describe this as a book report, as do older reviews of the course, but that is no longer the case. In my term, it was an assignment on LLM coding agents, the class of tools behind Devin, OpenHands, SWE-Agent, Claude Code, and Codex. Working in a team, students study a minimal existing coding agent, extend it with more sophisticated features to improve its performance, evaluate it against a subset of SWE-Bench problems and real open GitHub issues, and write a summary report. The assignment spans three milestones: repository setup, a midterm checkpoint, and a final submission.

My team built on mini-swe-agent, a deliberately minimal agent from the SWE-agent project that resolves GitHub issues in roughly a hundred lines of code. Our central contribution was a tool registry that allows the language model to call safe, well-defined tools rather than relying solely on raw shell commands. Alongside the provided `read_file` example, we added six tools: `write_file`, `list_directory`, `search_file_content`, `read_many_files`, `replace`, and `todo_write`, the last of which exists solely to help the agent plan its own work. We evaluated the agent with Gemini 2.5 Flash on a forty-instance slice of SWE-bench Verified, where it resolved 23 of the 40 problems at both the midterm and the final checkpoint. Interestingly, the score remained the same while the pattern of failures changed, which reinforced the importance of reporting results honestly rather than claiming an improvement the data does not support. We then directed the agent at sixteen real open-source issues, in projects such as containerd, moby/swarmkit, and impacket, preserving the patch and the full trajectory for each attempt.

It is worth knowing before you begin that full evaluation runs are slow and may encounter API rate limits, so this is the assignment most likely to penalize a late start.

### The Final Exam

The course concludes with a final exam in the last week, after Reading Day, taken online and proctored through ProctorU. It accounts for 20% of the grade for three-credit students and 15% for four-credit students. Online proctoring has never been my preferred format, and this experience was no exception, but no alternative is offered.

### Grading

| Component | 3 credit | 4 credit |
| --- | --- | --- |
| Quizzes | 15% | 11.25% |
| Programming assignments (MPs) | 25% | 18.75% |
| Project | 40% | 30% |
| Final exam | 20% | 15% |
| 4th credit hour assignment | - | 25% |
| Extra credit | up to 3% | up to 3% |

The four-credit weights are simply the three-credit weights scaled to 75%, with the additional assignment accounting for the remainder. Letter grades follow the scale in the syllabus, with 93% required for an A, 90% for an A-, 87% for a B+, 83% for a B, and 80% for a B-.

Two policies deserve careful attention. Late work is accepted only if requested at least one week before the deadline, the penalty is 30% per week, project milestones are not eligible for late submission at all, and no assignment is accepted after Reading Day. In addition, the course policy states that students should not use LLMs unless explicitly permitted, which sits somewhat uneasily alongside a fourth-credit assignment focused on building LLM agents and a project milestone that requires LLM-generated tests. I recommend reading each assignment's instructions carefully rather than relying on assumptions.

## My Takeaways

This course was well worth the time it required. What distinguishes it from the other MCS courses I have taken is that almost nothing is synthetic. Students analyze the test suite of a real library, refactor real code against real lint rules, and deliver an application alongside teammates whose schedules rarely align. This approach closely reflects how software is built in practice, and it provides a more authentic and meaningful education as a result.

The team project is where most of the hours are spent, and it is also the component most likely to be frustrating. Fixed teams and sequenced deliverables mean that a single unresponsive teammate can affect the entire semester. That challenge, however, is also the most realistic aspect of the experience. Learning to navigate it while maintaining a high standard is an essential part of what the course teaches.

Experienced engineers will likely find the lecture content familiar. The MPs, however, will be new to most students unless they already work in program analysis, since fault localization and AST-based tooling are not part of most day-to-day roles. In conclusion, I would gladly take this course again, although I would not pair it with another demanding course. When given the attention it requires, it offers a great deal in return.
