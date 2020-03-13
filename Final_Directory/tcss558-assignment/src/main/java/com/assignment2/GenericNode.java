package com.assignment2;
/**
 * TCSS 558
 * Assignment 2
 * @author Danielle Lambion, Sonia Xu, Asmita Singla
 * @since 2020-02-05
 *
 * Generic node class creates either a TCP client or TCP server depending on command line specification. This class
 * contains the main method that drives the rest of the program.
*/
public class GenericNode {
    public static void main(String args[]) throws Exception {
        String type = args[0];
        if(type.equalsIgnoreCase("tc")){
            TcpClient node = new TcpClient();
            node.start(args);
        } else if(type.equalsIgnoreCase("ts")) {
            TcpServer node = new TcpServer();
            node.start(args);
        }
    }
}