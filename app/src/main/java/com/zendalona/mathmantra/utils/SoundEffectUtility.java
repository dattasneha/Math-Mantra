package com.zendalona.mathmantra.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class SoundEffectUtility {
    private static SoundEffectUtility instance;
    private SoundPool soundPool;
    private Context context;
    private Map<Integer, Integer> soundMap;// Map to hold sound resource IDs and their corresponding SoundPool IDs
    private int maxStreams;

    // Singleton pattern to ensure a single instance of SoundEffectUtility
    public static synchronized SoundEffectUtility getInstance(Context context,int maxStreams) {
        if (instance == null) {
            instance = new SoundEffectUtility(context.getApplicationContext(),maxStreams);
        }
        return instance;
    }

    private SoundEffectUtility(Context context, int maxSteams) {
        this.context = context;
        this.soundMap = new HashMap<>();
        this.maxStreams = maxSteams;
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(maxSteams)
                .build();
    }

    public void loadSound(int soundResId) {
        int soundId = soundPool.load(context, soundResId, 1);
        soundMap.put(soundResId, soundId); // Map the resource ID to the SoundPool ID
    }

    public void playSound(int soundResId, float leftVolume, float rightVolume, float rate) {
        Log.d("sound", "hi");
        Integer soundId = soundMap.get(soundResId);
        if (soundId != null) {
            soundPool.play(soundId, leftVolume, rightVolume, 1, 0, rate);
            Log.d("Sound played",soundId.toString());
        } else {
            this.loadSound(soundResId);
            this.playSound(soundResId,leftVolume, rightVolume, rate);
        }
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
