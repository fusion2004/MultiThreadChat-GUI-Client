package org.fusion2004.multithreadchat.client.gui;

import java.io.*;
import java.net.*;

/**
 *
 * @author Fusion2004
 */
public class NetworkConnectionThread implements Runnable{
    public void run() {
	while(true) {
            synchronized(ThreadCommunicator.getCommandLock()) {
                try {
                    ThreadCommunicator.getCommandLock().wait();
                } catch(InterruptedException e) {}
                switch(ThreadCommunicator.getCommand()) {
                    case CONNECT:
                        try {
                            ThreadCommunicator.setSocket(new Socket(ThreadCommunicator.getHost(), ThreadCommunicator.getPort()));
                            ThreadCommunicator.setOS(new PrintStream(ThreadCommunicator.getSocket().getOutputStream()));
                            ThreadCommunicator.setIS(new DataInputStream(ThreadCommunicator.getSocket().getInputStream()));
                        } catch (UnknownHostException e) {
                            ThreadCommunicator.addReceivedMessage("* Unknown host "+ThreadCommunicator.getHost());
                            break;
                        } catch (IOException e) {
                            ThreadCommunicator.addReceivedMessage("* Couldn't get I/O for the connection to the host "+ThreadCommunicator.getHost());
                            break;
                        }

                        ThreadCommunicator.setConnected(true);
                        ThreadCommunicator.clearCommand();
                        System.out.println("CONNECTED");
                        break;
                    case DISCONNECT:
                        ThreadCommunicator.setConnected(false);
                        try {
                            ThreadCommunicator.getOS().close();
                            ThreadCommunicator.getIS().close();
                            ThreadCommunicator.getSocket().close();
                        } catch(IOException e) {

                        }
                        ThreadCommunicator.clearCommand();
                        break;

                    case NONE:
                        break;
                }
            }
        }
    }
}
