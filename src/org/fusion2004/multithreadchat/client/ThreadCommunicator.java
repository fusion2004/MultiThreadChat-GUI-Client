package org.fusion2004.multithreadchat.client;

import org.fusion2004.multithreadchat.client.enums.command;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author Fusion2004
 */
public class ThreadCommunicator {
    private static String host = "localhost";
    private static Integer port = 7777;
    private static Boolean connected = false;
    private static Object connectLock = new Object();
    private static command com = command.NONE;
    private static Object commandLock = new Object();
    private static ArrayList send = new ArrayList<String>();
    private static ArrayList received = new ArrayList<String>();
    private static Socket socketClient;
    private static PrintStream os;
    private static Object outputLock = new Object();
    private static DataInputStream is;
    private static Object inputLock = new Object();

    public static void connect() {
        synchronized(commandLock) {
            ThreadCommunicator.com = command.CONNECT;
            commandLock.notifyAll();
        }
    }
    public static void disconnect() {
        synchronized(commandLock) {
            ThreadCommunicator.com = command.DISCONNECT;
            commandLock.notifyAll();
        }
    }
    public static void clearCommand() {
        synchronized(commandLock) {
            ThreadCommunicator.com = command.NONE;
            commandLock.notifyAll();
        }
    }
    public static command getCommand() {
        synchronized(commandLock) {
            return ThreadCommunicator.com;
        }
    }
    public static Object getCommandLock() {
        return ThreadCommunicator.commandLock;
    }

    public static String getHost() {
        return host;
    }
    public static void setHost(String host) {
        ThreadCommunicator.host = host;
    }

    public static int getPort() {
        return port;
    }
    public static void setPort(int port) {
        ThreadCommunicator.port = port;
    }

    public static Boolean connected() {
        return connected;
    }
    public static void setConnected(Boolean connected) {
        synchronized(connectLock) {
            ThreadCommunicator.connected = connected;
            connectLock.notifyAll();
        }
    }
    public static Object getConnectLock() {
        return ThreadCommunicator.connectLock;
    }

    /*public synchronized static ArrayList<String> getSendMessages() {
        return send;
    }*/
    public static ArrayList<String> getAndClearSendMessages() {
        synchronized(outputLock) {
            ArrayList<String> result = new ArrayList<String>(send);
            send.clear();
            return result;
        }
    }
    public static void addSendMessage(String msg) {
        synchronized(outputLock) {
            ThreadCommunicator.send.add(msg);
            ThreadCommunicator.outputLock.notifyAll();
        }
    }
    public static boolean hasNewSendMessages() {
        synchronized(outputLock) {
            return ( ! ThreadCommunicator.send.isEmpty());
        }
    }

    /*public synchronized static ArrayList<String> getReceivedMessages() {
        return received;
    }*/
    public static ArrayList<String> getAndClearReceivedMessages() {
        synchronized(inputLock) {
            ArrayList<String> result = new ArrayList<String>(received);
            received.clear();
            return result;
        }
    }
    public static void addReceivedMessage(String msg) {
        synchronized(inputLock) {
            ThreadCommunicator.received.add(msg);
            ThreadCommunicator.inputLock.notifyAll();
        }
    }
    public static boolean hasNewReceivedMessages() {
        synchronized(inputLock) {
            return ( ! ThreadCommunicator.received.isEmpty());
        }
    }

    public static Socket getSocket() {
        return ThreadCommunicator.socketClient;
    }
    public static void setSocket(Socket socket) {
        ThreadCommunicator.socketClient = socket;
    }
    public static PrintStream getOS() {
        return ThreadCommunicator.os;
    }
    public static void setOS(PrintStream ps) {
        ThreadCommunicator.os = ps;
    }
    public static Object getOutputLock() {
        return ThreadCommunicator.outputLock;
    }
    public static DataInputStream getIS() {
        return ThreadCommunicator.is;
    }
    public static void setIS(DataInputStream dis) {
        ThreadCommunicator.is = dis;
    }
    public static Object getInputLock() {
        return ThreadCommunicator.inputLock;
    }
}
