package com.example.personalplanner.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalplanner.ai.FeatureBuilder;
import com.example.personalplanner.ai.OnnxModel;
import com.example.personalplanner.database.entity.Task;

public class OnnxTestActivity extends AppCompatActivity {

    private static final String TAG =
            "ONNX_TEST";

    private OnnxModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        new Thread(() -> {

            try {

                model =
                        new OnnxModel(
                                getApplicationContext()
                        );

                long now =
                        System.currentTimeMillis();

                Task task =
                        new Task(
                                "ONNX Test Task",
                                "Testing AI model",
                                5,
                                now + 24 * 60 * 60 * 1000,
                                120,
                                false,
                                1,
                                now,
                                now,
                                0,
                                false,
                                "NONE"
                        );

                float[] features =
                        FeatureBuilder.buildFeatures(
                                task,
                                19,
                                120,
                                19,
                                2,
                                false
                        );

                Log.d(
                        TAG,
                        "FEATURE COUNT = "
                                + features.length
                );

                for (int i = 0;
                     i < features.length;
                     i++) {

                    Log.d(
                            TAG,
                            "Feature[" + i + "] = "
                                    + features[i]
                    );
                }

                float score =
                        model.predict(features);

                Log.d(
                        TAG,
                        "PREDICTED SCORE = "
                                + score
                );

                runOnUiThread(() ->
                        Toast.makeText(
                                this,
                                "ONNX Score = "
                                        + score,
                                Toast.LENGTH_LONG
                        ).show()
                );

            } catch (Exception e) {

                Log.e(
                        TAG,
                        "ONNX TEST FAILED",
                        e
                );

                runOnUiThread(() ->
                        Toast.makeText(
                                this,
                                "ONNX TEST FAILED",
                                Toast.LENGTH_LONG
                        ).show()
                );
            }
        }).start();
    }

    @Override
    protected void onDestroy() {

        if (model != null) {
            model.close();
        }

        super.onDestroy();
    }
}