package com.github.rccookie.engine2d.online;

/**
 * Type of message between server and client.
 */
public enum MessageType {
    /**
     * Message from the server to all clients.
     */
    SERVER_TO_CLIENT,
    /**
     * Message from one client to all other clients.
     */
    CLIENT_TO_CLIENT,
    /**
     * Message from one client to the server.
     */
    CLIENT_TO_SERVER,
    /**
     * Error message.
     */
    ERROR,
    /**
     * Other / unknown message type.
     */
    OTHER
}
