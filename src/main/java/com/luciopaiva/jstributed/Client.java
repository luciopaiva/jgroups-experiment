package com.luciopaiva.jstributed;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client extends ReceiverAdapter {

    private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private JChannel channel;
    private ClientMessage reusableClientMessage = new ClientMessage("", "");

    private Client(String userName) throws Exception {
        this.reusableClientMessage.setUserName(userName);
        this.run();
    }

    private void broadcast(String payload) throws Exception {
        reusableClientMessage.setMessage(payload);
        Message message = new Message(null, reusableClientMessage);
        channel.send(message);
    }

    public void viewAccepted(View view) {
        System.out.println("$ view: " + view);
    }

    public void receive(Message message) {
        try {
            ClientMessage clientMessage = message.getObject();
            System.out.println();
            System.out.println(String.format("< %s says: %s", clientMessage.getUserName(), clientMessage.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run() throws Exception {
        channel = new JChannel();
        channel.setReceiver(this);
        channel.connect("cluster");
        System.out.println("Members online: " + channel.getView().getMembers().size());

        while (true) {
            System.out.print("> ");
            System.out.flush();
            String line = in.readLine().trim();

            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("quit")) {
                break;
            }

            broadcast(line);
        }

        channel.close();
    }

    public static void main(String ...args) throws Exception {
        if (args.length < 1) {
            System.out.println("Please specify user name:");
            System.out.println("\tjstributed <user-name>");
            System.exit(1);
        }
        String userName = args[0];

        new Client(userName);
    }
}
