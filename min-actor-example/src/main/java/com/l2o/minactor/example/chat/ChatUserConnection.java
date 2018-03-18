package com.l2o.minactor.example.chat;

import java.nio.ByteBuffer;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.api.WriteCallback;

import com.l2o.minactor.Actor;

public class ChatUserConnection implements WebSocketListener, WriteCallback, Actor<String> {
    private Session session;
    private boolean binary;
    private ChatRoom chatRoom;
    public ChatUserConnection(ChatRoom chatRoom) {
	this.chatRoom = chatRoom;
    }
    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        chatRoom.unregister(this);
    }

    @Override
    public void onWebSocketConnect(Session session) {
        this.session = session;
        chatRoom.register(this);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        binary = true;
        chatRoom.sendMessage(this, new String(payload));
    }

    @Override
    public void onWebSocketText(String message) {
        binary = false;
        chatRoom.sendMessage(this, message);
   }

    @Override
    public void writeFailed(Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void writeSuccess() {
    }
    
    @Override
    public void postEvent(String message) {
	if (binary) {
	    session.getRemote().sendBytes(ByteBuffer.wrap(message.getBytes()), this);
	} else {
	    session.getRemote().sendString(message, this);
	}
    }
}