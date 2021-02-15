package de.dgs.apps.openspeedyfx.scenes.mainmenu;

public class MainMenuSettingsData {
    private float musicVolume;
    private float effectsVolume;
    private boolean showHints;
    private boolean autoScroll;
    private int foxMovementCount;
    private String customMapPath;

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = musicVolume;
    }

    public float getEffectsVolume() {
        return effectsVolume;
    }

    public void setEffectsVolume(float effectsVolume) {
        this.effectsVolume = effectsVolume;
    }

    public void setShowHints(boolean showHints) {
        this.showHints = showHints;
    }

    public boolean getShowHints() {
        return showHints;
    }

    public boolean isAutoScroll() {
        return autoScroll;
    }

    public void setAutoScroll(boolean autoScroll) {
        this.autoScroll = autoScroll;
    }

    public void setFoxMovementCount(int foxMovementCount) { this.foxMovementCount = foxMovementCount; }

    public int getFoxMovementCount() { return foxMovementCount; }

    public void setCustomMapPath(String customMapPath) { this.customMapPath = customMapPath; }

    public String getCustomMapPath() { return customMapPath; }
}