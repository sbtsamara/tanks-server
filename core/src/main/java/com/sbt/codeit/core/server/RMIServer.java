package com.sbt.codeit.core.server;

import com.sbt.codeit.core.control.GameController;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by Roman on 08.02.2017.
 */
public class RMIServer {

    private static final int PORT = 2017;
    private static final String STUB_NAME = "GameController";
    private static GameController controller;

    public RMIServer(GameController controller) {
        this.controller = controller;
    }

    public void start() {
        try {
            GameController stub = (GameController) UnicastRemoteObject.exportObject(controller, 0);
            Registry registry = LocateRegistry.createRegistry(PORT);
            registry.bind(STUB_NAME, stub);
        } catch (RemoteException | AlreadyBoundException e) {
            e.printStackTrace();
        }
    }

}