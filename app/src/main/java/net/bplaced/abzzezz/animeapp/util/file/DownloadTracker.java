/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 14:05
 */

package net.bplaced.abzzezz.animeapp.util.file;

import android.content.Context;
import ga.abzzezz.util.data.FileUtil;
import ga.abzzezz.util.logging.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;

public class DownloadTracker {

    private final File trackerFile;
    private final Context context;

    public DownloadTracker(Context context) {
        this.context = context;
        this.trackerFile = new File(context.getFilesDir(), "DownloadTracker.xml");

        Logger.log("Download Tracker set up", Logger.LogType.INFO);
    }

    public void submitTrack(String information) {
        String track = information + "\n";
        try (FileOutputStream fos = new FileOutputStream(trackerFile, true)) {
            fos.write(track.getBytes());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return list, same as before
     *
     * @return
     */
    public ArrayList<String> getList() {
        try {
            return (ArrayList<String>) FileUtil.getFileContentAsList(trackerFile);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
