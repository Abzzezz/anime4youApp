/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 09.04.21, 19:51
 */

package net.bplaced.abzzezz.animeapp.util.provider.providers;

import android.content.Context;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONArray;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

public class AniFlix extends Provider {


    public AniFlix() {
        super("ANIFLIX");
    }

    @Override
    public void refreshShow(Show show, Consumer<Show> updatedShow) {

    }

    @Override
    public void getShowEpisodeReferrals(Show show, Consumer<JSONArray> showReferrals) {
        new TaskExecutor().executeAsync(() -> {

            return new JSONArray();
        }, new TaskExecutor.Callback<JSONArray>() {
            @Override
            public void onComplete(JSONArray result) throws Exception {
                showReferrals.accept(result);
            }

            @Override
            public void preExecute() {

            }
        });
    }

    @Override
    public void handleURLRequest(Show show, Context context, Consumer<Optional<String>> resultURL, int... ints) {

    }

    @Override
    public void handleDownload(SelectedActivity activity, String url, Show show, File outDirectory, int... ints) {

    }
}
