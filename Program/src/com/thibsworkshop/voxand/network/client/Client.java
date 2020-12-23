package com.thibsworkshop.voxand.network.client;

import com.thibsworkshop.voxand.network.UUIDUtils;
import com.thibsworkshop.voxand.network.server.Server;
import com.thibsworkshop.voxand.network.server.Server.ServerError;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.UUID;

public class Client implements Runnable{

    enum DataType{
        CONNEXION((short)0),
        COMMAND((short)1);

        private short v;
        DataType(short var){
            v = var;
        }
    }

    private UUID uuid;
    public String name = "";
    private long sleepTime = 1000;

    private String host = "127.0.0.1";
    private int port = 45545;

    private InetAddress adress;
    private DatagramSocket socket;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;

    private boolean created;

    private ByteBuffer sendBuffer;
    private int sendBufferPos = 0;
    private ByteBuffer receiveBuffer;
    private int receiveBufferPos = 0;

    private int bufferLength = 1472;

    public Client(String pName, long sleep){
        name = pName;
        sleepTime = sleep;
        uuid = UUID.randomUUID();

        sendBuffer = ByteBuffer.allocate(bufferLength);
        receiveBuffer = ByteBuffer.allocate(bufferLength);

        create();
    }

    public boolean create() { //TODO: Initialize packet here and socket, but don't connect yet
        created = true;
        if(socket != null){
            try {
                socket = new DatagramSocket(); //Creating a new socket
            } catch (SocketException e) {
                e.printStackTrace();
                created = false;
            }
        }

        try {
            adress = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            System.err.println("[NETWORK ERROR] Can't resolve hostname");
            e.printStackTrace();
            created = false;
        }
        if(sendPacket == null)
            sendPacket = new DatagramPacket(sendBuffer.array(), bufferLength, adress, port);
        if(receivePacket == null)
            receivePacket = new DatagramPacket(receiveBuffer.array(), bufferLength, adress, port);

        if(!created)
            System.err.println("[NETWORK ERROR] Couldn't connect to server");

        return created;
    }

    public void run(){
        int nbre = 0;

        while(true){
            String envoi = name + "-" + (++nbre);

            try {

                //On envoie au serveur
                socket.send(packet);

                //Et on récupère la réponse du serveur
                byte[] buffer2 = new byte[8196];
                DatagramPacket packet2 = new DatagramPacket(buffer2, buffer2.length, adresse, port);
                socket.receive(packet2);
                System.out.println(envoi + " a reçu une réponse du serveur : ");
                System.out.println(new String(packet2.getData()));

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void send(DataType dataType) throws SocketException, UnknownHostException {
        if(!created){
            if(!create()) return;
        } // If not created we create the client socket, if the creation fails then we leave.

        ServerError error = ServerError.OK;
        sendBuffer.clear(); //Clearing the data before filling it and then sending it
        switch (dataType){
            case CONNEXION:
                error = connect();
                break;
            case COMMAND:
                break;
            default:
                System.err.println("[NETWORK ERROR] Unknown DataType command, can't send data to the server");
                break;
        }

        switch (error){
            case OK: break;
            case ALREADY_CONNECTED:
                System.err.println("[NETWORK ERROR] Can't connect to server: already connected");
                break;
            case TIMEOUT:
                System.err.println("[NETWORK ERROR] Timed out from the server");
                break;
            default:
                System.err.println("[NETWORK ERROR] Unknown error, what happened?");
                break;
        }
    }

    private ServerError connect() throws SocketException, UnknownHostException {

        sendBuffer.putShort(DataType.CONNEXION.v);
        System.arraycopy(UUIDUtils.asBytes(uuid),0,sendBuffer,0,16);
        System.arraycopy(name.getBytes(),0,sendBuffer,16,name.length());


        return ServerError.OK;
    }

    int addToBuffer
}


