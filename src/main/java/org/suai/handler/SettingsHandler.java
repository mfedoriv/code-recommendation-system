package org.suai.handler;

import org.suai.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class SettingsHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        //To save old token
        String token = null;
        Preferences prefs = Preferences.userRoot().node("CodeRecSystem");
        token = prefs.get("token", "");

        if (!parameterMap.isEmpty()) { // when save settings
            try {
                prefs.clear();
            } catch (BackingStoreException e) {
                e.printStackTrace();
            }
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String parameter = entry.getKey();
                String value = entry.getValue()[0];
                if (parameter.equals("token")) { // prevent erasing token when send empty input
                    if (token != null && !token.equals(value) && !value.equals("")) {
                        prefs.put(parameter, value);
                        System.out.println("Token StackOverflow was changed from " + token + " to " + value + ".");
                    } else {
                        prefs.put(parameter, token);
                    }
                } else {
                    boolean val = value.equals("true");
                    prefs.putBoolean(parameter, val);
                }
            }
        }
        try (PrintWriter out = resp.getWriter()) {
            resp.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            resp.setContentType("charset=UTF-8");
            BufferedReader br  = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/Settings.html")));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("STYLE")) {
                    out.print(Utils.getDataFromFile("/style.css"));
                }
                if (line.contains("CHECKED_cplusplus")){
                    line = prefs.getBoolean("ParserCplusplus_enabled", false)
                            ? line.replace("CHECKED_cplusplus", "checked")
                            : line.replace("CHECKED_cplusplus", "");
                }
                if (line.contains("CHECKED_cppreference")){
                    line = prefs.getBoolean("ParserCppreference_enabled", false)
                            ? line.replace("CHECKED_cppreference", "checked")
                            : line.replace("CHECKED_cppreference", "");
                }
                if (line.contains("CHECKED_stackoverflow")){
                    line = prefs.getBoolean("ParserStackoverflow_enabled", false)
                            ? line.replace("CHECKED_stackoverflow", "checked")
                            : line.replace("CHECKED_stackoverflow", "");
                }
                if (line.contains("CHECKED_searchcode")){
                    line = prefs.getBoolean("ParserSearchcode_enabled", false)
                            ? line.replace("CHECKED_searchcode", "checked")
                            : line.replace("CHECKED_searchcode", "");
                }
                if (line.contains("YOUR_TOKEN")){
                    token = prefs.get("token", "");
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
