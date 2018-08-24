# Ethereum File Hub #

Trustless Media Purchase

A server and client implementation for using the ethereum blockchain to trustlessly transfer files for payment in ether.

### Goals

* Show proof-of-concept of an ethereum payment channel using Java and Web3J
* Include a visual representation of a payment channel state

### Requirements

* Java 1.8 JDK
* A local Ganache ethereum node running on `localhost:7545`.  Note: checkout [Ganache](https://truffleframework.com/ganache) for an easy local dev node.  The client and server private keys are hard coded in `Constants.java` for easy deployment and are specific to Ganache.

### Build

`./gradle clean build`

This will also run the tests which need the ethereum node running. You will get a `java.net.ConnectException` if it cannot connect.

### Run

#### Start the server 

`java -jar build/libs/efh-server-0.0.1.jar <port number> <delay millis> <file path>`

* `port number` is the port the server is listening on
* `delay millis` is the delay millis, which allows the file transfer to go slower so the pause button is usable.
* `/path/to/a.file`  this is the file that is being transferred.  It is reccommended to use a decent size file > 100MB if possible.

example

`java -jar build/libs/efh-server-0.0.1.jar 5050 100 /path/to/a.file`

#### Start the client

`java -jar build/libs/efh-client-0.0.1.jar <server host> <server port number> <file size>`

* `server host` the hostname for the server
* `server port number` server port
* `file size` the expected size of the file

example

`java -jar build/libs/efh-client-0.0.1.jar localhost 5050 155611039`






