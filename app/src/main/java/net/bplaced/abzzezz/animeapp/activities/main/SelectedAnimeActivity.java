/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:37
 */

package net.bplaced.abzzezz.animeapp.activities.main;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import ga.abzzezz.util.data.FileUtil;
import ga.abzzezz.util.data.URLUtil;
import ga.abzzezz.util.logging.Logger;
import ga.abzzezz.util.stringing.StringUtil;
import id.ionbit.ionalert.IonAlert;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.extra.PlayerActivity;
import net.bplaced.abzzezz.animeapp.util.ImageUtil;
import net.bplaced.abzzezz.animeapp.util.InputDialogBuilder;
import net.bplaced.abzzezz.animeapp.util.file.OfflineImageLoader;
import net.bplaced.abzzezz.animeapp.util.scripter.ScriptUtil;
import net.bplaced.abzzezz.animeapp.util.scripter.URLHandler;
import net.bplaced.abzzezz.animeapp.util.tasks.DownloadTask;

import java.io.File;
import java.util.*;

public class SelectedAnimeActivity extends AppCompatActivity {

    public transient AnimeEpisodeAdapter animeEpisodeAdapter;
    private String animeName;
    private int aid, animeEpisodes;
    private File animeFile;
    private GridView episodeGrid;

    public static List<String> sortWithNumberInName(List<String> in) {
        in.sort(Comparator.comparingInt((o) -> StringUtil.extractNumberI(o.substring(o.indexOf("::") + 2, o.indexOf(".mp4")))));
        return in;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(AnimeAppMain.getInstance().getThemeID());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anime_selected_layout);

        /*
         * Get intent varaibles
         */
        this.animeName = getIntent().getStringExtra("anime_name");
        this.animeEpisodes = Integer.parseInt(getIntent().getStringExtra("anime_episodes"));
        String animeCover = getIntent().getStringExtra("anime_cover");
        this.aid = Integer.parseInt(getIntent().getStringExtra("anime_aid"));
        String language = getIntent().getStringExtra("anime_language");

        /*
         * Set text
         */
        TextView selected_anime_name = findViewById(R.id.selected_anime_name);
        TextView selected_anime_episodes = findViewById(R.id.selected_anime_episodes);
        TextView selected_anime_aid = findViewById(R.id.selected_anime_aid);
        TextView selected_anime_size = findViewById(R.id.anime_directory_size);
        TextView selected_anime_language = findViewById(R.id.selected_anime_language);

        selected_anime_aid.append(String.valueOf(aid));
        selected_anime_name.setText(animeName);
        selected_anime_episodes.append(String.valueOf(animeEpisodes));
        selected_anime_language.append(language);


        /*
         * Toolbar and Image
         */
        Toolbar toolbar = findViewById(R.id.selected_anime_toolbar);
        ImageView cover = findViewById(R.id.anime_cover_image);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(animeName);
        /**
         * If offline mode is enabled use image offline loader
         */
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("offline_mode", false)) {
            OfflineImageLoader.loadImage(animeCover, String.valueOf(aid), cover, this);
        } else {
            Picasso.with(getApplicationContext()).load(animeCover).resize(ImageUtil.dimensions[0], ImageUtil.dimensions[1]).into(cover);
        }
        /*
         * GridView and Download Button
         */
        this.episodeGrid = findViewById(R.id.anime_episodes_grid);
        /*
         * Get anime file
         */

        /*
         * If it does not exist then create new one
         */
        this.animeFile = new File(getFilesDir(), animeName);

        /*
         * Convert file array to list
         */
        List<String> episodes = sortWithNumberInName(Arrays.asList(animeFile.list() == null ? new String[0] : animeFile.list()));

        selected_anime_size.append(FileUtil.calculateFileSize(animeFile));
        /*
         * Set Adapter
         */
        this.animeEpisodeAdapter = new AnimeEpisodeAdapter(episodes, getApplicationContext());
        this.episodeGrid.setAdapter(animeEpisodeAdapter);
        /*
         Configure grid
         */
        episodeGrid.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = null;
            int mode = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("video_player_preference", "0"));
            if (mode == 0) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", getEpisodeFile(position)), "video/mp4");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (mode == 1) {
                intent = new Intent(getApplicationContext(), PlayerActivity.class);
                intent.putExtra("path", getEpisodeFile(position).getAbsolutePath());
            }
            startActivity(Objects.requireNonNull(intent));
            finish();
        });
        /*
        Set long click listener
         */
        episodeGrid.setOnItemLongClickListener((parent, view, position, id) -> {
            new IonAlert(this, IonAlert.WARNING_TYPE)
                    .setTitleText("Remove file?")
                    .setContentText("Won't be able to recover this file!")
                    .setConfirmText("Yes,delete it!")
                    .setConfirmClickListener(ionAlert -> {
                        animeEpisodeAdapter.deleteItem(position);
                        ionAlert.dismissWithAnimation();
                    }).setCancelText("Abort").setCancelClickListener(IonAlert::dismissWithAnimation)
                    .show();
            return true;
        });

        /*
         * Button
         */
        FloatingActionButton downloadAnime = findViewById(R.id.download_anime_button);
        /*
        "Calculate" next start
         */
        downloadAnime.setOnClickListener(v -> downloadEpisode(getLatestEpisode(), animeEpisodes, 0));
    }

    /**
     * Toolbar
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anime_selected_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public int getLatestEpisode() {
        if (animeFile.list() != null) {
            OptionalInt highest = Arrays.stream(animeFile.list()).map(s -> StringUtil.extractNumberI(s.substring(s.indexOf("::") + 2, s.indexOf(".mp4")))).mapToInt(integer -> integer).max();
            if (highest.isPresent())
                return highest.getAsInt() + 1;
        }

        return 1;
    }

    public void resetAdapter() {
        animeEpisodeAdapter = new AnimeEpisodeAdapter(sortWithNumberInName(Arrays.asList(animeFile.list())), this);
        episodeGrid.setAdapter(animeEpisodeAdapter);
        animeEpisodeAdapter.notifyDataSetChanged();
    }

    /**
     * Items selected
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        switch (itemID) {
            case R.id.download_specific_episode:
                InputDialogBuilder inputDialogBuilder = new InputDialogBuilder(new InputDialogBuilder.InputDialogListener() {
                    @Override
                    public void onDialogInput(String text) {
                        downloadEpisode(Integer.parseInt(text), 1, 0);
                    }

                    @Override
                    public void onDialogDenied() {
                    }
                });
                inputDialogBuilder.showInput("Download spefic", "Enter episode to download", this);
                break;
            case R.id.download_bound:
                InputDialogBuilder dialogBuilder = new InputDialogBuilder(new InputDialogBuilder.InputDialogListener() {
                    @Override
                    public void onDialogInput(String text) {
                        downloadEpisode(getLatestEpisode(), Integer.parseInt(text), 0);
                    }

                    @Override
                    public void onDialogDenied() {
                    }
                });
                dialogBuilder.showInput("Download bound", "Enter bound", this);
                break;
            case R.id.toogle_notifications_anime:
                //Add to notification manager
                AnimeAppMain.getInstance().getAnimeNotifications().add(animeName + StringUtil.splitter + aid, String.valueOf(animeEpisodes));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * On Back pressed
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, DrawerMainMenu.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    /**
     * Download method
     *
     * @param start
     * @param countMax
     * @param currentCount
     */
    public void downloadEpisode(int start, int countMax, int currentCount) {
        Logger.log("Next episode: " + start, Logger.LogType.INFO);
        int[] count = {currentCount, start};
        /**
         * Check if count is bigger than the max episodes to download
         */
        if (count[0] >= countMax) {
            Logger.log("current episode exceeds max / start exceeds max", Logger.LogType.ERROR);
            return;
        }
        /**
         * Load captcha URL
         */
        final WebView webView = new WebView(getApplicationContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(URLHandler.HTTPS_CAPTCHA_ANIME_4_YOU_ONE);
        /**
         * Clear all previous data and Cookies, so no code 400 appears: Cookie too large (yummy ;) )
         */
        WebStorage.getInstance().deleteAllData();
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        webView.clearSslPreferences();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.evaluateJavascript(ScriptUtil.getRequest(aid, count[1]), returnCaptcha -> {
                    if (returnCaptcha.contains("vivo")) {
                        Logger.log("Found link for vivo:" + returnCaptcha, Logger.LogType.INFO);
                        makeText("Found VIVO link");
                        view.loadUrl(URLUtil.toUrl(StringUtil.removeBadCharacters(returnCaptcha, "\\\\", "\""), "https"));
                        view.setWebViewClient(new WebViewClient() {
                            @Override
                            public void onPageFinished(WebView view, String url) {
                                view.evaluateJavascript(ScriptUtil.vivoExploit, value -> {
                                    if (value.contains("node")) {
                                        //Run new Download Task and download episode
                                        new DownloadTask(SelectedAnimeActivity.this, new String[]{value.replaceAll("\"", ""), animeName + "::" + count[1] + ".mp4"}, new int[]{count[0], count[1], countMax}).executeAsync();
                                        view.destroy();
                                        //  download(value.replaceAll("\"", ""), animeName + "::" + count[1]);

                                    } else makeText("Error getting direct vivo link: " + value);
                                });
                                super.onPageFinished(view, url);
                            }
                        });
                    } else {
                        makeText("Error getting vivo link. Anime4you might be down / Anime done downloading");
                        Logger.log(returnCaptcha, Logger.LogType.WARNING);
                    }
                });
                super.onPageFinished(view, url);
            }
        });
    }

    /**
     * Make text
     *
     * @param text
     */
    public void makeText(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    /**
     * Get episode file
     *
     * @param index
     * @return
     */
    public File getEpisodeFile(int index) {
        File animeFile = new File(getFilesDir(), animeName);
        return new File(animeFile, Objects.requireNonNull(animeFile.list())[index]);
    }

    /*
    private BroadcastReceiver broadcastReceiver;
    public void download(String url, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        File file = new File(Environment.DIRECTORY_DOWNLOADS, "Move");
        request.setDescription("Downloading File: " + fileName);
        request.setTitle(fileName);
        request.setDestinationInExternalPublicDir(file.getAbsolutePath(), fileName + ".mp4");
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
                    if (cursor.moveToFirst()) {
                        File src = new File(Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))).getPath());
                        File newOut = new File(getFilesDir(), fileName.substring(0, fileName.indexOf("::")));
                        if (!newOut.exists()) newOut.mkdir();
                        FileUtil.copyFile(src, new File(newOut, src.getName()), true);
                    }

                    cursor.close();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
    */

    /**
     * Episode adapter
     */
    class AnimeEpisodeAdapter extends BaseAdapter {

        private final List<String> episodes;
        private final Context context;

        public AnimeEpisodeAdapter(List<String> episodes, Context context) {
            this.episodes = new ArrayList<>(episodes);
            this.context = context;
        }

        @Override
        public int getCount() {
            return episodes.size();
        }

        @Override
        public Object getItem(int position) {
            return episodes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TextView textView = new TextView(context);
            textView.setTextSize(15);
            textView.setTextColor(ColorStateList.valueOf(Color.WHITE));
            textView.setText(episodes.get(position));
            return textView;
        }

        /**
         * Remove item from list then refresh
         * @param index
         */
        public void deleteItem(int index) {
            Logger.log("Deleted: " + getEpisodeFile(index).delete(), Logger.LogType.INFO);
            episodes.remove(index);
            notifyDataSetChanged();
        }
    }

    /*
    New download task
    */
}

