package com.example.sergi.grillcrewsoundboard;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FragmentManager fragmentManager = null;
    private Fragment soundboard = null;
    private Fragment player = null;
    private Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.bottomNav = findViewById(R.id.navigationView);

        this.fragmentManager = getFragmentManager();
        Fragment soundboardFragment = this.fragmentManager.findFragmentByTag("soundboard_fragment");
        Fragment playerFragment = this.fragmentManager.findFragmentByTag("player_fragment");
        if(soundboardFragment == null) {
            if(playerFragment == null) {
                this.soundboard = Soundboard.newInstance();
                this.player = Player.newInstance();
                this.fragment = this.soundboard;
                FragmentTransaction ft = this.fragmentManager.beginTransaction();
                ft.add(R.id.container, this.fragment, "soundboard_fragment");
                ft.commit();
            }
            else {
                this.soundboard = Soundboard.newInstance();
                this.player = playerFragment;
                this.fragment = playerFragment;
                FragmentTransaction ft = this.fragmentManager.beginTransaction();
                ft.remove(this.fragment);
                ft.add(R.id.container, this.fragment, "player_fragment");
                ft.commit();
            }
        }
        else {
            this.soundboard = soundboardFragment;
            this.player = Player.newInstance();
            this.fragment = soundboardFragment;
            FragmentTransaction ft = this.fragmentManager.beginTransaction();
            ft.remove(this.fragment);
            ft.add(R.id.container, this.fragment, "soundboard_fragment");
            ft.commit();
        }

        if(this.player == null) this.player = Player.newInstance();

        this.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                openFragment(item);
                return true;
            }
        });

    }

    protected void openFragment(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_soundboard:
                if (this.fragment != null && this.fragment != this.soundboard) {
                    this.fragment = this.soundboard;
                    FragmentTransaction ft = this.fragmentManager.beginTransaction();
                    ft.remove(this.player);
                    ft.add(R.id.container, this.fragment, "soundboard_fragment");
                    ft.commit();
                }
                break;
            case R.id.navigation_player:
                if (this.fragment != null && this.fragment != this.player) {
                    this.fragment = this.player;
                    FragmentTransaction ft = this.fragmentManager.beginTransaction();
                    ft.remove(this.soundboard);
                    ft.add(R.id.container, this.fragment, "player_fragment");
                    ft.commit();
                }
                break;
        }
    }
}
