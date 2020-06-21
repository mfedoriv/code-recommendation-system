package org.suai;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.suai.handler.GetCodeHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.suai.handler.SearchHandler;
import org.suai.handler.SettingsHandler;
import org.suai.handler.StatusHandler;

import java.util.prefs.Preferences;

public class CodeRecServer {

    public static final String ANSI_RESET = "\u001B[0m";

    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public static void main(String[] args) {

        Server server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler(server, "/");
        handler.addServlet(GetCodeHandler.class, "/getcode");
        handler.addServlet(StatusHandler.class, "/status");
        handler.addServlet(SearchHandler.class, "/search");
        handler.addServlet(SettingsHandler.class, "/settings");

        server.setHandler(handler);

        // run all parsers when start
        Preferences prefs = Preferences.userRoot().node("CodeRecSystem");
        prefs.putBoolean("ParserCplusplus_enabled", true);
        prefs.putBoolean("ParserCppreference_enabled", true);
        prefs.putBoolean("ParserStackoverflow_enabled", true);
        prefs.putBoolean("ParserSearchcode_enabled", true);

        try {
            server.start();
            System.out.println("\n" + ANSI_BLUE + "CODE RECOMMENDATION SYSTEM" + ANSI_RESET);
            System.out.println("Sublime Text 3 plugin which allows you to get examples of using C functions " +
                    "with one press of the shortcut.");
            System.out.println(ANSI_GREEN + "The server is running!" + ANSI_RESET);
            System.out.println("You can customize your search by going to " + ANSI_CYAN +
                    "http://localhost:8080/settings" + ANSI_RESET);
            System.out.println("To check status of server go to " + ANSI_CYAN +
                    "http://localhost:8080/status" + ANSI_RESET);
            System.out.println("To search from browser go to " + ANSI_CYAN +
                    "http://localhost:8080/search" + ANSI_RESET);
            System.out.println("\nSERVER LOG\n" +
                    "_______________________________________________________________________________");
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
