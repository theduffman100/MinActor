package com.l2o.minactor.example.chat;

import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import com.google.inject.Inject;

public class ChatWebSocketHandler extends WebSocketHandler implements WebSocketCreator {
    @Inject
    private ChatRoom chatRoom; 
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.setCreator(this);;
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        return new ChatUserConnection(chatRoom);
    }
}