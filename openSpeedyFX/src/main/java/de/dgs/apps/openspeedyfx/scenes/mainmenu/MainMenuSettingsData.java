package de.dgs.apps.openspeedyfx.scenes.mainmenu;

/*
Copyright 2021 DGS-Development (https://github.com/DGS-Development)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

public class MainMenuSettingsData {
    private float musicVolume;
    private float effectsVolume;
    private boolean showHints;
    private boolean autoScroll;
    private String defaultMapPath;
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

    public String getDefaultMapPath() {
        return defaultMapPath;
    }

    public void setDefaultMapPath(String defaultMapPath) {
        this.defaultMapPath = defaultMapPath;
    }

    public String getCustomMapPath() { return customMapPath; }

    public void setCustomMapPath(String customMapPath) { this.customMapPath = customMapPath; }
}