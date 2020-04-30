package org.suai.parser;

import org.suai.Example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public interface Parser {
    public Example findExample(String funcName) throws ParseException;

    public default String getResponse(String urlString, boolean isCompressed) throws ParseException {
        HttpURLConnection connection = null;
        BufferedReader reader;
        String line;
        StringBuilder responseContent = new StringBuilder();
        try {
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
                responseContent.append(line);
            }
            reader.close();
        } catch (IOException e) {
            throw new ParseException(e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return responseContent.toString();
    }
}
