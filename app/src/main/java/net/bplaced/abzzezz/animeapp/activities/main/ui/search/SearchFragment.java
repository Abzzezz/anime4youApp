/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 23.06.20, 18:16
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import com.squareup.picasso.Picasso;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.bplaced.abzzezz.animeapp.util.tasks.myanimelist.MyAnimeListSearchTask;
import net.bplaced.abzzezz.animeapp.util.ui.ImageUtil;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_search, container, false);
        final ListView listView = root.findViewById(R.id.simple_list_view);
        final SearchView showSearch = root.findViewById(R.id.show_search_view);
        //Set adapter
        listView.setAdapter(new SearchAdapter(new ArrayList<>(), root.getContext()));

        showSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (URLUtil.isOffline(getContext())) return true;

                final SearchAdapter searchAdapter = (SearchAdapter) listView.getAdapter();
                searchAdapter.getEntries().clear();

                new MyAnimeListSearchTask(query).executeAsync(new TaskExecutor.Callback<List<Show>>() {
                    @Override
                    public void onComplete(List<Show> result) {
                        searchAdapter.getEntries().addAll(result);
                        searchAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void preExecute() {

                    }
                });
                showSearch.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return root;
    }

    static class SearchAdapter extends BaseAdapter {
        private final List<Show> entries;
        private final Context context;

        public SearchAdapter(final List<Show> entries, final Context context) {
            this.entries = entries;
            this.context = context;
        }

        @Override
        public int getCount() {
            return entries.size();
        }

        @Override
        public Object getItem(int position) {
            return entries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);

                final Show showAtIndex = (Show) getItem(position);

                convertView.setOnClickListener(listener -> {
                    try {
                        AnimeAppMain.getInstance().getShowSaver().addShow(showAtIndex);
                        Toast.makeText(context, "Added show!", Toast.LENGTH_SHORT).show();
                    } catch (final JSONException e) {
                        e.printStackTrace();
                    }
                });

                final TextView showTitle = convertView.findViewById(R.id.show_title_text_view_search);
                final TextView showEpisodes = convertView.findViewById(R.id.show_episodes_text_view_search);
                final TextView showProvider = convertView.findViewById(R.id.show_provider_text_view_search);
                final ImageView imageView = convertView.findViewById(R.id.show_cover_image_view_search);
                //Load image from url into the image view
                Picasso.with(context)
                        .load(showAtIndex.getImageURL())
                        .resize(ImageUtil.IMAGE_COVER_DIMENSIONS[0], ImageUtil.IMAGE_COVER_DIMENSIONS[1])
                        .into(imageView);

                showTitle.append(showAtIndex.getShowTitle()); //append the show's title
                showEpisodes.append(String.valueOf(showAtIndex.getEpisodeCount())); //Append the episode count
                showProvider.append("MAL"); //Append the provider

            }
            return convertView;
        }

        public List<Show> getEntries() {
            return entries;
        }
    }
}
