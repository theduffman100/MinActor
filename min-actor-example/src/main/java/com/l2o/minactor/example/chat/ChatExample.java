package com.l2o.minactor.example.chat;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.l2o.minactor.guice.SystemTimeActorModule;

public class ChatExample {
    public static void main(String[] args) throws Exception {
	Server server = new Server();
	Injector injector = Guice.createInjector(new SystemTimeActorModule());
	
	ServerConnector pubConnector = new ServerConnector(server);
	pubConnector.setPort(8080);
	server.addConnector(pubConnector);
	
        ResourceHandler resHandler = new ResourceHandler();
        resHandler.setBaseResource(Resource.newResource(ChatExample.class.getResource(".")));
        resHandler.setWelcomeFiles(new String[] { "main-page.html" });
       
        HandlerList handlers = new HandlerList();
        handlers.addHandler(injector.getInstance(ChatWebSocketHandler.class));
        handlers.addHandler(resHandler);
        handlers.addHandler(new DefaultHandler());
        
        server.setHandler(handlers);
        
        server.start();
	server.join();
    }
}
