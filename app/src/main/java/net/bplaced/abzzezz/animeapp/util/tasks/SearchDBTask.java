/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 16:41
 */

package net.bplaced.abzzezz.animeapp.util.tasks;

import net.bplaced.abzzezz.animeapp.util.scripter.AniDBSearch;
import net.ricecode.similarity.JaroStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class SearchDBTask extends TaskExecutor implements Callable<List<JSONObject>> {

    private final String input;
    private final AniDBSearch aniDBSearch = new AniDBSearch();
    private final SimilarityStrategy stringSimilarity = new JaroStrategy();

    public SearchDBTask(final String input) {
        this.input = input;
    }

    public <R> void executeAsync(Callback<List<JSONObject>> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public List<JSONObject> call() throws Exception {
        final List<JSONObject> showsOut = new ArrayList<>();
        final JSONArray showsIn = new JSONArray(aniDBSearch.getDataBase());

        for (int i = 0; i < showsIn.length(); i++) {
            final JSONObject show = showsIn.getJSONObject(i);

            if (stringSimilarity.score(show.getString("titel"), input) > 0.8 && !showsOut.contains(show)) {
                showsOut.add(show);
            }
        }
        return showsOut;
    }
}
