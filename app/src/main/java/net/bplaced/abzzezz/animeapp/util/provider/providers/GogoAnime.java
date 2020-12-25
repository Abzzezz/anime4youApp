/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 24.12.20, 21:30
 */

package net.bplaced.abzzezz.animeapp.util.provider.providers;

import android.content.Context;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.gogoanime.GogoAnimeFetcher;
import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.provider.Providers;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.bplaced.abzzezz.animeapp.util.tasks.gogoanime.GogoAnimeDownloadTask;
import net.bplaced.abzzezz.animeapp.util.tasks.gogoanime.GogoAnimeRefreshTask;
import net.bplaced.abzzezz.animeapp.util.tasks.gogoanime.GogoAnimeSearchTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/*
TODO: Fix unnecessary JSON transferring
 */
public class GogoAnime extends Provider {

    public GogoAnime() {
        super(Providers.GOGOANIME, "");
    }

    @Override
    public void refreshShow(Show show, Consumer<Show> updatedShow) {
        new GogoAnimeRefreshTask(show).executeAsync(new TaskExecutor.Callback<Show>() {
            @Override
            public void onComplete(Show result) throws Exception {
                updatedShow.accept(result);
            }

            @Override
            public void preExecute() {
                Logger.log("Updating show from gogo-anime", Logger.LogType.INFO);
            }
        });
    }

    @Override
    public void handleSearch(String searchQuery, Consumer<List<JSONObject>> searchResults) {
        new GogoAnimeSearchTask(searchQuery).executeAsync(new TaskExecutor.Callback<List<JSONObject>>() {
            @Override
            public void onComplete(List<JSONObject> result) throws Exception {
                searchResults.accept(result);
            }

            @Override
            public void preExecute() {
                Logger.log("Searching gogo-anime", Logger.LogType.INFO);
            }
        });
    }

    @Override
    public JSONObject format(Show show) throws JSONException {
        return new JSONObject()
                .put(StringHandler.SHOW_ID, show.getID())
                .put(StringHandler.SHOW_TITLE, show.getTitle())
                .put(StringHandler.SHOW_LANG, show.getLanguage())
                .put("ep_start", show.getShowAdditional().getInt("ep_start"))
                .put("ep_end", show.getShowAdditional().getInt("ep_end"))
                .put(StringHandler.SHOW_IMAGE_URL, show.getImageURL())
                .put("episodes", show.getShowAdditional().getJSONArray("episodes"))
                .put(StringHandler.SHOW_PROVIDER, Providers.GOGOANIME.name());
    }

    @Override
    public Show getShow(JSONObject data) throws JSONException {
        final JSONArray episodes = data.getJSONArray("episodes");
        return new Show(
                data.getString(StringHandler.SHOW_ID),
                String.valueOf(episodes.length()),
                data.getString(StringHandler.SHOW_TITLE),
                data.getString(StringHandler.SHOW_IMAGE_URL),
                data.getString(StringHandler.SHOW_LANG),
                Providers.ANIME4YOU.getProvider(), new JSONObject()
                .put("episodes", episodes)
                .put("ep_start", data.getInt("ep_start"))
                .put("ep_end", data.getInt("ep_end")));
    }


    @Override
    public Show decode(JSONObject showJSON) throws JSONException {
        final JSONArray episodes = showJSON.getJSONArray("episodes");
        return new Show(
                showJSON.getString(StringHandler.SHOW_ID),
                String.valueOf(episodes.length()),
                showJSON.getString(StringHandler.SHOW_TITLE),
                showJSON.getString(StringHandler.SHOW_IMAGE_URL),
                showJSON.getString(StringHandler.SHOW_LANG),
                Providers.ANIME4YOU.getProvider(), new JSONObject()
                .put("episodes", episodes)
                .put("ep_start", showJSON.getInt("ep_start"))
                .put("ep_end", showJSON.getInt("ep_end")));
    }

    @Override
    public Optional<URL> handleURLRequest(Show show, Context context, int... ints) {
        try {
            return Optional.of(new URL(String.format(GogoAnimeFetcher.API_URL, show.getShowAdditional().getJSONArray("episodes").getString((ints[1] + 1)))));
        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void handleDownload(SelectedActivity activity, URL url, Show show, File outDirectory, int... ints) {
        new GogoAnimeDownloadTask(activity, url, show.getTitle(), outDirectory, ints).executeAsync();
    }

}
