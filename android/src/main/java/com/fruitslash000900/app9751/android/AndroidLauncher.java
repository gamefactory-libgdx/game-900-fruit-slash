package com.fruitslash000900.app9751.android;

import android.os.Bundle;
import android.os.Vibrator;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.fruitslash000900.app9751.HapticInterface;
import com.fruitslash000900.app9751.MainGame;

public class AndroidLauncher extends AndroidApplication implements HapticInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true;

        MainGame game = new MainGame();
        game.haptic = this;
        initialize(game, configuration);
    }

    @Override
    public void vibrate(int ms) {
        try {
            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (v != null) v.vibrate(ms);
        } catch (Exception ignored) {}
    }
}
