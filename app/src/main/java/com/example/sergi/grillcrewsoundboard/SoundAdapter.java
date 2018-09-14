package com.example.sergi.grillcrewsoundboard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.MyViewHolder> {

    private List<Sound> soundsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, artist, album;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            artist = view.findViewById(R.id.artist);
            album = view.findViewById(R.id.album);
        }
    }

    public SoundAdapter(List<Sound> soundsList) {
        this.soundsList = soundsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sound_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Sound sound = soundsList.get(position);
        holder.title.setText(sound.getTitle());
        holder.artist.setText(sound.getArtist());
        holder.album.setText(sound.getAlbum());
    }

    @Override
    public int getItemCount() {
        return soundsList.size();
    }
}
