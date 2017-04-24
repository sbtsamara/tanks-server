package com.sbt.codeit.bot;

import com.sbt.codeit.core.control.GameController;
import com.sbt.codeit.core.control.ServerListener;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Roman on 08.02.2017.
 */
public class SimpleBot implements ServerListener {

    private static final String HOST = "localhost";
    private static final int PORT = 2017;
    private static final String STUB_NAME = "GameController";
    private static GameController server;
    private static ServerListener client;

    private int i = 0;

    public static void main(String... args) {
        try {
            SimpleBot simpleBot = new SimpleBot();
            Registry registry = LocateRegistry.getRegistry(HOST, PORT);
            server = (GameController) registry.lookup(STUB_NAME);
            client = (ServerListener) UnicastRemoteObject.exportObject(simpleBot, 0);
            server.register(client, createName());
            server.start(client);
            synchronized (simpleBot) {
                simpleBot.wait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void update(ArrayList<ArrayList<Character>> field) {
        try {
            if(i % 5 == 0) {
                randomDirection();
            }
            if(i % 6 == 0) {
                server.fire(client);
            }
            i++;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void randomDirection() throws Exception {
        Random random = new Random();
        switch (random.nextInt(4)) {
            case 0:
                server.up(client);
                break;
            case 1:
                server.down(client);
                break;
            case 2:
                server.left(client);
                break;
            case 3:
                server.right(client);
        }
    }

    private static String createName() {
        Random random = new Random();
        char[] chars = "ABCDEFGHIJKLMNOPQRSTYUWXYZ".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < random.nextInt(10) + 3; i++) {
            stringBuilder.append(chars[random.nextInt(chars.length)]);
        }
        return stringBuilder.toString();
    }
}
