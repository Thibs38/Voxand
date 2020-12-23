package com.thibsworkshop.voxand.network.server;

import com.thibsworkshop.voxand.network.client.Client;

import java.io.IOException;
import java.net.*;

public class Server{

    public enum ServerError{ OK, TIMEOUT, ALREADY_CONNECTED}

    private ServerConfig config;

    private SClient[] clients;

    private ServerSocket server;
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
        clients = new SClient[config.MAX_PLAYER];



        //System.out.println("Started server: " + server.getInetAddress().getHostAddress() + ":" + port);
    }

    public void start(){
        if(started){
            System.err.println("[LOW ERROR] Can't start server: server already started");
            return;
        }
        //New thread to not block everything else
        Thread t = new Thread(new Runnable(){
            public void run(){
                try {

                    //Creation of server connexion on the selected port
                    DatagramSocket server = new DatagramSocket(config.PORT);

                    while(isRunning){

                        //Packet object
                        byte[] buffer = new byte[1472];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                        //Fetching the datagram from the client
                        //Blocking the thread until it received something
                        server.receive(packet);

                        //When received, we print it
                        String str = new String(packet.getData());
                        System.out.println("Reçu de la part de " + packet.getAddress()
                                + " sur le port " + packet.getPort() + " : ");
                        System.out.println(str);

                        //Reinitializing the packet length for future receptions
                        packet.setLength(buffer.length);

                        //Answer to the client
                        byte[] buffer2 = new String("Réponse du serveur à " + str + "! ").getBytes();
                        DatagramPacket packet2 = new DatagramPacket(
                                buffer2,             //Data
                                buffer2.length,      //Data size
                                packet.getAddress(), //Sender adress
                                packet.getPort()     //Sender port
                        );

                        //Send the datagram to the previous sender
                        server.send(packet2);
                        packet2.setLength(buffer2.length);
                    }
                } catch (IOException e) {
                    System.err.println("[HIGH ERROR] Connexion lost");
                    e.printStackTrace();
                }

            }
        });

        t.start();
    }

    public void close(){
        isRunning = false;
    }
}
