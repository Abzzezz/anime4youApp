/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 14.06.20, 20:07
 */

package net.bplaced.abzzezz.animeapp.activities.extra;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;
import ga.abzzezz.util.data.URLUtil;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.main.DrawerMainMenu;
import net.bplaced.abzzezz.animeapp.util.file.AnimeSaver;
import net.bplaced.abzzezz.animeapp.util.file.Downloader;
import net.bplaced.abzzezz.animeapp.util.scripter.URLHandler;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;


public class SplashScreen extends AppCompatActivity {

    /**
     * Basically the main-class
     */

    public static AnimeSaver saver;
    public static boolean isDebugVersion = true;
    public float version = 33F;

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
        checkPermissions();
        //Configure handlers
        configureHandlers();
        //Set version text
        TextView versionText = findViewById(R.id.version_text);
        versionText.append("v." + version + (isDebugVersion ? "Developer Build" : ""));
        /**
         * Start new intent
         */
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, DrawerMainMenu.class);
            startActivity(intent);
            finish();
        }, isDebugVersion ? 10 : 2500);
    }

    /**
     * Checks permissions and internet connection
     */
    private void checkPermissions() {
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.BLUETOOTH}, 101);
        if (!URLHandler.isOnline(this))
            Toast.makeText(this, "You are not connected to the internet. If Images are not cached they will not show.", Toast.LENGTH_LONG).show();
    }

    /**
     * Configures the handlers and gets a random background
     */
    private void configureHandlers() {
        this.saver = new AnimeSaver(getApplicationContext());
        AutoUpdater autoUpdater = new AutoUpdater();
        VersionChecker versionChecker = new VersionChecker();
        try {
            if(versionChecker.execute().get()) {
                autoUpdater.execute(this);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /*
     * Copyright (c) 2020. Roman P.
     * All code is owned by Roman P. APIs are mentioned.
     * Last modified: 12.06.20, 19:33
     */

    class VersionChecker extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                return version < Float.valueOf(checkUpdate());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return false;
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
