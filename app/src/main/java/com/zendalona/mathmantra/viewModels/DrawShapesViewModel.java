package com.zendalona.mathmantra.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DrawShapesViewModel extends ViewModel {
    private MutableLiveData<String> _resultText = new MutableLiveData<>("");
    public LiveData<String> resultText = _resultText;

    public void updateResult(String text) {
        _resultText.setValue(text);
    }
}
