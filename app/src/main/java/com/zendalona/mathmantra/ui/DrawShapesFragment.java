package com.zendalona.mathmantra.ui;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier;
import com.zendalona.mathmantra.R;
import com.zendalona.mathmantra.databinding.DialogResultBinding;
import com.zendalona.mathmantra.databinding.FragmentDirectionBinding;
import com.zendalona.mathmantra.databinding.FragmentDrawShapesBinding;
import com.zendalona.mathmantra.utils.RandomValueGenerator;
import com.zendalona.mathmantra.utils.StatusTextView;
import com.zendalona.mathmantra.utils.StrokeManager;
import com.zendalona.mathmantra.utils.TTSUtility;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class DrawShapesFragment extends Fragment
        implements StrokeManager.DownloadedModelsChangedListener {
    private FragmentDrawShapesBinding binding;
    private RandomValueGenerator random;
    private TTSUtility tts;
    private static final String TAG = "DrawFragment";
    private static final String SHAPES_MODEL_TAG = "zxx-Zsym-x-autodraw";

    @VisibleForTesting
    final StrokeManager strokeManager = new StrokeManager();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDrawShapesBinding.inflate(inflater, container, false);
        random = new RandomValueGenerator();
        tts = new TTSUtility(requireActivity());

        DrawCanvas drawingView = binding.drawingView;

        drawingView.setStrokeManager(strokeManager);

        // Configure stroke manager
        strokeManager.setContentChangedListener(drawingView);
        strokeManager.setDownloadedModelsChangedListener(this);
        strokeManager.setClearCurrentInkAfterRecognition(true);
        strokeManager.setTriggerRecognitionAfterInput(false);

        // Set and download the Shapes model automatically
        strokeManager.setActiveModel(SHAPES_MODEL_TAG);
        strokeManager.download();// Auto-download Shapes model

        strokeManager.reset(); // Clear strokes at start

        // Button Click Listeners
        binding.recognizeShapeButton.setOnClickListener(v -> strokeManager.recognize());
        binding.clearButton.setOnClickListener(v -> {
            strokeManager.reset();
            drawingView.clear();
            generateNewQuestions();
        });
//        view.findViewById(R.id.delete_button).setOnClickListener(v -> strokeManager.deleteActiveModel());
        generateNewQuestions();
        return binding.getRoot();
    }


    @Override
    public void onDownloadedModelsChanged(Set<String> downloadedLanguageTags) {
        if (downloadedLanguageTags.contains(SHAPES_MODEL_TAG)) {
            Log.i(TAG, "Shapes model downloaded successfully.");
        } else {
            Log.w(TAG, "Shapes model download failed or pending.");
        }
    }

    private void generateNewQuestions() {
        int topic = random.generateQuestionTopic();
        String question;
        switch (topic) {
            case 1:
                question = "Draw a circle.";
                break;
            case 2:
                question = "Draw a square.";
                break;
            case 3:
                question = "Draw a triangle.";
                break;
            default:
                question = "Draw anything";

        }
        binding.drawShapeTv.setText(question);
    }
}

