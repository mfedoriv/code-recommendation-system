package org.suai.parser;

import org.suai.Example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public interface Parser {
    public ArrayList<Example> findExample(String funcName) throws ParseException;

    public default ArrayList<String> getResponse(String urlString, boolean isCompressed) throws ParseException {
        urlString = urlString.replaceAll(" ", "+");
        HttpURLConnection connection = null;
        BufferedReader reader;
        String line;
        ArrayList<String> responseContent = new ArrayList<>();
        try {
            ////
            // Check for redirect
            URL resourceUrl, base, next;
            Map<String, Integer> visited;
            String location;
            int times;
            visited = new HashMap<>();

            while (true)
            {
                // lambda expression. Don't know what it is and what happens there... but it works!
                times = visited.compute(urlString, (key, count) -> count == null ? 1 : count + 1);

                if (times > 3)
                    throw new ParseException("Stuck in redirect loop");

                resourceUrl = new URL(urlString);
                connection = (HttpURLConnection) resourceUrl.openConnection();

                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setInstanceFollowRedirects(false);   // Make the logic below easier to detect redirections
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36");

                switch (connection.getResponseCode())
                {
                    case HttpURLConnection.HTTP_MOVED_PERM:
                    case HttpURLConnection.HTTP_MOVED_TEMP:
                        location = connection.getHeaderField("Location");
                        location = URLDecoder.decode(location, "UTF-8");
                        base = new URL(urlString);
                        next = new URL(base, location);  // Deal with relative URLs
                        urlString = next.toExternalForm();
                        continue;
                }

                break;
            }
            ////
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36");
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            int status = connection.getResponseCode();
            if (status > 299) {
                if (isCompressed) {
                    reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(connection.getErrorStream())));
                } else {
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }
            } else {
                if (isCompressed) {
                    reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(connection.getInputStream())));
                } else {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                }
            }
            while ((line = reader.readLine()) != null) {
                responseContent.add(line);
            }
            reader.close();
        } catch (IOException e) {
            throw new ParseException(e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return responseContent;
    }
}
