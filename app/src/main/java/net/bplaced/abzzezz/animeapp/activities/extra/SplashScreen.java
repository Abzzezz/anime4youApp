/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 14.06.20, 20:07
 */

package net.bplaced.abzzezz.animeapp.activities.extra;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import ga.abzzezz.util.data.URLUtil;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.main.DrawerMainMenu;
import net.bplaced.abzzezz.animeapp.util.file.Downloader;
import net.bplaced.abzzezz.animeapp.util.scripter.URLHandler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Set theme
         */
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_mode", false)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);
        /**
         * Check permissions. If not given prompt to do so
         */

        AnimeAppMain.getInstance().createNotificationChannel(getApplication());
        AnimeAppMain.getInstance().checkPermissions(this);
        //Configure handlers
        AnimeAppMain.getInstance().configureHandlers(getApplication());

        SplashScreen.AutoUpdater autoUpdater = new SplashScreen.AutoUpdater();
        SplashScreen.VersionChecker versionChecker = new SplashScreen.VersionChecker();
        try {
            if (versionChecker.execute().get()) {
                autoUpdater.execute(this);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        //Set version text
        TextView versionText = findViewById(R.id.version_text);
        versionText.append("v." + AnimeAppMain.getInstance().getVersion());
        /**
         * Start new intent
         */
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, DrawerMainMenu.class);
            startActivity(intent);
            finish();
        }, AnimeAppMain.getInstance().isDebugVersion() ? 10 : 2500);
    }


    /**
     * Auto updates
     */

    class VersionChecker extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if(!checkUpdate().equals("NULL"))
                    return AnimeAppMain.getInstance().getVersion() < Float.valueOf(checkUpdate());
                else return false;
            } catch (MalformedURLException e) {
                return false;
            }
        }

        private String checkUpdate() throws MalformedURLException {
            return URLUtil.getURLContentAsString(new URL(URLHandler.checkURL));
        }
    }

    /**
     * Checks if the byte (version) is smaller than the Byte value of the String on the host
     */
    public class AutoUpdater extends AsyncTask<Activity, Integer, String> {

        @Override
        protected String doInBackground(Activity... activities) {
            File outDic = new File(Environment.DIRECTORY_DOWNLOADS, "Anime4you-Update");
            String fileName = "AutoUpdate.apk";
            Downloader.download(URLHandler.updateURL, outDic, fileName, activities[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String path) {
            Toast.makeText(SplashScreen.this, "New update available. Please install the new version.", Toast.LENGTH_LONG).show();
            super.onPostExecute(path);
        }
    }

}
