package com.example.sergi.grillcrewsoundboard;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class Player extends Fragment {

    private List<Sound> soundList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SoundAdapter soundAdapter;
    private MediaPlayer mediaPlayer = null;
    private SeekBar seekBar = null;
    private Handler mSeekbarUpdateHandler = new Handler();
    private Runnable mUpdateSeekbar = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null) seekBar.setProgress(mediaPlayer.getCurrentPosition());
            mSeekbarUpdateHandler.postDelayed(this, 50);
        }
    };
    private int seekTime = 20000;

    public static Player newInstance() {
        return new Player();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_player, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                ImageButton button = view.findViewById(R.id.pausePlayButton);
                button.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                view.findViewById(R.id.mediaControlPanel).setVisibility(View.GONE);
            }
        });

        soundAdapter = new SoundAdapter(soundList);
        RecyclerView.LayoutManager sLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(sLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(soundAdapter);

        seekBar = view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        if (soundList.size() == 0) prepareSoundData();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this.getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View listView, int position) {
                Sound sound = soundList.get(position);
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(sound.getFile().getFileDescriptor(), sound.getFile().getStartOffset(), sound.getFile().getLength());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    view.findViewById(R.id.mediaControlPanel).setVisibility(View.VISIBLE);
                    ImageButton button = view.findViewById(R.id.pausePlayButton);
                    button.setImageResource(R.drawable.ic_pause_white_24dp);
                    TextView textTitle = view.findViewById(R.id.mediaTextTitle);
                    textTitle.setText(sound.getTitle());
                    TextView textArtist = view.findViewById(R.id.mediaTextArtist);
                    textArtist.setText(sound.getArtist());
                    seekBar.setMax(mediaPlayer.getDuration());
                    mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        view.findViewById(R.id.pausePlayButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    ImageButton button = view.findViewById(R.id.pausePlayButton);
                    button.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                    mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
                } else {
                    mediaPlayer.start();
                    ImageButton button = view.findViewById(R.id.pausePlayButton);
                    button.setImageResource(R.drawable.ic_pause_white_24dp);
                    mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);
                }
            }
        });

        view.findViewById(R.id.rewindButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    if (currentPosition - seekTime >= 0) {
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.seekTo(currentPosition - seekTime);
                            seekBar.setProgress(currentPosition - seekTime);
                        }
                        else {
                            mediaPlayer.seekTo(currentPosition - seekTime);
                        }
                    } else {
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.seekTo(0);
                            seekBar.setProgress(0);
                        }
                        else {
                            mediaPlayer.seekTo(0);
                        }
                    }
                }
            }
        });

        view.findViewById(R.id.forwardButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    if (currentPosition == 0 && !mediaPlayer.isPlaying()) return;
                    if (currentPosition + seekTime <= mediaPlayer.getDuration()) {
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.seekTo(currentPosition + seekTime);
                            seekBar.setProgress(currentPosition + seekTime);
                        }
                        else {
                            mediaPlayer.seekTo(currentPosition + seekTime);
                        }
                    } else {
                        mediaPlayer.stop();
                        ImageButton button = getView().findViewById(R.id.pausePlayButton);
                        button.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                        mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
                        getView().findViewById(R.id.mediaControlPanel).setVisibility(View.GONE);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mediaPlayer == null) mediaPlayer = new MediaPlayer();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mediaPlayer.isPlaying()) mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void prepareSoundData() {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        Field[] fields = R.raw.class.getFields();
        for(Field f: fields) {
            if (f.getName().startsWith("playersound")) {
                final AssetFileDescriptor afd = getResources().openRawResourceFd(getResources().getIdentifier(f.getName(),"raw", this.getContext().getPackageName()));
                mmr.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                soundList.add(new Sound(mmr.extractMetadata(mmr.METADATA_KEY_TITLE), mmr.extractMetadata(mmr.METADATA_KEY_ARTIST), mmr.extractMetadata(mmr.METADATA_KEY_ALBUM), afd));
            }
        }

        soundAdapter.notifyDataSetChanged();
    }
}
