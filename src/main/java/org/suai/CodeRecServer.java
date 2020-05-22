package org.suai;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.suai.handler.GetCodeHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.suai.handler.SearchHandler;
import org.suai.handler.StatusHandler;

public class CodeRecServer {
    public static void main(String[] args) {

        Server server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler(server, "/");
        handler.addServlet(GetCodeHandler.class, "/getcode");
        handler.addServlet(StatusHandler.class, "/status");
        handler.addServlet(SearchHandler.class, "/search");

        server.setHandler(handler);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
