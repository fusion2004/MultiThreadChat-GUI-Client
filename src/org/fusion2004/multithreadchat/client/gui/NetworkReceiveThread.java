/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fusion2004.multithreadchat.client.gui;

import java.io.IOException;

/**
 *
 * @author Fusion2004
 */
public class NetworkReceiveThread implements Runnable{
    public void run() {
	String responseLine;

        while(true) {
            synchronized(ThreadCommunicator.getConnectLock()) {
                try {
                    ThreadCommunicator.getConnectLock().wait();
                } catch(InterruptedException e) {}
            }
            try {
                while(ThreadCommunicator.connected() && (responseLine = ThreadCommunicator.getIS().readLine()) != null) {
                    ThreadCommunicator.addReceivedMessage(responseLine);
                }
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    }
}
