package org.suai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRedirectExample {

    public static void main(String[] args) throws IOException {

        URL resourceUrl, base, next;
        Map<String, Integer> visited;
        HttpURLConnection conn;
        String location;
        int times;
        String url = "http://www.cplusplus.com/strcmp";
        visited = new HashMap<>();

        while (true)
        {
            times = visited.compute(url, (key, count) -> count == null ? 1 : count + 1);

            if (times > 3)
                throw new IOException("Stuck in redirect loop");

            resourceUrl = new URL(url);
            conn = (HttpURLConnection) resourceUrl.openConnection();

            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setInstanceFollowRedirects(false);   // Make the logic below easier to detect redirections
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36");

            switch (conn.getResponseCode())
            {
                case HttpURLConnection.HTTP_MOVED_PERM:
                case HttpURLConnection.HTTP_MOVED_TEMP:
                    location = conn.getHeaderField("Location");
                    location = URLDecoder.decode(location, "UTF-8");
                    base     = new URL(url);
                    next     = new URL(base, location);  // Deal with relative URLs
                    url      = next.toExternalForm();
                    continue;
            }
            break;
        }
    }

}

