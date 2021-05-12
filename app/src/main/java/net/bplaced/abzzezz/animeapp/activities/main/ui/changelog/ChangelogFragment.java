/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 06.04.21, 18:09
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.changelog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.util.Constant;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;

import java.net.URL;

public class ChangelogFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.layout_simple_list, container, false);
        new TaskExecutor().executeAsync(() -> URLUtil.collectArray(new URL(Constant.APP_CHANGELOG_TXT)), new TaskExecutor.Callback<String[]>() {
            @Override
            public void onComplete(final String[] result) {
                requireActivity().runOnUiThread(() -> {
                    final ListView listView = getActivity().findViewById(R.id.simple_list_view);
                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
                    arrayAdapter.addAll(result);
                    listView.setAdapter(arrayAdapter);
                });
            }

            @Override
            public void preExecute() {

            }
        });
        return root;
    }
}
