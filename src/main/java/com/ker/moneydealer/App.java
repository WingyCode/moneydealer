package com.ker.moneydealer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class App
{
    public static void main( String[] args ) throws Exception {
        Server server = new Server(7777);
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        handler.setContextPath("/");

        server.setHandler(handler);
        ServletHolder servlet = handler.addServlet(ServletContainer.class, "/api/*");
//        servlet.setInitOrder(1);
        servlet.setInitParameter("jersey.config.server.provider.packages", "com.ker.moneydealer.Api");

        try {
            server.start();
            server.join();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            server.destroy();
        }
    }
    
}
