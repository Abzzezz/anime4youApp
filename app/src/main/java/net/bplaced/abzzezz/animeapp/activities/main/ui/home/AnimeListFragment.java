/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:16
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import ga.abzzezz.util.logging.Logger;
import id.ionbit.ionalert.IonAlert;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.main.SelectedAnimeActivity;
import net.bplaced.abzzezz.animeapp.util.ImageUtil;
import net.bplaced.abzzezz.animeapp.util.InputDialogBuilder;
import net.bplaced.abzzezz.animeapp.util.file.OfflineImageLoader;
import net.bplaced.abzzezz.animeapp.util.scripter.DataBaseSearch;
import net.bplaced.abzzezz.animeapp.util.scripter.URLHandler;
import net.bplaced.abzzezz.animeapp.util.tasks.DataBaseTask;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;

import java.util.List;

public class AnimeListFragment extends Fragment {

    private final DataBaseSearch dataBaseSearch = new DataBaseSearch();
    private AnimeAdapter animeAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.anime_list_layout, container, false);

        GridView gridView = root.findViewById(R.id.anime_grid);
        this.animeAdapter = new AnimeAdapter(AnimeAppMain.getInstance().getAnimeSaver().getList(), getActivity());
        gridView.setAdapter(animeAdapter);

        /**
         * Set onclick listener, if clicked pass information through to selected anime.
         */
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getActivity(), SelectedAnimeActivity.class);
            String[] pass = AnimeAppMain.getInstance().getAnimeSaver().getAll(position);
            getInformation(pass, intent);
        });
        /**
         * Set on long hold listener, then block the old one
         */
        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            new IonAlert(getActivity(), IonAlert.WARNING_TYPE)
                    .setTitleText("Remove file?")
                    .setContentText("Won't be able to recover this file!")
                    .setConfirmText("Yes,delete it!")
                    .setConfirmClickListener(ionAlert -> {
                        animeAdapter.removeItem(position);
                        ionAlert.dismissWithAnimation();
                    }).setCancelText("Abort").setCancelClickListener(IonAlert::dismissWithAnimation)
                    .show();
            return true;
        });

        FloatingActionButton addAidButton = root.findViewById(R.id.add_aid);
        addAidButton.setOnClickListener(v -> {
            EditText editText = new EditText(getActivity());
            InputDialogBuilder inputDialogBuilder = new InputDialogBuilder(new InputDialogBuilder.InputDialogListener() {
                @Override
                public void onDialogInput(String text) {
                    animeAdapter.addItem(text);
                }

                @Override
                public void onDialogDenied() {
                }
            });
            inputDialogBuilder.showInput("Enter AID", "Enter AID to add anime", getActivity());
        });

        return root;
    }


    private void getInformation(String[] savedInformation, Intent intent) {
        String[] information = new String[6];

        information[0] = savedInformation[0];
        information[2] = savedInformation[2];
        information[3] = savedInformation[3];
        //Add basic information from file
/*
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("offline_mode", false)) {
            extra = pass;
            }
 */
        new TaskExecutor().executeAsync(new DataBaseTask(savedInformation[3], dataBaseSearch, "\"Letzte\":\"", "\"Untertitel\":\"", "\"Jahr\":\""), new TaskExecutor.Callback<String[]>() {
            @Override
            public void onComplete(String[] result) {
                information[1] = result[0];
                information[4] = result[1];
                information[5] = result[2];

                intent.putExtra("anime_name", information[0]);
                intent.putExtra("anime_episodes", information[1]);
                intent.putExtra("anime_cover", information[2]);
                intent.putExtra("anime_aid", information[3]);
                intent.putExtra("anime_language", information[4]);
                intent.putExtra("anime_year", Integer.valueOf(information[5]));
                startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void preExecute() {
                Logger.log("Fetching anime information", Logger.LogType.INFO);
            }
        });
    }

    class AnimeAdapter extends BaseAdapter {

        private final Context context;
        private final List<String> string;

        public AnimeAdapter(List<String> string, Context context) {
            this.string = string;
            this.context = context;
        }

        @Override
        public int getCount() {
            return string.size();
        }

        @Override
        public Object getItem(int position) {
            return string.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            String imageURL = AnimeAppMain.getInstance().getAnimeSaver().getAll(position)[2];
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("offline_mode", false)) {
                OfflineImageLoader.loadImage(imageURL, AnimeAppMain.getInstance().getAnimeSaver().getAll(position)[3], imageView, getContext());
            } else {
                try {
                    Picasso.with(context).load(imageURL).resize(ImageUtil.dimensions[0], ImageUtil.dimensions[1]).into(imageView);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(position);
                }
            }
            imageView.setAdjustViewBounds(true);
            return imageView;
        }

        public void removeItem(int index) {
            string.remove(index);
            AnimeAppMain.getInstance().getAnimeSaver().remove(index);
            notifyDataSetChanged();
        }

        public void addItem(String item) {
            if (!URLHandler.isOnline(getActivity())) {
                Toast.makeText(context, "You are currently not connected to the internet, returning", Toast.LENGTH_LONG).show();
                return;
            }

            new TaskExecutor().executeAsync(new DataBaseTask(item, dataBaseSearch, "src=\\\"", "\"Letzte\":\"", "\"titel\":\""), new TaskExecutor.Callback<String[]>() {
                @Override
                public void onComplete(String[] result) {
                    string.add(item);
                    String[] information = new String[] {result[2], result[1], result[0].replaceAll("\\\\", ""), item};
                    AnimeAppMain.getInstance().getAnimeSaver().add(information);
                    notifyDataSetChanged();
                }

                @Override
                public void preExecute() {
                }
            });
        }

        public List<String> getString() {
            return string;
        }
    }

    /**
     * Debug
     */
    /*
    class FTPGetter extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                FTPClient ftpClient = new FTPClient();
                ftpClient.connect("abzzezz.bplaced.net");
                ftpClient.login("abzzezz_client", "AzA33EUSgU7KZvbj");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                for (String line : SplashScreen.saver.getList()) {
                    baos.write((line + "\n").getBytes());
                }
                byte[] bytes = baos.toByteArray();
                Logger.log("File uploaded: " + ftpClient.storeFile("/www/lists/" + new Random().nextInt() + ".txt", new ByteArrayInputStream(bytes)), Logger.LogType.INFO);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
     */
}
