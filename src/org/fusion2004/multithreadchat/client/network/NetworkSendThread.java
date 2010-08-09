/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fusion2004.multithreadchat.client.network;

import java.io.*;
import java.util.*;
import org.fusion2004.multithreadchat.client.ThreadCommunicator;

/**
 *
 * @author Fusion2004
 */
public class NetworkSendThread implements Runnable{
    public void run() {
        ArrayList<String> msgsToSend;

        while(true) {
            synchronized(ThreadCommunicator.getOutputLock()) {
                try {
                    ThreadCommunicator.getOutputLock().wait();
                } catch(InterruptedException e) {}
                if(ThreadCommunicator.connected() && ThreadCommunicator.hasNewSendMessages()) {
                    msgsToSend = ThreadCommunicator.getAndClearSendMessages();
                    for(String s : msgsToSend)
                        ThreadCommunicator.getOS().println(s);
                }
            }
        }
    }
}
