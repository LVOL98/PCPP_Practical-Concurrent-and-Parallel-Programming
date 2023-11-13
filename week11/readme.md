# Lecture 11: Message Passing I

## Goals

The goals of this lecture are:

* Explain and reason about the Actors model of concurrent computation.
* Explain the differences between message passing and shared memory concurrency.
* Implement systems based on the Actors model using Akka.

## Readings

* [Akka Quickstart Guide](https://developer.lightbend.com/guides/akka-quickstart-java/)

* Akka Documentation (links to 2.6 documentation). Recall that we work with Akka 2.6 as it is open-source. Furthermore, the changes between 2.6 and 2.9 in Akka core should be negligible. When looking into these links, make sure that you select Gradle and Java:
  * [Getting Started Guide](https://doc.akka.io/docs/akka/2.6/typed/guide/index.html)
	* You can ignore sections [Why modern systems need a new programming model](https://doc.akka.io/docs/akka/2.6/typed/guide/actors-motivation.html#why-modern-systems-need-a-new-programming-model) and [How the Actor Model Meets the Needs of Modern, Distributed Systems](https://doc.akka.io/docs/akka/2.6/typed/guide/actors-intro.html#how-the-actor-model-meets-the-needs-of-modern-distributed-systems). Or, if you read them, try to be critical. These sections present a bias opinion in promoting the actors model.
	* You can also skip the section [Overview of Akka libraries and modules](https://doc.akka.io/docs/akka/2.6/typed/guide/modules.html#overview-of-akka-libraries-and-modules).
  * [General Concepts](https://doc.akka.io/docs/akka/2.6/general/index.html)
  * [Introduction to Actors](https://doc.akka.io/docs/akka/2.6/typed/actors.html#introduction-to-actors)
  * [Actor lifecycle](https://doc.akka.io/docs/akka/2.6/typed/actor-lifecycle.html#actor-lifecycle)

* Gul A. Agha. [Actors: A Model Of Concurrent Computation In Distributed Systems](https://apps.dtic.mil/dtic/tr/fulltext/u2/a157917.pdf). MIT Press 1985:
  * Chapter 2, complete.
	* This book is a bit old, but this chapter provides a more objective comparison of shared memory and actors model (as opposed to the sections in the Akka documention mentioned above).

## Lecture slides

See file [lecture11.pdf](lecture11.pdf).


## Exercises

See file [exercises11.pdf](exercises11.pdf).
