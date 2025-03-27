package com.zendalona.mathmantra.viewModels;

import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DirectionViewModel extends ViewModel {
    private final MutableLiveData<Float> _azimuth= new MutableLiveData<>();
    public LiveData<Float> azimuth = _azimuth;

    private final MutableLiveData<Animation> _animation= new MutableLiveData<>();
    public LiveData<Animation> animation = _animation;

    public Float currentAzimuth = 0f;

    public void updateAzimuth(float azimuth) {
        _azimuth.setValue(azimuth);
    }

    public void updateCompass(float  azimuth) {
        RotateAnimation rotateAnimation = new RotateAnimation(
                azimuth,
                -currentAzimuth,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );

        rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true);

        _animation.postValue(rotateAnimation);
        currentAzimuth = azimuth;
    }
}
