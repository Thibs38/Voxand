package com.thibsworkshop.voxand.network;


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerError;

import java.util.UUID;

// Class that contains static values about networking
public class Network {

    public static final int PACKET_LENGTH = 1472;
    public static final int NAME_LENGTH = 32;

    public static Charset charset = StandardCharsets.UTF_16;

    /**
     * Enum representing the errors of the server.
     * Each value is a short.
     *<br>
     * {@link #OK}<br>
     * {@link #UNKNOWN}<br>
     * {@link #CANT_RESOLVE_HOSTNAME}<br>
     * {@link #ALREADY_CONNECTED}<br>
     * {@link #TIMEOUT}<br>
     * {@link #TOO_SMALL_PACKET}<br>
     * {@link #UNKNOWN_DATA_TYPE}<br>
     * {@link #CORRUPTED_DATA}<br>
     * {@link #NOT_CONNECTED}<br>
     */
    public enum ServerError{
        /** No errors */
        OK((short)0),
        /** Unknown error */
        UNKNOWN((short)1),
        /** Host name is incorrect */
        CANT_RESOLVE_HOSTNAME((short)2),
        /** Client already connected to server */
        ALREADY_CONNECTED((short)3),
        /** Client haven't answered for too long */
        TIMEOUT((short)4),
        /** Packet sent by the client is less than 2 bytes long, the command can't be properly read */
        TOO_SMALL_PACKET((short)5),
        /** The {@link ClientDataType} sent is unknown */
        UNKNOWN_DATA_TYPE((short)6),
        /** The content sent by the client couldn't be properly read for the given {@link ClientDataType} */
        CORRUPTED_DATA((short)7),
        /** The client is sending packets but his connexion isn't accepted yet by the server */
        NOT_CONNECTED((short)8);


        public static ServerError[] values = ServerError.values();

        public short v;
        ServerError(short var){
            v = var;
        }
    }

    /**
     * Enum representing the data a client can send to the server.
     * Each value is a short that must be the first data in the sent packet.
     * <br>
     * {@link #CONNEXION}<br>
     * {@link #COMMAND}<br>
     */
    public enum ClientDataType {
        /** Send a connexion request to the server, followed by a {@link UUID} and a String representing the name */
        CONNEXION((short)0),
        /** Send a command to the server, followed by the <p style="color:red;">command</p> */
        COMMAND((short)1);

        public static ClientDataType[] values = ClientDataType.values();

        public short v;
        ClientDataType(short var){
            v = var;
        }
    }
    /**
     * Enum representing the data the server can send to a client.
     * Each value is a short that must be the first data in the sent packet.
     *<br>
     * {@link #CONNEXION}<br>
     * {@link #ERROR}<br>
     */
    public enum ServerDataType {
        /** Send the answer of a connexion request, followed by 0 or 1 and a {@link ServerError} (which is OK if connexion is successful) */
        CONNEXION((short)0),
        /** Send an error to a client,  followed by a {@link ServerError} */
        ERROR((short)1);

        public static ServerDataType[] values = ServerDataType.values();

        public short v;
        ServerDataType(short var){
            v = var;
        }
    }


}
