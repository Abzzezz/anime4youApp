/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:05
 */

package net.bplaced.abzzezz.animeapp.util.tasks.anime4you;

import net.bplaced.abzzezz.animeapp.util.scripter.Anime4YouDBSearch;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.ricecode.similarity.JaroStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Anime4YouSearchDBTask extends TaskExecutor implements Callable<List<JSONObject>> {

    private final String input;
    private final Anime4YouDBSearch anime4YouDBSearch = new Anime4YouDBSearch();
    private final SimilarityStrategy stringSimilarity = new JaroStrategy();

    public Anime4YouSearchDBTask(final String input) {
        this.input = input;
    }

    public <R> void executeAsync(Callback<List<JSONObject>> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public List<JSONObject> call() throws Exception {
        final List<JSONObject> showsOut = new ArrayList<>();
        final JSONArray showsIn = new JSONArray(anime4YouDBSearch.getDataBase());

        for (int i = 0; i < showsIn.length(); i++) {
            final JSONObject show = showsIn.getJSONObject(i);

            if (stringSimilarity.score(show.getString("titel"), input) > 0.8 && !showsOut.contains(show)) {
                showsOut.add(show);
            }
        }
        return showsOut;
    }
}
