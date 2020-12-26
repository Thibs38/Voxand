package com.thibsworkshop.voxand.network.server;

import com.thibsworkshop.voxand.network.Network;
import org.joml.Vector3f;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.UUID;

public class SClient {
    private String name;
    private InetAddress address;
    private int port;
    private UUID uuid;
    private Vector3f position;

    private boolean connected = false;

    private DatagramPacket sendPacket;
    private ByteBuffer sendBuffer;

    public SClient(InetAddress address, int port){
        this.address = address;

        this.position = new Vector3f();

        this.sendBuffer = ByteBuffer.allocate(Network.PACKET_LENGTH);
        this.sendPacket = new DatagramPacket(sendBuffer.array(), Network.PACKET_LENGTH);
    }

    public void connect(UUID uuid, String name){
        this.uuid = uuid;
        this.name = name;

        connected = true;
    }

    public ByteBuffer getBuffer(){
        sendBuffer.clear(); //Before getting the buffer we clear it
        return sendBuffer;
    }

    public DatagramPacket getSendPacket(){ return sendPacket; }

    public boolean isConnected(){ return connected; }
}
