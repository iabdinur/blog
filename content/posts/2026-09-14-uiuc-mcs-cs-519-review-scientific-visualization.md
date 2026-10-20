---
id: "7"
title: "UIUC MCS - CS 519 Review - Scientific Visualization"
slug: uiuc-mcs-cs-519-review-scientific-visualization
date: 2026-09-14T12:00:00Z
excerpt: Marching cubes, volume ray casting, and flow visualization. An excellent course in graphics and numerical methods, presented under the name of visualization.
tags:
  - UIUC Master of Computer Science
  - Data Science
author: iabdinur
coverImage: /images/covers/cs-519.svg
featured: false
draft: false
---

## Overview

* **TLDR:** This course should not be mistaken for a data visualization course. There are no dashboards and no charts. Instead, students implement marching cubes, cast rays through a CT scan, and integrate streamlines through a vector field. It is fundamentally a course in graphics and numerical methods presented under the name of visualization, and it is excellent.
    
* **Difficulty:** Hard
    
* **Opinion:** Enjoyed
    
* **Weekly workload:** 12+ hours
    
* **Semester:** Summer 2026
    

## Class Content

### Lecture Content

The course draws on material from two Coursera MOOCs, supplemented by additional assignments, a final project, and two exams. Each week includes one to two hours of condensed, densely packed video lessons, and the syllabus recommends spending at least as much time again reviewing them. No textbook is required. The course covers twelve topics, building from how a single pixel receives its color all the way to rendering tensor fields.

1. **Introduction.** The nature and purpose of scientific visualization, digital images and color models such as RGB and HSV, perceptual color spaces, sRGB, and gamma correction.
2. **Scalar field visualization.** Rendering, rasterization, shading, the relationship between rendering and visualization, and colormaps.
3. **Data science for people in a hurry.** A data taxonomy, linear interpolation, barycentric coordinates, scattered data interpolation, and marching squares.
4. **Domain modeling and terrain visualization.** Meshes and elements, triangulated surface meshes, data structures for polygonal meshes, and terrain generation for GIS.
5. **Contouring.** Marching cubes, its consistency and correctness challenges, marching tetrahedra, and dual marching squares and cubes.
6. **Volume visualization.** Volume rendering, ray casting, volumetric shading, and compositing.
7. **Thresholding, segmentation and classification.** Thresholding, segmenting multiple objects, graph cuts, interactive graph cuts, and transfer functions.
8. **Vector field visualization.** Glyphs and derived quantities.
9. **Advanced flow visualization.** Euler's method, numerical differentiation, Runge-Kutta methods, displacement plots, line integral convolution, and stream objects.
10. **Tensor field visualization.** Sampling domains, the nature of tensors, the curvature tensor, and the diffusion tensor.
11. **Bioinformatics visualization.** Fiber tracking, visualizing trees, and force-directed layout.
12. **Visualization at scale.** Edge filtering, edge bundling, and mesh simplification.

Beginning with gamma correction and perceptual color spaces establishes the tone for the entire course. It emphasizes why an image appears the way it does at the level of the underlying numbers, rather than at the level of design preference. This commitment to understanding before presentation is what I valued most.

### Weekly Work

Each week includes a quiz and one or more short assignments completed online. The quizzes are graded pass/fail at 80%, may be repeated as many times as needed, and retain the highest score, so they serve as study aids rather than formal assessments.

The most substantial work takes place in the notebooks. These are implementation assignments built on the scientific Python ecosystem, in which students write the algorithm themselves rather than calling a library that already provides it.

* **MP1 - Images, colormaps and contouring.** Work with digital images and colormaps, and produce contoured output. The deliverables include rough and interpolated variants in both grayscale and plasma colormaps, which makes the effect of interpolation on contour quality unmistakably clear.
* **MP2 - Scattered data interpolation.** Reconstruct a potential field from scattered data points using radial basis functions, verified against a reference image.
* **MP3 - Volume rendering.** Ray cast a real CT head scan provided as a gzipped VTK volume, in three staged parts, each with its own reference render.
* **MP4 - Flow visualization.** Vector field work that culminates in line integral convolution.
* **MP5 - Mesh simplification.** Reduce mesh complexity, with before and after renders serving as evidence.

Every MP includes reference images against which students compare their output. This provides a far more effective feedback loop than a numeric autograder for this kind of work, because when a ray caster is subtly incorrect, students can see precisely how. The ability to evaluate and correct one's own work fosters independence and a deeper understanding of the material.

In addition to the notebooks, the course includes two larger programming assignments that are graded by course staff rather than checked automatically. The first is a 2D mathematical visualization early in the term, designed to build fluency in Python. The second, later in the term, is a volume visualization application built with **VTK.js**, which is where the course introduces JavaScript. Both are individual assignments with firm deadlines, carrying a penalty of 10% per day late, capped at seven days, with no extensions offered.

### The Final Project

The final project is a visualization application of each student's own choosing, which may be completed in a team of up to three. Students submit a proposal partway through the term and the finished project at the end. The choice of technology is open, provided the instructor confirms that it can be graded. The same late policy applies as for the programming assignments.

This is the most thoughtfully designed component of the course. After a term of implementing established algorithms against reference images, the invitation to build something original and justify one's design choices is the moment when the material comes together.

### Exams

The course includes two exams, a midterm near the middle of the term and a final at the end, each worth 15%. Both are online and synchronous, and both are **open-web**, meaning students may consult any reference they choose. While this may sound generous, in practice it means the questions are designed to go well beyond anything that could simply be looked up.

### Grading

Rather than weighting categories of work, the syllabus assigns a weight to each weekly module. Most weeks are worth 5%, the weeks containing the two larger programming assignments and the final project carry 10%, and the midterm and final exams are worth 15% each. This structure rewards steady engagement throughout the term, as no single week outside the exams determines the outcome.

### Tooling

It is worth understanding the tools involved before enrolling: Python throughout, along with **NumPy**, **PyVista**, the **ParaView** application, and **VTK.js** with JavaScript for the web graphics portion. The stated prerequisite is CS 418 Interactive Computer Graphics or CS 498DV Data Visualization, or the consent of the instructor. The syllabus notes that the material should be accessible to anyone with some familiarity with calculus, prior coding experience, and the drive to learn. Even so, I would strongly recommend treating the prerequisite as essential.

## My Takeaways

This course was the greatest surprise of my summer. I enrolled expecting something closely related to data visualization and instead discovered a course on the numerical methods behind scientific rendering, which proved to be far more valuable than the course I had anticipated.

The MPs are what make the course exceptional. Implementing marching cubes and then examining its consistency challenges in lecture teaches the algorithm in a way that reading about ambiguous cube configurations alone never could. Ray casting a real CT volume and watching anatomical structures emerge as the compositing improves is a deeply rewarding experience that coursework rarely provides. Rather than simply viewing an image, students construct it, and that process transforms their understanding of it.

I would offer two considerations. First, the mathematics is substantial, including interpolation, numerical integration, Runge-Kutta methods, and tensors. Students whose linear algebra and calculus are not recent should set aside time to review them, because the course assumes this knowledge rather than teaching it. Second, the course title can be misleading. Students seeking Tableau, D3, or dashboard design will not find that material here.

In conclusion, for anyone who wishes to understand how scientific images are produced, I highly recommend this course. It is one of the few courses in which I completed the assignments and wanted to continue exploring, and that sense of curiosity is the most meaningful outcome a course can provide.
