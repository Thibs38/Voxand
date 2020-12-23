package com.thibsworkshop.voxand.network.server;

import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

import java.net.InetAddress;
import java.util.UUID;

public class SClient {
    public String name;
    public InetAddress address;
    public UUID uuid;
    private Vector3f position;

    public SClient(String name, UUID uuid, InetAddress address){
        this.name = name;
        this.address = address;
        this.uuid = uuid;

        this.position = new Vector3f();
    }

    public void update(byte[] data){

    }
}
