package org.suai.handler;

import org.json.JSONArray;
import org.suai.Utils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class StatusHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        try (PrintWriter out = resp.getWriter()) {
            resp.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            resp.setContentType("charset=UTF-8");
            BufferedReader br  = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/Status.html")));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("STYLE")) {
                    out.print(Utils.getDataFromFile("/style.css"));
                }
                out.println(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
