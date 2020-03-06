Programmers: Asmita Singla and Sonia Xu
Language version: Java 8
IDE: Eclipse
HARDWARE USED: Mac

The readme.txt should describe how to compile your source code and produce a working JAR file (if Java).

To compile source code to produce a working JAR file:

cd assignment1-sourcecode
jar cvfe GenericNode.jar GenericNode *.class

To run servers:

#TCP Server
java -jar GenericNode.jar ts 1234

#UDP Server
java -jar GenericNode.jar us 1234

#RMI Server
java -jar GenericNode.jar rmis


TESTING:

PERFORMANCE ON LOCAL COMPUTER (MAC): 
TCP
real	0m18.848s
user	0m18.174s
sys	0m4.938s

UDP
real	0m20.528s
user	0m18.091s
sys	0m5.619s

RMI
real	0m30.982s
user	0m37.279s
sys	0m6.838s
