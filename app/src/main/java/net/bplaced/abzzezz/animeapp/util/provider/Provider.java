/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:21
 */

package net.bplaced.abzzezz.animeapp.util.provider;

import android.content.Context;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class Provider {

    private Providers type;
    private String baseURL;

    public Provider(Providers type, String baseURL) {
        this.type = type;
        this.baseURL = baseURL;
    }

    public abstract void refreshShow(final Show show, final Consumer<Show> updatedShow);

    public abstract void handleSearch(final String searchQuery, final Consumer<List<JSONObject>> searchResults);

    public abstract JSONObject format(final Show show) throws JSONException;

    public abstract Show getShow(final JSONObject data) throws JSONException;

    public abstract Optional<URL> handleURLRequest(Show show, final Context context, int... ints);

    public abstract void handleDownload(SelectedActivity activity, final URL url, final Show show, final File outDirectory, final int... ints);

    public abstract Show decode(JSONObject showJSON) throws JSONException;

    public Providers getType() {
        return type;
    }

    public void setType(Providers type) {
        this.type = type;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

}
