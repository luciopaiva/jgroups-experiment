package com.luciopaiva.jstributed;

import java.io.Serializable;

class ClientMessage implements Serializable {
    private String userName;
    private String message;

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

    void setUserName(String userName) {
        this.userName = userName;
    }

    void setMessage(String message) {
        this.message = message;
    }
}
