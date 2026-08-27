package com.example.personalplanner.ai;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Collections;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OnnxValue;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;

public class OnnxModel {

    private static final String TAG = "ONNX_MODEL";

    private static final String MODEL_FILE =
            "personal_planner_random_forest.onnx";

    private final OrtEnvironment environment;
    private final OrtSession session;

    public OnnxModel(Context context) throws Exception {

        environment = OrtEnvironment.getEnvironment();

        File modelFile = copyModelToInternalStorage(context);

        OrtSession.SessionOptions options =
                new OrtSession.SessionOptions();

        session = environment.createSession(
                modelFile.getAbsolutePath(),
                options
        );

        Log.d(TAG, "ONNX MODEL LOADED SUCCESSFULLY");

        Log.d(TAG,
                "Input names: " +
                        session.getInputNames());

        Log.d(TAG,
                "Output names: " +
                        session.getOutputNames());
    }

    private File copyModelToInternalStorage(
            Context context) throws Exception {

        File modelFile = new File(
                context.getFilesDir(),
                MODEL_FILE
        );

        if (!modelFile.exists()) {

            InputStream inputStream =
                    context.getAssets().open(MODEL_FILE);

            FileOutputStream outputStream =
                    new FileOutputStream(modelFile);

            byte[] buffer = new byte[4096];

            int length;

            while ((length =
                    inputStream.read(buffer)) > 0) {

                outputStream.write(
                        buffer,
                        0,
                        length
                );
            }

            outputStream.close();
            inputStream.close();
        }

        return modelFile;
    }

    public float predict(float[] features)
            throws Exception {

        if (features == null ||
                features.length != 14) {

            throw new IllegalArgumentException(
                    "Model requires exactly 14 features"
            );
        }

        float[][] inputData =
                new float[][]{
                        features
                };

        OnnxTensor inputTensor =
                OnnxTensor.createTensor(
                        environment,
                        FloatBuffer.wrap(features),
                        new long[]{1, 14}
                );

        String inputName =
                session.getInputNames()
                        .iterator()
                        .next();

        try (inputTensor;
             OrtSession.Result result =
                     session.run(
                             Collections.singletonMap(
                                     inputName,
                                     inputTensor
                             ))) {

            OnnxValue output =
                    result.get(0);

            Object value =
                    output.getValue();

            if (value instanceof float[][]) {

                float[][] resultArray =
                        (float[][]) value;

                return resultArray[0][0];

            } else if (value instanceof float[]) {

                float[] resultArray =
                        (float[]) value;

                return resultArray[0];

            } else {

                throw new Exception(
                        "Unexpected ONNX output type: "
                                + value.getClass()
                );
            }
        }
    }

    public void close() {

        try {

            session.close();

        } catch (Exception e) {

            Log.e(
                    TAG,
                    "Error closing ONNX session",
                    e
            );
        }
    }
}