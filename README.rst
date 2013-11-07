Cohorte - jabsorb fork
######################

This project is a fork of the *jabsorb* project.
The original repository can be found at http://code.google.com/p/jabsorb/

*jabsorb* stands for *java - javascript object request broker*.
From the original project website:

    jabsorb is a simple, lightweight JSON-RPC library for communicating between
    Javascript running in the browser and Java running on the server.
    It allows you to call Java methods on the server from Javascript as if they
    were local, complete with automatic parameter and result serialization.

This work is supported by `isandlaTech <http://www.isandlatech.com>`_.
Contributions are welcome.


About this version
******************

This version adds support of user-specified class loaders to load beans, making
it usable within an OSGi framework.

Some modifications that have been applied make this fork incompatible with the
original project.
The main incompatibility is the way sets are serialized (array vs. object).


Compilation
***********

Compilation is based on Maven, simply run ``mvn clean install``.


License
*******

This fork, as the original project, is released under Apache License 2.0
