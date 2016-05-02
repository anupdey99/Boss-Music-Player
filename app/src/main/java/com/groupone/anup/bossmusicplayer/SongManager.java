package com.groupone.anup.bossmusicplayer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;


/**
 * Created by Anup on 10-Apr-16.
 */
public class SongManager extends AsyncTask<Void, Void, ArrayList<SongModel>>  {


    private Context context ;
    private ProgressDialog progressDialog;



    private ArrayList<SongModel> songArrayList = new ArrayList<SongModel>();
    private MainActivity activity ;

    public SongManager(MainActivity context) {
        this.context = context;
        this.activity= context;
    }



    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context,ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected ArrayList<SongModel> doInBackground(Void... params) {

        try {
            initLayout();
            return songArrayList;
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    protected void onPostExecute(ArrayList<SongModel> list) {
        if(list != null){
            activity.loadAllSongFragment();
        }
        progressDialog.dismiss();


    }

    private void initLayout() {
        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = { MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media._ID};
        final String where = MediaStore.Audio.Media.IS_MUSIC + "=1";
        final Cursor cursor = context.getContentResolver().query(uri,
                cursor_cols, where, null, null);

        while (cursor.moveToNext()) {
            String artist = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            String album = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            String track = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String data = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            Long albumId = cursor.getLong(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

            int duration = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
            long songID = cursor.getLong(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media._ID));


            Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
            /*
            //Logger.debug(albumArtUri.toString());
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        context.getContentResolver(), albumArtUri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);

            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.audio_file);
            } catch (IOException e) {

                e.printStackTrace();
            }

            */

            SongModel songModel = new SongModel();
            songModel.setSongID(songID);
            songModel.setArtist(artist);
            songModel.setAlbum(album);
            songModel.setTrack(track);
            songModel.setData(data);
            songModel.setAlbumId(albumId);
            songModel.setDuration(duration);
            songModel.setAlbumArtUri(albumArtUri);

            //songArrayList.add(songModel);
            MainActivity.SONG_LIST.add(songModel);
        }

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj,
                null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
