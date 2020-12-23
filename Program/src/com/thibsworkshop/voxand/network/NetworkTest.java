package com.thibsworkshop.voxand.network;


import com.thibsworkshop.voxand.network.client.Client;
import com.thibsworkshop.voxand.network.server.Server;
import com.thibsworkshop.voxand.network.server.ServerConfig;

public class NetworkTest {

    public static void main(String[] args) {
        new NetworkTest().run();
    }

    public void run(){
        ServerConfig serverConfig = new ServerConfig(16,45545);
        Server server = new Server(serverConfig);
        server.start();

        Thread cli1 = new Thread(new Client("Cysboy", 1000));
        Thread cli2 = new Thread(new Client("John-John", 1000));

        cli1.start();
        cli2.start();
    }
}
