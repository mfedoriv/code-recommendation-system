package org.suai.handler;

import org.suai.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class StatusHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (PrintWriter out = resp.getWriter()) {
            out.print("<html>" +
                    "<head><style type=\"text/css\">" + Utils.getDataFromFile("src/main/resources/style.css") +
                    "</style></head><body>" +
                    "<h2>Status of server: <b>Running</b></h2>" +
                    "</body></html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
