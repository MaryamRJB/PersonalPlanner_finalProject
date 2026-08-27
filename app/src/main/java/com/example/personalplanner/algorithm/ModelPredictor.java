package com.example.personalplanner.algorithm;

import android.content.Context;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.FloatBuffer;

public class ModelPredictor {

    private static final String MODEL_FILE =
            "personal_planner_random_forest.onnx";

    private final OrtEnvironment environment;
    private final OrtSession session;

    public ModelPredictor(Context context) throws Exception {

        environment = OrtEnvironment.getEnvironment();

        byte[] modelBytes = loadModel(
                context,
                MODEL_FILE
        );

        session = environment.createSession(
                modelBytes,
                new OrtSession.SessionOptions()
        );
    }

    private byte[] loadModel(
            Context context,
            String fileName
    ) throws Exception {

        InputStream inputStream =
                context.getAssets().open(fileName);

        byte[] buffer =
                new byte[inputStream.available()];

        int offset = 0;
        int bytesRead;

        while (offset < buffer.length &&
                (bytesRead = inputStream.read(
                        buffer,
                        offset,
                        buffer.length - offset
                )) != -1) {

            offset += bytesRead;
        }

        inputStream.close();

        return buffer;
    }

    public float predict(float[] features)
            throws Exception {

        if (features.length != 14) {
            throw new IllegalArgumentException(
                    "Expected 14 features, got "
                            + features.length
            );
        }

        float[][] input = new float[][]{
                features
        };

        OnnxTensor tensor =
                OnnxTensor.createTensor(
                        environment,
                        input
                );

        String inputName =
                session.getInputNames()
                        .iterator()
                        .next();

        OrtSession.Result result =
                session.run(
                        java.util.Collections.singletonMap(
                                inputName,
                                tensor
                        )
                );

        Object output =
                result.get(0).getValue();

        float score = extractScore(output);

        tensor.close();
        result.close();

        return score;
    }

    private float extractScore(Object output) {

        if (output instanceof float[][]) {

            float[][] values =
                    (float[][]) output;

            return values[0][0];
        }

        if (output instanceof float[]) {

            float[] values =
                    (float[]) output;

            return values[0];
        }

        throw new IllegalStateException(
                "Unexpected ONNX output type: "
                        + output.getClass()
                        .getName()
        );
    }

    public void close() throws Exception {
        session.close();
    }
}