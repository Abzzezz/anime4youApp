/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 03.04.21, 23:28
 */

package net.bplaced.abzzezz.animeapp.util.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.util.IntentHelper;
import net.bplaced.abzzezz.animeapp.util.tasks.download.EpisodeDownloadTask;

public class StopDownloadReceiver extends BroadcastReceiver {

    /**
     * Gets called if stop download is triggered
     *
     * @param context context
     * @param intent  intent to read from
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        final EpisodeDownloadTask trigger = (EpisodeDownloadTask) IntentHelper.getObjectForKey(intent.getDataString());
        if (!trigger.isCancelled()) {
            Logger.log("Further downloading cancelled", Logger.LogType.INFO);
            Toast.makeText(context, "Download cancelled", Toast.LENGTH_SHORT).show();
            trigger.cancelExecution();
        }
    }
}