/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 07.11.20, 20:32
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;
import com.htetznaing.lowcostvideo.LowCostVideo;
import com.htetznaing.lowcostvideo.Model.XModel;
import com.htetznaing.lowcostvideo.Sites.Vidoza;
import com.squareup.picasso.Picasso;
import ga.abzzezz.util.data.FileUtil;
import ga.abzzezz.util.logging.Logger;
import ga.abzzezz.util.stringing.StringUtil;
import id.ionbit.ionalert.IonAlert;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.extra.PlayerActivity;
import net.bplaced.abzzezz.animeapp.activities.extra.StreamPlayer;
import net.bplaced.abzzezz.animeapp.activities.main.DrawerMainMenu;
import net.bplaced.abzzezz.animeapp.util.file.OfflineImageLoader;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.tasks.DownloadTask;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.bplaced.abzzezz.animeapp.util.tasks.VideoFindTask;
import net.bplaced.abzzezz.animeapp.util.tasks.VivoDecodeTask;
import net.bplaced.abzzezz.animeapp.util.ui.ImageUtil;
import net.bplaced.abzzezz.animeapp.util.ui.InputDialogBuilder;
import net.bplaced.abzzezz.animeapp.util.ui.InputDialogBuilder.InputDialogListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

public class SelectedActivity extends AppCompatActivity {

    public EpisodeAdapter episodeAdapter;
    private String title;
    private int id, episodes;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(AnimeAppMain.getInstance().getThemeId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_show_layout);

        /*
         * Get intent variables
         */
        try {
            final JSONObject inf = new JSONObject(getIntent().getStringExtra("details"));

            this.title = inf.getString(StringHandler.SHOW_TITLE);
            this.episodes = inf.getInt(StringHandler.SHOW_EPISODES_COUNT);
            this.id = inf.getInt(StringHandler.SHOW_ID);
            this.file = new File(getFilesDir(), title);
            final String coverUrl = inf.getString(StringHandler.SHOW_IMAGE_URL);

            //Set text etc.
            ((TextView) findViewById(R.id.selected_anime_name)).setText(title);
            ((TextView) findViewById(R.id.selected_anime_episodes)).append(String.valueOf(episodes));
            ((TextView) findViewById(R.id.selected_anime_aid)).append(String.valueOf(id));
            ((TextView) findViewById(R.id.selected_anime_language)).append(inf.getString(StringHandler.SHOW_LANG));
            ((TextView) findViewById(R.id.selected_anime_year)).append(inf.getString(StringHandler.SHOW_YEAR));
            ((TextView) findViewById(R.id.anime_directory_size)).append(FileUtil.calculateFileSize(file));

            final ImageView cover = findViewById(R.id.anime_cover_image);
            final Toolbar toolbar = findViewById(R.id.selected_anime_toolbar);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setTitle(title);
            /*
             * If offline mode is enabled use image offline loader
             */
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("offline_mode", false))
                OfflineImageLoader.loadImage(coverUrl, String.valueOf(id), cover, this);
            else
                Picasso.with(getApplicationContext()).load(coverUrl).resize(ImageUtil.DIMENSIONS[0], ImageUtil.DIMENSIONS[1]).into(cover);


            final ListView listView = findViewById(R.id.anime_episodes_grid);

            /*
             * Set Adapter
             */
            this.episodeAdapter = new EpisodeAdapter(episodes, getApplicationContext());

            listView.setAdapter(episodeAdapter);
            listView.setOnItemClickListener((adapterView, view, i, l) -> {
                final boolean isDownloaded = isEpisodeDownloaded(i);
                new IonAlert(SelectedActivity.this, IonAlert.NORMAL_TYPE)
                        .setConfirmText("Stream")
                        .setConfirmClickListener(ionAlert -> getEpisode(i, 1, 0, true))
                        .setCancelText(isDownloaded ? "Play downloaded" : "Cancel")
                        .setCancelClickListener(ionAlert -> {
                            if (isDownloaded)
                                playEpisodeFromSave(i);
                            else
                                ionAlert.dismissWithAnimation();
                        }).show();
            });
            findViewById(R.id.download_anime_button).setOnClickListener(v -> getEpisode(getLatestEpisode(), episodes, 0, false));
        } catch (JSONException e) {
            Logger.log("Error parsing JSON", Logger.LogType.INFO);
            e.printStackTrace();
        }
    }


    /**
     * Toolbar
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.anime_selected_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public int getLatestEpisode() {
        if (file.list() != null) {
            final OptionalInt highest = Arrays.stream(file.list()).map(s -> StringUtil.extractNumberI(s.substring(0, s.lastIndexOf(".")))).mapToInt(integer -> integer).max();
            if (highest.isPresent()) return highest.getAsInt() + 1;
        }
        return 0;
    }

    public void refreshAdapter() {
        episodeAdapter.notifyDataSetChanged();
    }

    private boolean isEpisodeDownloaded(final int index) {
        if (file.list() != null) {
            return Arrays.stream(file.list()).anyMatch(s -> s.substring(0, s.lastIndexOf(".")).equals(String.valueOf(index)));
        }
        return false;
    }

    /**
     * Items selected
     *
     * @param item selected item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int itemID = item.getItemId();
        switch (itemID) {
            case R.id.download_bound:
                final InputDialogBuilder dialogBuilder = new InputDialogBuilder(new InputDialogListener() {
                    @Override
                    public void onDialogInput(final String text) {
                        getEpisode(getLatestEpisode(), Integer.parseInt(text), 0, false);
                    }

                    @Override
                    public void onDialogDenied() {
                    }
                });
                dialogBuilder.showInput("Download bound", "Enter bound", this);
                break;
            case R.id.toogle_notifications_show:
                //Add to notification manager
                AnimeAppMain.getInstance().getAnimeNotifications().add(title.concat(StringUtil.splitter) + id, String.valueOf(episodes));
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
        startActivity(new Intent(this, DrawerMainMenu.class));
        finish();
        super.onBackPressed();
    }

    /**
     * Download method
     *
     * @param start        start
     * @param countMax     max download
     * @param currentCount current episode
     */
    public void getEpisode(final int start, final int countMax, final int currentCount, final boolean stream) {
        Logger.log("Next episode: " + start, Logger.LogType.INFO);
        final int[] count = {currentCount, start};
        /*
         * Check if count is bigger than the max episodes to download
         */
        if (count[0] >= countMax) {
            Logger.log("current episode exceeds max / start exceeds max", Logger.LogType.ERROR);
            return;
        }

        final WebView webView = new WebView(getApplicationContext());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(StringHandler.CAPTCHA_ANIME_4_YOU_ONE);

        WebStorage.getInstance().deleteAllData();
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        webView.clearCache(true);
        webView.clearFormData();
        webView.clearHistory();
        webView.clearSslPreferences();

        new VideoFindTask(id, count[1]).executeAsync(new TaskExecutor.Callback<String>() {
            @Override
            public void onComplete(String foundEntry) {
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(final WebView view, final String url) {
                        view.evaluateJavascript(foundEntry, resultFromCaptcha -> {
                            try {
                                final JSONArray resultJSON = new JSONObject(resultFromCaptcha).getJSONArray("hosts");
                                final String vivoURLEncoded = resultJSON.getJSONObject(2).getString("href");
                                final String vidozaHash = resultJSON.getJSONObject(1).getString("crypt");

                                final String[] urls = new String[2];

                                final Consumer<String> onDone = vivoURLDecoded -> {
                                    urls[0] = vivoURLDecoded;
                                    webView.destroy();
                                    view.destroy();

                                    String finalURL = urls[0];
                                    if (urls[0] == null && urls[1] == null) {
                                        makeText("No link found for requested video");
                                        return;
                                    } else if (urls[0] == null) {
                                        finalURL = urls[1];
                                        makeText("Downloading from vidoza");
                                    } else if (urls[0].isEmpty()) {
                                        finalURL = urls[1];
                                        makeText("Downloading from vidoza");
                                    }

                                    finalURL = finalURL.replace("\"", "");

                                    if (stream) {
                                        final Intent intent = new Intent(SelectedActivity.this, StreamPlayer.class);
                                        intent.putExtra("stream", finalURL);
                                        startActivity(intent);
                                        finish();
                                    } else
                                        new DownloadTask(SelectedActivity.this, finalURL, title, new int[]{count[0], count[1], countMax}).executeAsync();
                                };

                                view.evaluateJavascript(String.format(StringHandler.VIDOZA_SCRIPT, vidozaHash), vidozaURL -> {
                                    Vidoza.fetch(vidozaURL.replace("\"", ""), new LowCostVideo.OnTaskCompleted() {
                                        @Override
                                        public void onTaskCompleted(final ArrayList<XModel> vidURL, final boolean multiple_quality) {
                                            vidURL.stream().max(XModel::compareTo).ifPresent(xModel -> urls[1] = xModel.getUrl());
                                            decodeVivo(vivoURLEncoded, onDone);
                                        }

                                        @Override
                                        public void onError() {
                                            System.out.println("Error vidoza");
                                            decodeVivo(vivoURLEncoded, onDone);
                                        }
                                    });
                                });
                            } catch (final Exception e) {
                                e.printStackTrace();
                            }
                        });
                        super.onPageFinished(view, url);
                    }
                });
            }

            @Override
            public void preExecute() {
            }
        });
    }

    private void decodeVivo(final String vivoURL, final Consumer<String> url) {
        new VivoDecodeTask(vivoURL).executeAsync(new TaskExecutor.Callback<String>() {
            @Override
            public void onComplete(String result) {
                url.accept(result);
            }

            @Override
            public void preExecute() {
            }
        });
    }

    /**
     * Play episode from file
     *
     * @param index episode
     */
    private void playEpisodeFromSave(final int index) {
        Intent intent = null;
        final Optional<File> videoFile = getEpisodeFile(index);

        if (videoFile.isPresent()) {
            final int mode = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("video_player_preference", "0"));
            if (mode == 0) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", videoFile.get()), "video/mp4");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (mode == 1) {
                intent = new Intent(getApplicationContext(), PlayerActivity.class);
                intent.putExtra("path", videoFile.get().getAbsolutePath());
            }
            startActivity(Objects.requireNonNull(intent));
        }
    }

    /**
     * Make text
     *
     * @param text
     */
    public void makeText(final String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    /**
     * Get episode file
     *
     * @param index
     * @return
     */
    public Optional<File> getEpisodeFile(final int index) {
        if (file.listFiles() != null) {
            return Arrays.stream(file.listFiles()).filter(file -> file.getName().substring(0, file.getName().lastIndexOf(".")).equals(String.valueOf(index))).findFirst();
        }
        return Optional.empty();
    }


    /**
     * Episode adapter
     */
    class EpisodeAdapter extends BaseAdapter {

        private final Context context;
        private final int episodes;

        public EpisodeAdapter(final int episodes, final Context context) {
            this.episodes = episodes;
            this.context = context;
        }

        @Override
        public int getCount() {
            return episodes;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(context).inflate(R.layout.episode_item_layout, parent, false);

            final TextView textView = convertView.findViewById(R.id.episode_name);
            final ImageView actionButton = convertView.findViewById(R.id.download_button);
            textView.setText("Episode: " + position);

            if (isEpisodeDownloaded(position)) {
                textView.setTextColor(0xFF30475e);
                actionButton.setImageResource(R.drawable.delete);
                actionButton.setOnClickListener(view ->
                        new IonAlert(SelectedActivity.this, IonAlert.WARNING_TYPE)
                                .setTitleText("Delete file?")
                                .setContentText("Won't be able to recover this file!")
                                .setConfirmText("Yes, delete it!")
                                .setConfirmClickListener(ionAlert -> {
                                    episodeAdapter.deleteItem(position);
                                    ionAlert.dismissWithAnimation();
                                }).setCancelText("Abort").setCancelClickListener(IonAlert::dismissWithAnimation)
                                .show());
            } else {
                textView.setTextColor(0xFFFFFFF);
                actionButton.setImageResource(R.drawable.download);
                actionButton.setOnClickListener(view -> getEpisode(position, 1, 0, false));
            }
            return convertView;
        }

        /**
         * Delete file
         *
         * @param index
         */
        public void deleteItem(final int index) {
            final Optional<File> videoFile = getEpisodeFile(index);
            if (videoFile.isPresent()) {
                Logger.log("Deleted: " + videoFile.get().delete(), Logger.LogType.INFO);
                notifyDataSetChanged();
            }
        }
    }
}

