package com.example.before.rmi;

import java.io.IOException;

import lipermi.handler.CallHandler;
import lipermi.net.Client;

public class RmiClient {
    private static Client client = null;
    private static CallHandler callHandler = new CallHandler();
    static {

        String remoteHost = "106.58.209.157";
        int port = 7777;
        try {
            client = new Client(remoteHost, port, callHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Client getClient(){
        return client;
    }

    public static void main(String[] args){
        RmiClient rmiClient = new RmiClient();
        PictureRmi pictureRmi = (PictureRmi) rmiClient.getClient().getGlobal(PictureRmi.class);
        //pictureRmi.uploadPicutre()

    }
}
