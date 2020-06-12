package org.suai.handler;

import org.eclipse.jetty.util.IO;
import org.json.JSONArray;
import org.suai.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

public class SettingsHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        //To save old token
        String token = null;
        try (FileReader fr = new FileReader("coderec.properties")) {
            Properties oldProperties = new Properties();
            oldProperties.load(fr);
            token = oldProperties.getProperty("token");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Properties properties = null;
        if (!parameterMap.isEmpty()) { // when save settings
            properties = new Properties();

            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String parameter = entry.getKey();
                String value = entry.getValue()[0];
                if (parameter.equals("token")) { // prevent erasing token when send empty input
                    if (token != null && !token.equals(value) && value.equals("")) {
                        value = token;
                    }
                }
                properties.setProperty(parameter, value);
            }
            try (FileWriter fw = new FileWriter("coderec.properties")) {
                properties.store(fw, "Configuration file of Code Recommendation System.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (properties == null) { // when load page without saving settings
            properties = new Properties();
            try (FileReader fr = new FileReader("coderec.properties")) {
                properties.load(fr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (PrintWriter out = resp.getWriter()) {
            resp.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            resp.setContentType("charset=UTF-8");
            BufferedReader br  = new BufferedReader(new FileReader("src/main/resources/Settings.html"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("STYLE")) {
                    out.print(Utils.getDataFromFile("src/main/resources/style.css"));
                }
                if (line.contains("CHECKED_cplusplus")){
                    line = properties.getProperty("ParserCplusplus_enabled", "false").toLowerCase().equals("true")
                            ? line.replace("CHECKED_cplusplus", "checked")
                            : line.replace("CHECKED_cplusplus", "");
                }
                if (line.contains("CHECKED_cppreference")){
                    line = properties.getProperty("ParserCppreference_enabled", "false").toLowerCase().equals("true")
                            ? line.replace("CHECKED_cppreference", "checked")
                            : line.replace("CHECKED_cppreference", "");
                }
                if (line.contains("CHECKED_stackoverflow")){
                    line = properties.getProperty("ParserStackoverflow_enabled", "false").toLowerCase().equals("true")
                            ? line.replace("CHECKED_stackoverflow", "checked")
                            : line.replace("CHECKED_stackoverflow", "");
                }
                if (line.contains("CHECKED_searchcode")){
                    line = properties.getProperty("ParserSearchcode_enabled", "false").toLowerCase().equals("true")
                            ? line.replace("CHECKED_searchcode", "checked")
                            : line.replace("CHECKED_searchcode", "");
                }
                if (line.contains("YOUR_TOKEN")){
                    token = properties.getProperty("token");
                    line = !token.equals("") ? line.replace("YOUR_TOKEN", "<b>" + token + "</b>")
                            : line.replace("YOUR_TOKEN","<p class=\"alert\">You haven't set the token yet.</p>");
                }
                out.println(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        doGet(req, resp);
    }
}
