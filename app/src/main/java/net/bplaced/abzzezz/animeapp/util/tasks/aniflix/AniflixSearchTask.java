/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 09.04.21, 19:53
 */

package net.bplaced.abzzezz.animeapp.util.tasks.aniflix;

import android.content.Context;
import net.bplaced.abzzezz.animeapp.util.provider.holders.AniFlixHolder;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class AniflixSearchTask extends TaskExecutor implements Callable<List<JSONObject>>, AniFlixHolder {

    private final String searchQuery;
    private final Context context;

    public AniflixSearchTask(final String searchQuery, final Context context) {
        this.searchQuery = searchQuery;
        this.context = context;
    }

    public void executeAsync(Callback<List<JSONObject>> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public List<JSONObject> call() throws Exception {
        final List<JSONObject> showsOut = new ArrayList<>();
        return showsOut;
    }
}