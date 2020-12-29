/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 29.12.20, 17:26
 */

package net.bplaced.abzzezz.animeapp.util.provider.providers;

import android.content.Context;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.bplaced.abzzezz.animeapp.util.tasks.twistmoe.TwistmoeSearchTask;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Twistmoe extends Provider {

    public Twistmoe() {
        super("TWISTMOE");
    }

    @Override
    public void refreshShow(Show show, Consumer<Show> updatedShow) {

    }

    @Override
    public void handleSearch(String searchQuery, Consumer<List<Show>> searchResults) {
        new TwistmoeSearchTask(searchQuery).executeAsync(new TaskExecutor.Callback<List<Show>>() {
            @Override
            public void onComplete(List<Show> result) {
                searchResults.accept(result);
            }

            @Override
            public void preExecute() {

            }
        });
    }

    @Override
    public JSONObject format(Show show) throws JSONException {
        return null;
    }

    @Override
    public Show getShow(JSONObject data) throws JSONException {
        return null;
    }

    @Override
    public void handleURLRequest(Show show, Context context, Consumer<Optional<URL>> resultURL, int... ints) {

    }

    @Override
    public void handleDownload(SelectedActivity activity, URL url, Show show, File outDirectory, int... ints) {

    }

    @Override
    public Show decode(JSONObject showJSON) throws JSONException {
        return null;
    }
}
