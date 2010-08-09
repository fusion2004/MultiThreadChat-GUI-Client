package org.fusion2004.multithreadchat.client;

import org.fusion2004.multithreadchat.client.gui.MultiThreadChatClient;

/**
 *
 * @author fusion2004
 */
public class GUILauncher {
    public static void main(final String[] args) {
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "MultiThreadChat Client");

        Thread runApp = new Thread(new Runnable() {
            public void run() {
                MultiThreadChatClient.main(args);
            }
        });
        runApp.start();
    }
}
