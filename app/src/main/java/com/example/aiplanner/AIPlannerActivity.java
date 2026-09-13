package com.example.aiplanner;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AIPlannerActivity extends AppCompatActivity {

    private ImageView aiLogo;
    private Animation aiAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setSelectedItemId(R.id.nav_ai);

        aiLogo = findViewById(R.id.aiLogo);

        aiAnimation = AnimationUtils.loadAnimation(
                this,
                R.anim.rotate
        );

        aiLogo.startAnimation(aiAnimation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (aiLogo != null) {
            aiLogo.clearAnimation();
        }
    }
}