/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 29.12.20, 19:41
 */

package net.bplaced.abzzezz.animeapp.util.tasks.twistmoe;

import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.tasks.DownloadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class TwistmoeDownloadTask extends DownloadTask {

    public TwistmoeDownloadTask(SelectedActivity application, URL url, String name, File outDir, int[] count) {
        super(application, url, name, outDir, count);
    }

    @Override
    public String call() throws Exception {
        if (!outDir.exists()) outDir.mkdir();
        this.outFile = new File(outDir, count[1] + ".mp4");
        try {
            //Open new URL connection
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", StringHandler.USER_AGENT);
            connection.addRequestProperty("Range", "f'bytes={pos}-");
            connection.addRequestProperty("Referer", "https://twist.moe/a/");
            connection.connect();
            //Open Stream
            this.fileOutputStream = new FileOutputStream(outFile);
            final ReadableByteChannel readableByteChannel = Channels.newChannel(connection.getInputStream());
            //Copy from channel to channel
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            //Close stream
            Logger.log("Done copying streams, closing stream", Logger.LogType.INFO);
            fileOutputStream.close();
            return name.concat(": ") + count[1];
        } catch (MalformedURLException e) {
            cancel();
            return name.concat(": ") + count[1];
        }
    }
}
