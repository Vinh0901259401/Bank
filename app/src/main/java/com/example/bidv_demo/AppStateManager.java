package com.example.bidv_demo;

public class AppStateManager {
    private AppMode currentMode;
    private ModeChangeListener listener;

    public AppStateManager() {
        this.currentMode = AppMode.NOT_LOGGED_IN;
    }

    public AppMode getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(AppMode newMode) {
        this.currentMode = newMode;
        if (listener != null) {
            listener.onModeChanged(newMode);
        }
    }

    public void setModeChangeListener(ModeChangeListener listener) {
        this.listener = listener;
    }

    public interface ModeChangeListener {
        void onModeChanged(AppMode newMode);
    }
}
