/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 29.03.21, 19:37
 */

package net.bplaced.abzzezz.animeapp.util.provider.providers;

import android.content.Context;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.bplaced.abzzezz.animeapp.util.tasks.twistmoe.TwistmoeDecodeSourcesTask;
import net.bplaced.abzzezz.animeapp.util.tasks.twistmoe.TwistmoeEpisodeDownloadTask;
import net.bplaced.abzzezz.animeapp.util.tasks.twistmoe.TwistmoeFetchCallable;
import net.bplaced.abzzezz.animeapp.util.tasks.twistmoe.TwistmoeSearchTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Twistmoe extends Provider {

    public Twistmoe() {
        super("TWISTMOE");
    }

    @Override
    public void refreshShow(final Show show, final Consumer<Show> updatedShow) {
        show.getProviderJSON(this).ifPresent(jsonObject -> new TaskExecutor().executeAsync(() ->
                new TwistmoeFetchCallable(jsonObject.getString("slug")).call(), new TaskExecutor.Callback<JSONObject>() {
            @Override
            public void onComplete(final JSONObject result) {
                try {
                    show.addEpisodesForProvider(result.getJSONArray("src"), Twistmoe.this); //Add the new episode urls
                    updatedShow.accept(show); //"Return" the show object
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void preExecute() {
                Logger.log("Refreshing twist.moe", Logger.LogType.INFO);
            }
        }));
    }

    @Override
    public void getShowEpisodeReferrals(Show show, Consumer<JSONArray> showReferrals) {
        new TwistmoeSearchTask(show.getID(), show.getShowTitle()).executeAsync(new TaskExecutor.Callback<List<JSONObject>>() {
            @Override
            public void onComplete(List<JSONObject> result) {
                if (result.size() >= 1) {
                    //This is bad..... I haven't thought this through.... I have to gamble i guess
                    show.getProviderJSON(Twistmoe.this).ifPresent(providerJSON -> {
                        try {
                            providerJSON.put("slug", result.get(0).getString("url")); //Update provider json object
                            show.updateProviderJSON(Twistmoe.this, providerJSON); //Update provider json

                            showReferrals.accept(result.get(0).getJSONArray("src")); //"Return" the
                        } catch (final JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void preExecute() {
                Logger.log("Searching twist.moe", Logger.LogType.INFO);
            }
        });
    }

    @Override
    public void handleURLRequest(Show show, Context context, Consumer<Optional<String>> resultURL, int... ints) {
        new TwistmoeDecodeSourcesTask(show.getShowEpisodes(this).optString(ints[1])).executeAsync(new TaskExecutor.Callback<String>() {
            @Override
            public void onComplete(String result) {
                resultURL.accept(Optional.of(result));
            }

            @Override
            public void preExecute() {
            }
        });
    }

    @Override
    public void handleDownload(SelectedActivity activity, String url, Show show, File outDirectory, int... ints) {
        new TwistmoeEpisodeDownloadTask(activity, url, show.getShowTitle(), outDirectory, ints).executeAsync();
    }
}
