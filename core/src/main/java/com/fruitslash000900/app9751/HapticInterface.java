package com.fruitslash000900.app9751;

/** Platform bridge for haptic feedback. Implemented by AndroidLauncher; no-op on desktop. */
public interface HapticInterface {
    void vibrate(int ms);
}
