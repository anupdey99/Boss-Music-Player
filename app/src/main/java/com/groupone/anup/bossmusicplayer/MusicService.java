package com.groupone.anup.bossmusicplayer;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;

import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Anup on 11-Apr-16.
 */
public class MusicService extends Service
        implements  MediaPlayer.OnPreparedListener,
                    MediaPlayer.OnErrorListener,
                    MediaPlayer.OnCompletionListener{


    Callbacks activity;
    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<SongModel> songList = MainActivity.SONG_LIST;
    //current position
    private int songPosition;
    //binder
    private final IBinder musicBind = new MusicBinder();
    //title of current song
    private String nowPlayingSongTitle = "";
    //notification id
    private static final int NOTIFY_ID=1;
    //shuffle flag and random
    private boolean shuffle=false;
    private Random rand;

    Handler mHandler = new Handler();
    Utilities utils = new Utilities();

    //binder
    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //check if playback has reached the end of a track
        if(player.getCurrentPosition()>0){
            mp.reset();
            playNext();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize position
        songPosition = 0;
        //random
        rand = new Random();
        //create player
        player = new MediaPlayer();
        //initialize
        initMusicPlayer();
    }



    @Override
    public void onDestroy() {
        stopForeground(true);
    }


    private void initMusicPlayer() {

        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

    }



    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("MUSIC PLAYER", "Playback Error");
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        //start playback
        mp.start();
        //notification
        Intent notIntent = new Intent(this, PlayerActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(nowPlayingSongTitle)
                .setOngoing(true)
                .setContentTitle("Boss Music Player")
                .setContentText(nowPlayingSongTitle);
        Notification notification = builder.build();
        startForeground(NOTIFY_ID, notification);

    }

    //play a song
    public void playSong(){
        //play
        player.reset();
        //get song
        SongModel nowPlaying = songList.get(songPosition);
        //get title
        nowPlayingSongTitle = nowPlaying.getTrack();
        //get id
        long nowPlayingSongID = nowPlaying.getSongID();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                nowPlayingSongID);
        //set the data source
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();


        mHandler.postDelayed(mUpdateTimeTask, 1000);

    }

    //set the song
    public void setSong(int songIndex){
        songPosition = songIndex;
    }
    public int getSongPosition(){
        return songPosition;
    }


    //playback methods
    public int getPosition(){
        return player.getCurrentPosition();
    }

    public int getDuration(){

        return player.getDuration();
    }

    public boolean isPlaying(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int position){
        player.seekTo(position);
    }

    public void runPlayer(){
        player.start();
    }

    //skip to previous track
    public void playPrev(){
        songPosition--;
        if(songPosition<0) songPosition=songList.size()-1;
        playSong();
    }

    //skip to next
    public void playNext(){
        if(shuffle){
            int newSong = songPosition;
            while(newSong==songPosition){
                newSong=rand.nextInt(songList.size());
            }
            songPosition=newSong;
        }
        else{
            songPosition++;
            if(songPosition>=songList.size()) songPosition=0;
        }
        playSong();
    }


    //toggle shuffle
    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }


    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = player.getDuration();
            long currentDuration = player.getCurrentPosition();

            // Displaying Total Duration time
            String tDuration = utils.milliSecondsToTimer(totalDuration);
            // Displaying time completed playing
             String cDuration = utils.milliSecondsToTimer(currentDuration);

            // Updating progress bar
            int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            //songProgressBar.setProgress(progress);
            activity.updateClient(tDuration,cDuration,progress,songPosition);
            // Running this thread after 1000 milliseconds
            mHandler.postDelayed(this, 1000);



        }
    };





    //Here Activity register to the service as Callbacks client
    public void registerClient(Activity activity){
        this.activity = (Callbacks)activity;
    }



    //callbacks interface for communication with service clients!
    public interface Callbacks{
        public void updateClient(String duration,String currentDuration, int progress,int songPosition  );


    }


}
