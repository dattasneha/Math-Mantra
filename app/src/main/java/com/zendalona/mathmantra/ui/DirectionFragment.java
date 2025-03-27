package com.zendalona.mathmantra.ui;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.zendalona.mathmantra.R;
import com.zendalona.mathmantra.databinding.DialogResultBinding;
import com.zendalona.mathmantra.databinding.FragmentDirectionBinding;
import com.zendalona.mathmantra.utils.DirectionChangeListener;
import com.zendalona.mathmantra.utils.DirectionDetectorUtility;
import com.zendalona.mathmantra.utils.RandomValueGenerator;
import com.zendalona.mathmantra.utils.TTSUtility;
import com.zendalona.mathmantra.viewModels.DirectionViewModel;

import java.util.Objects;

public class DirectionFragment extends Fragment implements DirectionChangeListener {

    private FragmentDirectionBinding binding;
    private RandomValueGenerator random;
    private TTSUtility tts;
    private DirectionDetectorUtility directionDetectorUtility;
    private DirectionViewModel  directionViewModel;
    public DirectionFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDirectionBinding.inflate(inflater, container, false);
        random = new RandomValueGenerator();
        tts = new TTSUtility(requireActivity());
        directionDetectorUtility = new DirectionDetectorUtility(requireContext(),this);
        generateNewQuiz();
        return binding.getRoot();
    }

    private void generateNewQuiz() {
        int topic = random.generateQuestionTopic();
        String question;
        switch (topic) {
            case 1:
                question = "Point your phone to the North.";
                break;
            case 2:
                question = "Which direction is East?";
                break;
            case 3:
                question = "Can you point to South?";
                break;
            default:
                question = "Point to the West direction.";
        }

        binding.showDirectionTv.setText(question);


        directionViewModel.azimuth.observe(getViewLifecycleOwner(), azimuth -> {
            if(azimuth != null) {
                directionViewModel.updateCompass(azimuth);
            }
        });

        directionViewModel.animation.observe(getViewLifecycleOwner(), animation -> {
            if (animation != null) {
                binding.compass.startAnimation(animation);
            }
        });

        if(Objects.equals(directionViewModel.currentAzimuth, directionViewModel.azimuth.getValue())) {
            showResultDialog(true);
        } else {
            showResultDialog(false);
        }
    }

    private void showResultDialog(boolean isCorrect) {
        String message = isCorrect ? "Right Answer" : "Wrong Answer";
        int gifResource = isCorrect ? R.drawable.right : R.drawable.wrong;
        tts.speak(message);
        LayoutInflater inflater = getLayoutInflater();
        DialogResultBinding dialogBinding = DialogResultBinding.inflate(inflater);
        View dialogView = dialogBinding.getRoot();

        // Load the GIF using Glide
        Glide.with(this)
                .asGif()
                .load(gifResource)
                .into(dialogBinding.gifImageView);

        dialogBinding.messageTextView.setText(message);

        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Continue", (dialog, which) -> {
                    dialog.dismiss();
                    generateNewQuiz();
                })
                .create()
                .show();
    }

    @Override
    public void onAzimuthChanged(float azimuth) {
        directionViewModel.updateAzimuth(azimuth);
    }

    @Override
    public void onPause() {
        super.onPause();
        directionDetectorUtility.unregisterListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        tts.shutdown();
    }
}