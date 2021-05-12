/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 07.04.21, 13:09
 */

package net.bplaced.abzzezz.animeapp.util.tasks.animepahe;

import net.bplaced.abzzezz.animeapp.util.Constant;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.provider.holders.AnimePaheHolder;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;
import java.util.concurrent.Callable;

public class AnimePaheSearchTask extends TaskExecutor implements Callable<Optional<JSONObject>>, AnimePaheHolder {

    private final String searchQuery;

    public AnimePaheSearchTask(final String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public void executeAsync(Callback<Optional<JSONObject>> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public Optional<JSONObject> call() throws Exception {
        final String collected = URLUtil.collectLines(URLUtil.createHTTPSURLConnection(String.format(SEARCH_API, searchQuery), new String[]{"User-Agent", Constant.USER_AGENT}), "");

        final JSONArray showsIn = new JSONObject(collected).getJSONArray("data");
        if (showsIn.length() == 0) return Optional.empty(); //Empty case

        return Optional.ofNullable(new AnimePaheFetchCallable(showsIn.getJSONObject(0)).call());
    }
}
