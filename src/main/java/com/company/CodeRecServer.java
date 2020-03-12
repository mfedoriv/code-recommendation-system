package com.company;

import com.company.handler.GetCodeHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

public class CodeRecServer {
    public static void main(String[] args) {

        Server server = new Server(8080);
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(GetCodeHandler.class, "/getcode");
        server.setHandler(handler);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
