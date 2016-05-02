package com.groupone.anup.bossmusicplayer;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SongListFragment extends Fragment {

    ListView songListView;
    SongListAdapter listAdapter;
    SongManager songManager;
    ArrayList<SongModel> songList;
    Context context = getActivity();

    static final String SONG_INDEX = "uniqueSongPosition";

    public SongListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);

        songListView = (ListView) view.findViewById(R.id.list_view);

        listAdapter = new SongListAdapter(getActivity(),MainActivity.SONG_LIST);

        songListView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(),PlayerActivity.class);
                intent.putExtra(SONG_INDEX,position);
                startActivity(intent);
            }
        });

        return view;
    }

}
