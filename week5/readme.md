# Lecture 5: Performance measurements

## Goals

The goals of this lecture are:

* Motivate the need for performance measurements
* Enable you to perform performance measurements of your own code
* Enable you to  benchmark a number of Java concepts including: object creation, threads,  an algorithm for computing prime factors and a search algorithm.
* Introduce the statistics of benchmarking (normal distribution, mean and variance).
* Make you aware off some pitfalls when using floating point numbers

## Readings 

* The note by Peter Sestoft: [Microbenchmarks in Java and C sharp](https://github.itu.dk/jst/PCPP2023-Public/blob/main/week05/benchmarkingNotes.pdf)
that can be found in the GitHub folder with course material for week 5

You may skip sections 9-12.

## To do before lecture 5
During lecture 5 you will be asked to do some experiments on your own computer. 
In order to do that you need to do:

* make sure you have a local copy (on your computer) of  `code-exercises` (same folder as this file)
* test that you can run ` measurement.java ` (in this subdirectory ` week05/code-exercises/Week05exercises/app/src/main/java/exercises05 `)
for example by executing:

 ` gradle -PmainClass=exercises05.measurement run `

Make sure you can also run   ` timingMultiplication.java `

### Optional readings
* The pitfalls of using floating point numbers: 

 * David Goldberg [What Every Computer Scientist Should Know About Floating-Point Arithmetic](https://github.itu.dk/jst/PCPP2023-Public/blob/main/week05/IEEE754_article.pdf)


## Lecture slides
Could be updated, so please check that you have the latest version

[lecture05.pdf Preliminary version](https://github.itu.dk/jst/PCPP2023-Public/blob/main/week05/lecture05Preliminary.pdf)


## Exercises

[exercises05.pdf](https://github.itu.dk/jst/PCPP2023-Public/blob/main/week05/exercises05.pdf)
