package com.luciopaiva.jstributed;

import java.io.Serializable;

class ClientMessage implements Serializable {
    private final String userName;
    private final String message;

    ClientMessage(String userName, String message) {
        this.userName = userName;
        this.message = message;
    }

    String getUserName() {
        return userName;
    }

    String getMessage() {
        return message;
    }
}
