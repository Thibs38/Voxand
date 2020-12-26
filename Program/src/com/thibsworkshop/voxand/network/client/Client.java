package com.thibsworkshop.voxand.network.client;

import com.thibsworkshop.voxand.network.Network;
import com.thibsworkshop.voxand.network.UUIDUtils;
import com.thibsworkshop.voxand.network.Network.ServerError;
import com.thibsworkshop.voxand.network.Network.ClientDataType;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetEncoder;
import java.util.UUID;

public class Client implements Runnable{

    private UUID uuid;
    public String name = "";
    private long sleepTime = 1000;

    private String host = "127.0.0.1";
    private int port = 45545;

    private InetAddress address;
    private DatagramSocket socket;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;

    private boolean connected;

    private ByteBuffer sendBuffer;
    private ByteBuffer receiveBuffer;



    public Client(String pName, long sleep){
        if(pName.getBytes().length > Network.NAME_LENGTH)
        name = pName;
        sleepTime = sleep;
        uuid = UUID.randomUUID();

        sendBuffer = ByteBuffer.allocate(Network.PACKET_LENGTH);
        receiveBuffer = ByteBuffer.allocate(Network.PACKET_LENGTH);

        create();
    }

    public void create() {
        if(socket == null){
            try {
                socket = new DatagramSocket(); //Creating a new socket
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }

    private void send(ClientDataType dataType){

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
            case CANT_RESOLVE_HOSTNAME:
                System.err.println("[NETWORK ERROR] Can't resolve hostname");
                break;
            case TIMEOUT:
                System.err.println("[NETWORK ERROR] Timed out from the server");
                break;
            case UNKNOWN:
            default:
                System.err.println("[NETWORK ERROR] Unknown error, what happened?");
                break;
        }
    }

    private ServerError connect() {
        try {
            address = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return ServerError.CANT_RESOLVE_HOSTNAME;
        }

        sendPacket = new DatagramPacket(sendBuffer.array(), Network.PACKET_LENGTH, address, port);
        receivePacket = new DatagramPacket(receiveBuffer.array(), Network.PACKET_LENGTH, address, port);

        sendBuffer.putShort(ClientDataType.CONNEXION.v); // 2B
        sendBuffer.put(UUIDUtils.asBytes(uuid)); // 16B
        sendBuffer.put(Network.charset.encode(name)); //

        //We send the data
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            return ServerError.UNKNOWN;
        }

        //We wait an answer from the server
        try {
            socket.receive(receivePacket);
        } catch (IOException e) {
            e.printStackTrace();
            return ServerError.UNKNOWN;
        }

        connected = true;
        return ServerError.OK;
    }

}


