package org.fusion2004.multithreadchat.client.gui;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class MultiThreadChatClient extends javax.swing.JFrame {

    static String lcOSName;
    static boolean IS_MAC;

    private static javax.swing.JMenuItem aboutMenuItem;
    private static javax.swing.JTextArea chatConsole;
    private static javax.swing.JMenu editMenu;
    private static javax.swing.JMenu fileMenu;
    private static javax.swing.JMenu helpMenu;
    private static javax.swing.JScrollPane jScrollPane1;
    private static javax.swing.JMenuBar menuBar;
    private static javax.swing.JButton sendButton;
    private static javax.swing.JTextField sendText;

    public MultiThreadChatClient() {
        initComponents();
    }
    
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        chatConsole = new javax.swing.JTextArea();
        sendText = new javax.swing.JTextField();
        sendButton = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        editMenu = new javax.swing.JMenu();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MultiThreadChat GUI Client");
        setMinimumSize(new java.awt.Dimension(471, 349));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        chatConsole.setColumns(20);
        chatConsole.setEditable(false);
        chatConsole.setFont(new java.awt.Font("Monospaced", 0, 12));
        chatConsole.setLineWrap(true);
        chatConsole.setRows(5);
        jScrollPane1.setViewportView(chatConsole);

        sendText.setFont(new java.awt.Font("Monospaced", 0, 12));
        sendText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                sendTextKeyPressed(evt);
            }
        });

        sendButton.setText("Send");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        fileMenu.setText("File");
        menuBar.add(fileMenu);

        editMenu.setText("Edit");
        menuBar.add(editMenu);

        helpMenu.setText("Help");

        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(sendText, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sendText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendButton))
                .addContainerGap())
        );

        pack();
    }
    
    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if( ! sendText.getText().equals("")) {
            ThreadCommunicator.addSendMessage(sendText.getText());
            sendText.setText("");
        }
        //sendText.grabFocus();
        sendText.requestFocusInWindow();
    }

    private void sendTextKeyPressed(java.awt.event.KeyEvent evt) {
        if(evt.getKeyCode() == evt.VK_ENTER)
            sendButtonActionPerformed(null);
    }

    public static void main(String[] args) {
        lcOSName = System.getProperty("os.name").toLowerCase();
        IS_MAC = lcOSName.startsWith("mac os x");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MultiThreadChatClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MultiThreadChatClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MultiThreadChatClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MultiThreadChatClient.class.getName()).log(Level.SEVERE, null, ex);
        }

	final Thread networkConnection = new Thread(new NetworkConnectionThread());
        final Thread networkSend = new Thread(new NetworkSendThread());
        final Thread networkReceive = new Thread(new NetworkReceiveThread());
        final Thread receivedPolling = new Thread(new Runnable() {
            private ArrayList<String> msgs;
            public void run() {
                while(true) {
                    synchronized(ThreadCommunicator.getInputLock()) {
                        try {
                            ThreadCommunicator.getInputLock().wait();
                        } catch(InterruptedException e) {}
                        if(ThreadCommunicator.hasNewReceivedMessages()) {
                            msgs = ThreadCommunicator.getAndClearReceivedMessages();
                            for(String s : msgs)
                                chatConsole.append("\n"+s);
                            chatConsole.setCaretPosition(chatConsole.getDocument().getLength());
                        }
                    }
                }
            }
        });

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MultiThreadChatClient().setVisible(true);

                networkSend.start();
                networkReceive.start();
                receivedPolling.start();
                networkConnection.start();

                chatConsole.append("Welcome to the MultiThreadChat GUI client!");
                String s;
                s = JOptionPane.showInputDialog("Server:", "localhost");
                ThreadCommunicator.setHost(s);
                s = JOptionPane.showInputDialog("Port:", "7777");
                ThreadCommunicator.setPort(Integer.parseInt(s));
                
                chatConsole.append("\nConnecting to "+ThreadCommunicator.getHost()+
                        ":"+ThreadCommunicator.getPort()+"...");

                sendText.requestFocusInWindow();

                ThreadCommunicator.connect();
            }
        });

        

        /*java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                chatConsole.append("Welcome to the MultiThreadChat GUI client!");
            }
        });
        
        Thread initialConnect = new Thread(new Runnable(){
            public void run() {
                ThreadCommunicator.connect();
            }
        });
        initialConnect.start();*/

    }

    
}