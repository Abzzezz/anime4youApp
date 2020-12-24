/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 16:11
 */

package net.bplaced.abzzezz.animeapp.util.connection;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLUtil {

    public static HttpsURLConnection createHTTPSURLConnection(final String urlIn, final String requestMethod, final String[]... requestProperties) throws IOException {
        final HttpsURLConnection connection = (HttpsURLConnection) new URL(urlIn).openConnection();
        connection.setRequestMethod(requestMethod);
        for (final String[] requestProperty : requestProperties) {
            connection.setRequestProperty(requestProperty[0], requestProperty[1]);
        }
        return connection;
    }

    public static HttpURLConnection createHTTPURLConnection(final String urlIn, final String requestMethod, final String[]... requestProperties) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) new URL(urlIn).openConnection();
        connection.setRequestMethod(requestMethod);
        for (final String[] requestProperty : requestProperties) {
            connection.setRequestProperty(requestProperty[0], requestProperty[1]);
        }
        return connection;
    }

    public static HttpsURLConnection createHTTPSURLConnection(final String urlIn, final String[]... requestProperties) throws IOException {
        final HttpsURLConnection connection = (HttpsURLConnection) new URL(urlIn).openConnection();
        for (final String[] requestProperty : requestProperties) {
            connection.setRequestProperty(requestProperty[0], requestProperty[1]);
        }
        return connection;
    }

    public static HttpURLConnection createHTTPURLConnection(final String urlIn, final String[]... requestProperties) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) new URL(urlIn).openConnection();
        for (final String[] requestProperty : requestProperties) {
            connection.setRequestProperty(requestProperty[0], requestProperty[1]);
        }
        return connection;
    }
}
