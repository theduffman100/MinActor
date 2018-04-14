# MinActor
Minimalistic Actor model library. 
This library supports a multi-thread model where individual actor objects are guaranteed to execute in a single thread and can exchange asynchronous messages and set up timers in an efficient way.

The library covers the following three use cases:
 * Sub-classing BaseActor to implement asynchronous event-processing entities such as game rooms
 * Weaving asynchronous layer over service classes
 * Run actor interactions over simulated time
 
The threads used to run actors are pooled. The library does not use any synchronized block.

A Guice-friendly library is provided to support seamless actor attachment and weaving when creating objects using an injector.
