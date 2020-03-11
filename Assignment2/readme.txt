Danielle Lambion
TCSS 558
Assignment 1

Project Information:
-------------------------------------------------------------------------------
Project was developed as a Maven build in the IntelliJ IDE in Java. The program
was tested in Java version 11.
All possible grading criteria for this project was attempted. The only exception is the RMI server is not multi-threaded.
Please only test multi-threading in UDP and TCP.

Compilation Directions: 
-------------------------------------------------------------------------------
Navigate to the directory /DanielleLambion_Assignment1/src/main/java. Type the following Linux command to compile:

$ javac *.java

Then gather the compiled classes and manifest file with the following Linux command:

$ jar cvfm GenericNode.jar Manifest.txt *.class

Now an executable jar is created and can be used for the creation of UDP, TCP, and RMI
clients and servers.

Alternatively, open the Maven project in an IDE and use the IDE to create a jar file (this did not work in IntelliJ though).

Progam Usage and Interaction:
-------------------------------------------------------------------------------
TCP:
-----
Start the TCP server with the following command:
java -jar GenericNode.jar ts <port>

Use the TCP client to interact with the server using the following commands:
java -jar GenericNode.jar tc <IP of server> <port of server> put <key> <value>
java -jar GenericNode.jar tc <IP of server> <port of server> get <key>
java -jar GenericNode.jar tc <IP of server> <port of server> del <key>
java -jar GenericNode.jar tc <IP of server> <port of server> store
java -jar GenericNode.jar tc <IP of server> <port of server> exit

UDP:
-----
Start the UDP server with the following command:
java -jar GenericNode.jar us <port>

Use the UDP client to interact with the server using the following commands:
java -jar GenericNode.jar uc <IP of server> <port of server> put <key> <value>
java -jar GenericNode.jar uc <IP of server> <port of server> get <key>
java -jar GenericNode.jar uc <IP of server> <port of server> del <key>
java -jar GenericNode.jar uc <IP of server> <port of server> store
java -jar GenericNode.jar uc <IP of server> <port of server> exit

RMI:
-----
Start the RMI registry with:
rmiregistry &

Start the RMI server with the following command:
java -jar GenericNode.jar rmis

Use the RMI client to interact with the server using the following commands:
java -jar GenericNode.jar rmic <IP of server> put <key> <value>
java -jar GenericNode.jar rmic <IP of server> get <key>
java -jar GenericNode.jar rmic <IP of server> del <key>
java -jar GenericNode.jar rmic <IP of server> store
java -jar GenericNode.jar rmic <IP of server> exit
