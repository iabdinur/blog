---
id: "6"
title: "UIUC MCS - CS 421 Review - Programming Languages and Compilers"
slug: uiuc-mcs-cs-421-review-programming-languages-and-compilers
date: 2026-08-31T12:00:00Z
excerpt: Haskell, lambda calculus, parsing, and Prolog in a single summer term. A dense and demanding course that offers a transformative way of thinking about programs.
tags:
  - UIUC Master of Computer Science
  - Software Engineering
author: iabdinur
coverImage: /images/covers/cs-421.svg
featured: false
draft: false
---

## Overview

* **TLDR:** This is the most theoretically rich course I have taken in the program, and the one that has most profoundly changed how I read code. It progresses from Haskell to formal semantics to parsing to Prolog across twelve topics, and within a summer term, that pace is demanding. The course is well worth the effort, although I would not recommend it as a first exposure to functional programming.
    
* **Difficulty:** Hard
    
* **Opinion:** Enjoyed
    
* **Weekly workload:** 12+ hours
    
* **Semester:** Summer 2026
    

## Class Content

### Lecture Content

The course sets out an ambitious goal: that students will no longer be merely consumers of programming languages, but will be able to implement and document languages of their own and approach research papers in the field with genuine understanding. To that end, it covers twelve topics organized into three movements: functional programming, then semantics and types, and finally parsing and logic programming.

1. **Intro, recursion and higher-order functions.** Haskell from the ground up, induction and recursion, tail recursion, higher-order functions, mapping, and folding. Project Euler problems serve as the introduction.
2. **Algebraic data types and higher-order functions.** Product types and sum types, `map` and `foldr`.
3. **Interpreters and semantics.** Writing interpreters and working with closures, followed by an introduction to formal semantics and big-step rules.
4. **Lambda calculus and continuations.** Lambda calculus with worked examples, evaluation order, Church numerals, the Y combinator, continuations, and the CPS transform.
5. **Type classes and monads.** Polymorphism and type classes, functors and applicatives, monads, and the state monad.
6. **Grammars, First/Follow sets, and regular expressions.** An introduction to grammars, First and Follow sets, regular languages, and right-linear grammars.
7. **LL and LR parsing.** LL parsing, fixing non-LL grammars, LR parsing, and shift-reduce conflicts.
8. **Combinator parsing and small-step semantics.** Including the Church-Rosser property.
9. **Unification and monotype semantics.**
10. **Polytype semantics and Prolog.**
11. **Prolog cut operator, Hoare semantics, variables and parameters.**
12. **Macros.**

Several topics include POGIL activities, which are structured group exercises rather than traditional lectures. Polytype semantics, Hoare semantics, the Prolog cut operator, variables, and macros each have one. These activities mark the points at which the material moves beyond passive viewing and requires students to work through the ideas themselves, and they were where I learned the most. This kind of active learning, in which students construct understanding through their own reasoning rather than simply receiving it, is what made the theory truly lasting.

The sequencing of the course also deserves recognition. Students write interpreters before studying semantics formally, so that when big-step rules are introduced, they have already built the very thing those rules describe. Lambda calculus arrives once students are fluent enough in Haskell that Church numerals feel like an intriguing idea rather than an obstacle. By the time unification appears in topic 9, students have already encountered it implicitly through type inference, and it becomes immediately clear in Prolog. Throughout the course, theory and practice continually reinforce each other, which I believe is the most effective way to teach this material.

### Format and Setup

The summer offering is **100% asynchronous**, with no scheduled lecture meetings and all content delivered as recorded video. Communication takes place on Discord rather than Piazza or Campuswire, which made it noticeably faster to receive answers than in my other courses. Exams are proctored online through ProctorU.

I recommend setting up your environment before the term begins, because the course pins specific versions: **GHC 9.8.4** with **Stack resolver lts-23.28**, along with a release repository that students clone. Configuring Haskell tooling to match a specific resolver can take an afternoon, and that time is far better spent before the first week than during it.

### Assignments

The coursework is completed in Haskell for the first two thirds of the term and in Prolog for the final portion. I did not retain my machine problem files after the term ended, so I am unable to provide a detailed breakdown of each assignment as I have for other courses. Students seeking that level of detail will find the course forums to be a more valuable resource.

What I can share is that the assignments are where the twelve weekly hours are spent. The lectures can be watched at an accelerated pace, but the problems cannot be solved that way. They require students to fully understand the underlying ideas rather than simply recognize them.

### Exams

The course includes three midterms, each aligned with one of its movements. Based on the practice material, the first focuses on Haskell fundamentals such as recursion, tail recursion, and recursive data types. The second covers continuations and the CPS transform, along with type class instances such as Functor, Applicative, and Monad. The third addresses parsing and types, including recursive descent parsers, converting grammars to LL form, constructing LR item sets, resolving shift-reduce conflicts, and unification.

### The Project

The course concludes with a project, for which I built a parser and interpreter for Core, the small functional intermediate language from *Implementing Functional Languages*. Written in Haskell with the Parsec library, the interpreter supports supercombinator declarations, `let` and `letrec`, lambdas, `case` expressions, and algebraic data constructors, and it passes all 28 of the provided test fixtures in roughly 435 lines of code across five modules. One of its most elegant details is the global environment, which is defined in terms of itself so that every supercombinator can refer to every other one. That approach requires only a few lines of code, and it works only because Haskell is lazy. The project brought together nearly everything the course teaches, from parsing to environments to evaluation, and it was a deeply satisfying way to conclude the term.

## My Takeaways

This is the course that most profoundly reshaped how I think, and it is also the one I would most encourage students to schedule thoughtfully.

The greatest value lies in the middle third of the course. Writing an interpreter, then formalizing it as big-step rules, and then working through small-step semantics provides a vocabulary for reasoning about program behavior that I now use constantly. I do not use it to write Haskell, but rather to read unfamiliar code and reason carefully about what it must be doing. Parsing is the most immediately practical component, and after studying LL and LR parsing, grammars no longer feel obscure.

The primary consideration is the pace. Twelve topics spanning Haskell, lambda calculus, type theory, two parsing paradigms, and logic programming represent a full semester of material delivered over a summer. Students who have never written functional code will find that the first three weeks require far more time than the syllabus suggests, and the course does not slow down to accommodate them. This is not a reason to avoid the course. Rather, it is a reason to create the conditions for success before beginning.

In conclusion, I highly recommend this course, but I encourage students to take it on its own. I paired it with two other courses and would not choose to do so again. The material is well worth the challenge, and it deserves the time and focus to engage with it fully.
