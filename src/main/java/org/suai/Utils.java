package org.suai;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class Utils {
    public static String escapeHTML(String s) {
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127 || c == '"' || c == '\'' || c == '<' || c == '>' || c == '&') {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                out.append(c);
            }
        }
        return out.toString().replaceAll("\\n", "<br>")
                .replaceAll(" ", "&nbsp;&nbsp;");
    }

    public static String getDataFromFile(String path) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Utils.class.getResourceAsStream(path)))) {
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void writeDataToFile(String path, String data) {
        try (PrintWriter writer = new PrintWriter(new File(Utils.class.getResource(path).getPath()))) {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadWebpage(String urlString, String fileName) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url != null) {
            try(
                    BufferedReader reader =  new BufferedReader(new InputStreamReader(url.openStream()));
                    PrintWriter writer = new PrintWriter(new File(fileName));
            )  {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                }
                System.out.println("Webpage " + url.toString() + " downloaded successfully!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
