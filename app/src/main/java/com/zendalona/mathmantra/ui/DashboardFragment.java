package com.zendalona.mathmantra.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zendalona.mathmantra.databinding.FragmentDashboardBinding;
import com.zendalona.mathmantra.utils.FragmentNavigation;
import com.zendalona.mathmantra.utils.SensorUtility;

import java.util.Objects;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private FragmentNavigation navigationListener;
    private SensorUtility sensorUtility ;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentNavigation) navigationListener = (FragmentNavigation) context;
        else throw new RuntimeException(context.toString() + " must implement FragmentNavigation");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        sensorUtility = new SensorUtility(requireContext());

        // Create the observer which updates the UI.
        final Observer<String> nameObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newName) {
                // Update the UI
               binding.bell.setText(newName);
            }
        };
        sensorUtility.directionLiveData.observe(getViewLifecycleOwner(), nameObserver);
        binding.ringBellCv.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.loadFragment(new RingBellFragment(),FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        });
        binding.tapTablaCv.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.loadFragment(new TapTablaFragment(),FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        });
        binding.speakNumbersCv.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.loadFragment(new CountNumbersFragment(),FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        });
        binding.ttsCv.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.loadFragment(new ChooseLessonFragment(),FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        });
        binding.mcqCv.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.loadFragment(new MCQFragment(),FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        });
        binding.mathQuizCv.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.loadFragment(new MathQuizFragment(),FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        });
        binding.numberLineCv.setOnClickListener(v -> {
            if (navigationListener != null) navigationListener.loadFragment(new NumberLineFragment(),FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        });

        return binding.getRoot();
    }
}