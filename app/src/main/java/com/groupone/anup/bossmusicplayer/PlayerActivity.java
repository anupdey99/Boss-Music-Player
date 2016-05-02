package com.groupone.anup.bossmusicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity
        implements View.OnClickListener,MusicService.Callbacks,
        SeekBar.OnSeekBarChangeListener {

    TextView songTitle,songArtist,songAlbum,songDuration,songCurrentDuration;
    SeekBar seekBar;
    ImageButton previousBtn, play_pauseBtn, nextBtn;
    ProgressBar progressBar;

    //service
    private MusicService musicService;
    private Intent playIntent;
    //binding
    private boolean musicBound = false;

    //activity and playback pause flags
    private boolean paused=false, playbackPaused=false;
    private boolean isPlayerRunning = false;

    private int songPosition;

    private ArrayList<SongModel> songArrayList = MainActivity.SONG_LIST;


    // test

    private boolean state_play = false;
    private boolean state_pause = false;
    int seekPosition = 0;
    int progress = 0;
    int totalDuration;
    private Handler mHandler = new Handler();
    Utilities utilities = new Utilities();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPlayer);
        toolbar.setTitle("Now Playing");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        songTitle = (TextView) findViewById(R.id.songTitle);
        songArtist = (TextView) findViewById(R.id.songArtist);
        songAlbum = (TextView) findViewById(R.id.songAlbum);
        seekBar = (SeekBar) findViewById(R.id.seekBarID);
        previousBtn = (ImageButton) findViewById(R.id.prevBtn);
        play_pauseBtn = (ImageButton) findViewById(R.id.play_pauseBtn);
        nextBtn = (ImageButton) findViewById(R.id.nextBtn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        songDuration = (TextView) findViewById(R.id.endText);
        songCurrentDuration = (TextView) findViewById(R.id.startText);

        // get Song unique ID
        Intent intent = getIntent();
        songPosition = intent.getIntExtra(SongListFragment.SONG_INDEX,-1);





        play_pauseBtn.setOnClickListener(this);
        previousBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(0);
        seekBar.setMax(100);



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_shuffle) {
            musicService.setShuffle();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    //start and bind the service when the activity starts
    @Override
    protected void onResume() {
        super.onResume();
        if(playIntent == null){
            playIntent = new Intent(PlayerActivity.this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }




    @Override
    protected void onPause() {
        //stopService(playIntent);
        //musicService = null;
        unbindService(musicConnection);
        super.onPause();
    }






    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            musicService = ((MusicService.MusicBinder) service).getService();
            musicBound = true;
            musicService.registerClient(PlayerActivity.this);
            musicService.setSong(songPosition);
            play_pauseBtn.performClick();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;


        }
    };














 private void showSongDetails(){

     songTitle.setText(songArrayList.get(songPosition).getTrack());
     songArtist.setText(songArrayList.get(songPosition).getArtist());
     songAlbum.setText(songArrayList.get(songPosition).getAlbum());
 }


    private void playNow(){

        musicService.playSong();
        musicService.runPlayer();



    }





    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.play_pauseBtn :


                if ( !musicService.isPlaying()){

                    if(seekPosition >= 0 ){
                        //musicService.seek(seekPosition);
                        seekPosition = 0;
                        musicService.runPlayer();
                    }else {
                        playNow();
                    }




                    play_pauseBtn.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
                }else if (musicService.isPlaying()){
                    musicService.pausePlayer();
                    seekPosition = musicService.getPosition();
                    play_pauseBtn.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
                    stopService(playIntent);
                }

                break;
            case R.id.nextBtn:
                musicService.playNext();
                break;
            case R.id.prevBtn:
                musicService.playPrev();
                break;

            default:
                break;
        }



    }

    @Override
    public void updateClient( String duration,String currentDuration, int progress, int songPosition ) {

            songDuration.setText(duration);
            songCurrentDuration.setText(currentDuration);
            this.progress = progress;
            if (this.songPosition!=songPosition){
                this.songPosition = songPosition;
                showSongDetails();
            }

            mHandler.postDelayed(updateSeekBar,1000);


    }


//Make sure you update Seekbar on UI thread
    Runnable updateSeekBar = new Runnable() {
    @Override
    public void run() {

        seekBar.setProgress(progress);
        mHandler.postDelayed(this,1000);
    }
};


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


           if (fromUser){
               musicService.seek(utilities.progressToTimer(progress,musicService.getDuration()) );

           }


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {


    }
}
