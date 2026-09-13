package com.example.aiplanner;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // پیدا کردن Bottom Navigation داخل include
        View bottomNavView = findViewById(R.id.bottom_nav);

        bottomNavigationView =
                bottomNavView.findViewById(R.id.bottom_navigation);

        // صفحه اولیه
        if (savedInstanceState == null) {
            showFragment(new HomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                showFragment(new HomeFragment());
                return true;
            }

            if (itemId == R.id.nav_ai) {
                showFragment(new AIPlannerFragment());
                return true;
            }

            if (itemId == R.id.nav_routine) {
                showFragment(new RoutineFragment());
                return true;
            }

            if (itemId == R.id.nav_analysis) {
                showFragment(new AnalysticsFragment());
                return true;
            }

            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private void showFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}