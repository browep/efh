# Ethereum File Hub #

Trustless Media Purchase

A server and client implementation for using the ethereum blockchain to trustlessly transfer files for payment in ether.

### Goals

* Show proof-of-concept of an ethereum payment channel using Java an Web3J
* Include a visual representation of a payment channel state

### Requirements

* Java 1.8 JDK
* A local Ganache ethereum node running on `localhost:7545`.  Note: checkout [Ganache](https://truffleframework.com/ganache) for an easy local dev node.  The client and server private keys are hard coded in `Constants.java` for easy deployment and are specific to Ganache.

### Build

`./gradle clean build`

This will also run the tests which need the ethereum node running. You will get a `java.net.ConnectException` if it cannot connect.

### Run

Start the server 

`java -jar build/libs/efh-server-0.0.1.jar 5050 100`

Start the client

`java -jar build/libs/efh-client-0.0.1.jar localhost 5050`






