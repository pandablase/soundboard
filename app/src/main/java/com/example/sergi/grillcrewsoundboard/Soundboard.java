package com.example.sergi.grillcrewsoundboard;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class Soundboard extends Fragment {

    private List<Sound> soundList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ButtonAdapter buttonAdapter;
    private MediaPlayer mediaPlayer = null;

    public static Fragment newInstance() {
        return new Soundboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_soundboard, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        mediaPlayer = new MediaPlayer();

        buttonAdapter = new ButtonAdapter(soundList);
        RecyclerView.LayoutManager sLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(sLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(16), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(buttonAdapter);

        if (soundList.size() == 0) prepareSoundData();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this.getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View gridView, int position) {
                Sound sound = soundList.get(position);
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(sound.getFile().getFileDescriptor(), sound.getFile().getStartOffset(), sound.getFile().getLength());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mediaPlayer == null) mediaPlayer = new MediaPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer.isPlaying()) mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void prepareSoundData() {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        Field[] fields = R.raw.class.getFields();
        for(Field f: fields) {
            if (!f.getName().startsWith("playersound")) {
                final AssetFileDescriptor afd = getResources().openRawResourceFd(getResources().getIdentifier(f.getName(),"raw", this.getContext().getPackageName()));
                mmr.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                soundList.add(new Sound(mmr.extractMetadata(mmr.METADATA_KEY_TITLE), mmr.extractMetadata(mmr.METADATA_KEY_ARTIST), mmr.extractMetadata(mmr.METADATA_KEY_ALBUM), afd));
            }
        }

        buttonAdapter.notifyDataSetChanged();
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
