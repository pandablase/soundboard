package com.example.sergi.grillcrewsoundboard;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.MyViewHolder> {

    private List<Sound> soundsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public Button button;

        public MyViewHolder(View view) {
            super(view);
            button = view.findViewById(R.id.gridButton);
        }
    }

    public ButtonAdapter(List<Sound> soundsList) {
        this.soundsList = soundsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sound_grid_element, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Sound sound = soundsList.get(position);
        holder.button.setText(sound.getTitle());
    }

    @Override
    public int getItemCount() {
        return soundsList.size();
    }
}
