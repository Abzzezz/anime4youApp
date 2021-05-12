/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 07.04.21, 14:29
 */

package net.bplaced.abzzezz.animeapp.util.tasks.animepahe;

import net.bplaced.abzzezz.animeapp.util.Constant;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.provider.holders.AnimePaheHolder;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.util.Locale;
import java.util.concurrent.Callable;

public class AnimePaheFetchCallable implements Callable<JSONObject>, AnimePaheHolder {
    private final JSONObject showJSON;

    public AnimePaheFetchCallable(final JSONObject showJSON) {
        this.showJSON = showJSON;
    }

    @Override
    public JSONObject call() throws Exception {
        HttpsURLConnection episodeAPIConnection = URLUtil.createHTTPSURLConnection(String.format(Locale.ENGLISH, EPISODE_API, showJSON.getString("id"), 1), new String[]{"User-Agent", Constant.USER_AGENT});
        JSONObject collectedLines = new JSONObject(URLUtil.collectLines(episodeAPIConnection, ""));
        final JSONArray data = collectedLines.getJSONArray("data");

        final int pages = (int) Math.ceil(collectedLines.getDouble("total") / collectedLines.getDouble("per_page")); //Calculate the number of pages that is needed to store all the episodes

        for (int i = 2; i <= pages; i++) {
            episodeAPIConnection = URLUtil.createHTTPSURLConnection(String.format(Locale.ENGLISH, EPISODE_API, showJSON.getString("id"), i), new String[]{"User-Agent", Constant.USER_AGENT});
            collectedLines = new JSONObject(URLUtil.collectLines(episodeAPIConnection, ""));
            final JSONArray moreData = collectedLines.getJSONArray("data");

            for (int j = 0; j < moreData.length(); j++) {
                data.put(moreData.getJSONObject(j));
            }

        }

        final JSONArray sources = new JSONArray();

        for (int i = 0; i < data.length(); i++) {
            final JSONObject dataJSONObject = data.getJSONObject(i);
            sources.put(new JSONObject()
                    .put("anime_id", dataJSONObject.getInt("anime_id"))
                    .put("session", dataJSONObject.getString("session")
                    ));
        }

        episodeAPIConnection.disconnect();
        showJSON.put("src", sources);
        return showJSON;
    }
}
