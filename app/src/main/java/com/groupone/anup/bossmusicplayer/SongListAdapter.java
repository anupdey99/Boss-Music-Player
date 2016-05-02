package com.groupone.anup.bossmusicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anup on 10-Apr-16.
 */
public class SongListAdapter extends ArrayAdapter<SongModel> {

    Context context;
    ArrayList<SongModel> songList ;

    public SongListAdapter(Context context, ArrayList<SongModel> objects) {
        super(context, R.layout.media_list_item, objects);
        this.context = context;
        this.songList = objects;
    }

    static class ViewHolder {
        ImageView playIC;
        TextView title,description;


    }

    // generate view by myself
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {

            // to convert my xml custom layout into view!
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.media_list_item, null);

            viewHolder = new ViewHolder();
            viewHolder.playIC = (ImageView) convertView.findViewById(R.id.play_ic);
            viewHolder.title = (TextView) convertView.findViewById(R.id.songTitleID);
            viewHolder.description= (TextView) convertView.findViewById(R.id.songDescriptionID);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        //viewHolder.playIC.setImageBitmap(songList.get(position).getBitmap());
        viewHolder.title.setText(songList.get(position).getTrack());
        viewHolder.description.setText(songList.get(position).getArtist()+","+songList.get(position).getAlbum());


        return convertView;
    }

}
