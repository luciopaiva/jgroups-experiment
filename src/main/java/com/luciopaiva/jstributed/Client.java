package com.luciopaiva.jstributed;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public class Client extends ReceiverAdapter {

    private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private JChannel channel;
    private ClientMessage reusableClientMessage = new ClientMessage("-", "-");
    private Message message = new Message(null, reusableClientMessage);
    private Set<Address> previousNodes = new HashSet<>();
    private Set<Address> currentNodes = new HashSet<>();
    private Set<Address> nodesLeft = new HashSet<>();
    private Set<Address> nodesJoined = new HashSet<>();

    private Client(String userName) throws Exception {
        this.reusableClientMessage.setUserName(userName);
        this.run();
    }

    private void broadcast(String payload) throws Exception {
        reusableClientMessage.setMessage(payload);
        // needs to be called to trigger serialization process
        message.setObject(reusableClientMessage);
        channel.send(message);
    }

    public void viewAccepted(View view) {
        Set<Address> temp = previousNodes;
        previousNodes = currentNodes;
        currentNodes = temp;

        System.out.println("Received new view " + view.getViewId());
        if (currentNodes != null) {
            currentNodes.clear();
            currentNodes.addAll(view.getMembers());

            StringJoiner membersJoined = new StringJoiner(", ");
            StringJoiner membersLeft = new StringJoiner(", ");

            Utils.distinctInCollections(previousNodes, currentNodes, nodesLeft, nodesJoined);
            nodesLeft.forEach(node -> membersLeft.add(node.toString()));
            nodesJoined.forEach(node -> membersJoined.add(node.toString()));

            if (!nodesJoined.isEmpty()) {
                System.out.println("New members have joined: " + membersJoined.toString());
            }
            if (!nodesLeft.isEmpty()) {
                System.out.println("Some members have left: " + membersLeft.toString());
            }
        }
        prompt();
    }

    public void receive(Message message) {
        try {
            ClientMessage clientMessage = message.getObject();
            System.out.println();
            System.out.println(String.format("< %s says: %s", clientMessage.getUserName(), clientMessage.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        prompt();
    }

    private void prompt() {
        System.out.println("Type message to send:");
    }

    private void run() throws Exception {
        channel = new JChannel();
        channel.setDiscardOwnMessages(true);
        channel.setReceiver(this);
        channel.connect("cluster");
        System.out.println("Members online: " + channel.getView().getMembers().size());

        while (true) {
            prompt();
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
