/*
 * Copyright (c) 2020.
 * The code used in this project is entirely owned by Roman P.
 * Code snippets / templates / etc. are mentioned and credited.
 */

package net.bplaced.abzzezz.animeapp.util.tasks.twistmoe;

import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.provider.holders.TwistmoeHolder;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;

import javax.net.ssl.HttpsURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TwistmoeSearchTask extends TaskExecutor implements Callable<List<Show>>, TwistmoeHolder {

    private final String searchQuery;

    public TwistmoeSearchTask(final String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public <R> void executeAsync(final Callback<List<Show>> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public List<Show> call() throws Exception {
        final HttpsURLConnection connection = URLUtil.createHTTPSURLConnection(SHOW_API, new String[]{"x-access-token", getRequestToken()}, new String[]{"User-Agent", StringHandler.USER_AGENT});
        connection.connect();
        System.out.println(connection.getResponseMessage());

        /*
        HttpsURLConnection connection = URLUtil.createHTTPSURLConnection(url);
        connection.connect();
        final JSONObject fetchedDetails = new JSONObject(new BufferedReader(new InputStreamReader(connection.getInputStream())).lines().collect(Collectors.joining()));

        connection = URLUtil.createHTTPSURLConnection(Twistmoe.API_URL + searchQuery + "/sources/");
        connection.connect();

        final JSONArray fetchedSources = new JSONArray(new BufferedReader(new InputStreamReader(connection.getInputStream())).lines().collect(Collectors.joining()));
        final JSONArray sources = new JSONArray();

        for (int i = 0; i < fetchedSources.length(); i++) {
            final JSONObject item = fetchedSources.getJSONObject(i);
            sources.put(item.getString("source"));
        }

        final JSONObject showDetails = new JSONObject();
        showDetails.put("url", searchQuery)
                .put("title", fetchedDetails.getString("title"))
                .put("id", fetchedDetails.getString("id"))
                .put("sources", sources)
                .put("episode_count", sources.length())
                .put("description", fetchedDetails.getString("description"));
        return showDetails;

         */
        return new ArrayList<>();
    }
}
