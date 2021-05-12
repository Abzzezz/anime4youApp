/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 06.04.21, 23:15
 */

package net.bplaced.abzzezz.animeapp.util.provider.providers;

import android.content.Context;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.bplaced.abzzezz.animeapp.util.tasks.animepahe.AnimePaheEpisodeDownloadTask;
import net.bplaced.abzzezz.animeapp.util.tasks.animepahe.AnimePaheFetchDirectTask;
import net.bplaced.abzzezz.animeapp.util.tasks.animepahe.AnimePaheRefreshTask;
import net.bplaced.abzzezz.animeapp.util.tasks.animepahe.AnimePaheSearchTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

public class AnimePahe extends Provider {

    public AnimePahe() {
        super("ANIMEPAHE");
    }

    @Override
    public void refreshShow(final Show show, final Consumer<Show> updatedShow) {
        new AnimePaheRefreshTask(show).executeAsync(new TaskExecutor.Callback<JSONObject>() {
            @Override
            public void onComplete(final JSONObject result) {
                //This is bad..... I haven't thought this through.... I have to gamble i guess
                show.getProviderJSON(AnimePahe.this).ifPresent(providerJSON -> {
                    try {
                        providerJSON.put("session", result.getString("session")); //Update session
                        show.updateProviderJSON(AnimePahe.this, providerJSON); //Update provider json

                        updatedShow.accept(show); //"Return" the updated show object
                    } catch (final JSONException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void preExecute() {
                Logger.log("Refreshing show", Logger.LogType.INFO);
            }
        });
    }

    @Override
    public void getShowEpisodeReferrals(final Show show, final Consumer<JSONArray> showReferrals) {
        new AnimePaheSearchTask(show.getShowTitle()).executeAsync(new TaskExecutor.Callback<Optional<JSONObject>>() {
            @Override
            public void onComplete(final Optional<JSONObject> result) {
                show.getProviderJSON(AnimePahe.this).ifPresent(providerJSON ->
                        result.ifPresent(resultJSON -> {
                            try {
                                providerJSON.put("session", resultJSON.getString("session")); //update the session
                                show.updateProviderJSON(AnimePahe.this, providerJSON); //Commit changes
                                showReferrals.accept(resultJSON.getJSONArray("src")); //Return the show referrals
                            } catch (final JSONException e) {
                                e.printStackTrace();
                            }
                        }));
            }

            @Override
            public void preExecute() {
                Logger.log("Searching anime-pahe", Logger.LogType.INFO);
            }
        });
    }

    @Override
    public void handleURLRequest(final Show show, final Context context, final Consumer<Optional<String>> resultURL, final int... ints) {
        new AnimePaheFetchDirectTask(show.getShowEpisodes(this).optJSONObject(ints[1])).executeAsync(new TaskExecutor.Callback<Optional<String>>() {
            @Override
            public void onComplete(final Optional<String> result) {
                resultURL.accept(result);
            }

            @Override
            public void preExecute() {
                Logger.log("Fetching direct video link", Logger.LogType.INFO);
            }
        });
    }

    @Override
    public void handleDownload(final SelectedActivity activity, final String url, final Show show, final File outDirectory, final int... ints) {
        new AnimePaheEpisodeDownloadTask(activity, url, show.getShowTitle(), outDirectory, ints).executeAsync();
    }
}
