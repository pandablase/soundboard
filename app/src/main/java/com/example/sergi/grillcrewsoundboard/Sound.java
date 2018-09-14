package com.example.sergi.grillcrewsoundboard;

import android.content.res.AssetFileDescriptor;

public class Sound {
    private String title, artist, album;
    private AssetFileDescriptor file;

    public Sound() {
    }

    public Sound(String title, String artist, String album, AssetFileDescriptor file) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public AssetFileDescriptor getFile() {
        return file;
    }

    public void setFile(AssetFileDescriptor file) {
        this.file = file;
    }
}
