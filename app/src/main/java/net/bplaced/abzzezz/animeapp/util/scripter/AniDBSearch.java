/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:05
 */

package net.bplaced.abzzezz.animeapp.util.scripter;

import ga.abzzezz.util.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;


public class AniDBSearch {

    public final String getShowDetails(final String search) {
        final StringBuilder builder = new StringBuilder(getDataBase());
        final int start = builder.indexOf(search);
        return builder.substring(start, builder.indexOf("}", start) + 1);
    }

    public final String getDataBase() {
        try {
            final HttpURLConnection urlConnection = createConnection(StringHandler.DATABASE);
            urlConnection.connect();
            return new BufferedReader(new InputStreamReader(urlConnection.getInputStream())).lines().collect(Collectors.joining());
        } catch (final Exception e) {
            Logger.log("Timeout/MalformedURL/IOException identified. Requesting Backup database", Logger.LogType.INFO);
            try {
                final HttpURLConnection urlConnection = createConnection(StringHandler.BACKUP_DATABASE);
                urlConnection.connect();
                return new BufferedReader(new InputStreamReader(urlConnection.getInputStream())).lines().collect(Collectors.joining());
            } catch (final IOException ioException) {
                Logger.log("Excception thrown while requesting backup database. Return is null", Logger.LogType.WARNING);
                return "";
            }
        }
    }

    private HttpURLConnection createConnection(final String url) throws IOException {
        final HttpURLConnection urlConnection = (HttpURLConnection) new URL(StringHandler.DATABASE).openConnection();
        urlConnection.addRequestProperty("User-Agent", StringHandler.USER_AGENT);
        urlConnection.setConnectTimeout(4000);
        return urlConnection;
    }
}
