package com.thibsworkshop.voxand.network.server;

import com.thibsworkshop.voxand.network.Network;
import com.thibsworkshop.voxand.network.Network.ClientDataType;
import com.thibsworkshop.voxand.network.Network.ServerDataType;
import com.thibsworkshop.voxand.network.Network.ServerError;
import com.thibsworkshop.voxand.network.UUIDUtils;


import java.io.IOException;
import java.net.*;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.UUID;

import

import javax.xml.crypto.Data;

public class Server{

    private ServerConfig config;

    private final HashMap<InetAddress,SClient> clients;

    private boolean isRunning = true;
    private boolean started = false;


    /* The server has three components:
     *     • The getter, which collect data coming from the clients
     *     • The tickloop, which update the game state 20 times per second using the last info sent by the client
     *     • The sender, which send the data to the clients when the tick loop is done.
     *
     * The getter is on another Thread to receive data asynchronously from the server loop. It updates the inputs of the clients.
     * The loop is called 20 times per seconds, it starts by applying the inputs of the client, then updating the whole world
     * and finally sending the new data to the client asynchronously.
     *
     * There are a total of 2 + n threads with n being the number of clients, one for getting data, one for the loop, and one for
     * each client to send them data.
     *
     * A data stream always starts with 2 bytes (short) representing the type of data sent
     */

    public Server(ServerConfig config){
        this.config = config;
        clients = new HashMap<>();
    }


    public void start(){
        ByteBuffer receiveBuffer;
        DatagramPacket receivePacket;
        DatagramSocket socket;

        if(started){ //If the server is already started we won't start it again
            System.err.println("[SERVER ERROR] Couldn't start server: server already started");
            return;
        }

        try {
            socket = new DatagramSocket(config.PORT); //Creating a new socket
        } catch (SocketException e) {
            System.err.println("[SERVER ERROR] Couldn't start server: port not available");
            return;
        }

        receiveBuffer = ByteBuffer.allocate(Network.PACKET_LENGTH);
        receivePacket = new DatagramPacket(receiveBuffer.array(), Network.PACKET_LENGTH);


        //New thread to not block everything else
        Thread t = new Thread(new Runnable(){
            public void run() {

                started = true;

                while (isRunning) {

                    //Fetching the datagram from the client
                    //Blocking the thread until it received something
                    try {
                        socket.receive(receivePacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }

                    //Here we are defining a protocol:
                    //First we get the client
                    SClient client = clients.get(receivePacket.getAddress());
                    ClientDataType dataType; //The command sent by the client

                    //If it is null, then we add it to the list, and then try to create it
                    if(client == null) {
                        createClient(receivePacket.getAddress(), receivePacket.getPort());
                    }

                    //If the packet is less than 2 bytes long, then we send an error to the client
                    if(receivePacket.getLength() < 2) {
                        sendError(ServerError.TOO_SMALL_PACKET, client);
                    }

                    dataType = ClientDataType.values[receiveBuffer.getShort()];

                        //TODO: make a timeout code to disconnect client not answering for x seconds
                        //TODO: fill connexion code, add switch to determine what to do with the info.

                    process(dataType,receiveBuffer, client);
                }
            }
        });

        t.start();
    }

    private void process(ClientDataType dataType, ByteBuffer buffer, SClient client){

        ServerError error = ServerError.OK;
        switch (dataType){

            case CONNEXION -> {
                error = connectClient(buffer, client);
            }
            case COMMAND -> {

            }
            default -> {
                error = ServerError.UNKNOWN_COMMAND;
            }
        }

        switch (error) {
            case OK -> {

            }
            case UNKNOWN_COMMAND, CORRUPTED_DATA, UNKNOWN, NOT_CONNECTED -> {
                //For these cases, we will always use the sendError pattern
                sendError(error,dataType,client);
            }
            case ALREADY_CONNECTED -> {
            }
            case TIMEOUT -> {
            }
            case TOO_SMALL_PACKET -> {
            }
            case CONNEXION_REFUSED -> {
            }
            default -> {
                //TODO: there's an error in the error management code
            }
        }
    }

    private ServerError connectClient(ByteBuffer receiveBuffer, SClient client){
        //To connect: retrieve UUID, retrieve Name, fill SClient with them
        //Send to the client that he is connected

        //--- Getting uuid ---//

        byte[] uuidBuffer = new byte[16];

        if(!readBuffer(receiveBuffer,uuidBuffer)){
            return ServerError.CORRUPTED_DATA;
        }
        UUID uuid = UUIDUtils.asUuid(uuidBuffer);

        //--- Getting name ---//

        byte[] nameBuffer = new byte[Network.NAME_LENGTH];

        if(!readBuffer(receiveBuffer,nameBuffer)){
            return ServerError.CORRUPTED_DATA;
        }
        String name = Network.charset.decode(ByteBuffer.wrap(nameBuffer)).toString();

        //--- Creating client ---//

        client.connect(uuid, name);

        //--- Sending connexion validation to client ---//

        DatagramPacket sendPacket = client.getSendPacket();
        ByteBuffer sendBuffer = client.getBuffer();
        sendBuffer.putShort(ServerDataType.CONNEXION.v);


        return ServerError.OK;
    }

    private void createClient(InetAddress address, int port) {
        clients.put(address, new SClient(address, port));
    }


    private void sendError(DatagramSocket socket, ServerError error, ClientDataType dataType, SClient client){
        //To send an error: we get the buffer and the packet of the client
        //We fill it with the server data type error, followed by the error and the command the client tried to use

        DatagramPacket sendPacket = client.getSendPacket();
        ByteBuffer buffer = client.getBuffer();
        buffer.putShort(ServerDataType.ERROR.v);
        buffer.putShort(error.v);
        buffer.putShort(dataType.v);

        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: client might be unreachable, try
        }
    }

    private boolean readBuffer(ByteBuffer buffer, byte[] array){
        try{
            buffer.get(array);
        }catch (BufferUnderflowException e){
            return false;
        }
        return true;
    }

    public void close(){

        isRunning = false;
        started = false;
    }

    public boolean isStarted(){ return started; }
}
