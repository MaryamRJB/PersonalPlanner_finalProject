package com.example.aiplanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import adapter.CalendarAdapter;
import model.DataGenerator;
import model.DayModel;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView calendar = (RecyclerView) findViewById(R.id.calendar);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);

        calendar.setLayoutManager(layoutManager);
        DataGenerator dataGenerator = new DataGenerator();
        List<DayModel> days_curr_month = dataGenerator.generateDaysForCurrMonth();

        int today_position=-1;
        for(int i = 0;i<days_curr_month.size();i++){
            DayModel day = days_curr_month.get(i);
            if (day.isToday()){
                today_position = i;
            }
        }

        CalendarAdapter calendarAdapter = new CalendarAdapter(days_curr_month);
        calendar.setAdapter(calendarAdapter);
        calendar.scrollToPosition(today_position);

        calendarAdapter.setOnClickListener(new CalendarAdapter.OnDayClickListener() {
            @Override
            public void onClick(int position, DayModel day) {
                if(day.isSelected()){
                    return;
                }
                for(int i = 0;i<days_curr_month.size();i++){
                    if(days_curr_month.get(i).isSelected()){
                        days_curr_month.get(i).setSelected(false);
                        Log.d("Select","Selected:"+i+days_curr_month.get(i).isSelected());
                        break;
                    }
                }
                Log.d("Select","Selected:"+position+days_curr_month.get(position).isSelected());
                day.setSelected(true);
                Log.d("Select","Selected:"+position+days_curr_month.get(position).isSelected());
                calendarAdapter.notifyDataSetChanged();
            }
        });

        View profileHeader = findViewById(R.id.profile_header);
        ShapeableImageView btnProfile = profileHeader.findViewById(R.id.imgProfile);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SignInActivity.this);
                startActivity(intent);
            }
        });




    }
}