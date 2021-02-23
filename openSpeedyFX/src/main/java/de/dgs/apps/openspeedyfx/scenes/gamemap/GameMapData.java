package de.dgs.apps.openspeedyfx.scenes.gamemap;

import de.dgs.apps.openspeedyfx.game.logic.model.Player;
import de.dgs.apps.openspeedyfx.game.resourcepacks.Resourcepack;
import de.dgs.apps.openspeedyfx.scenes.ballscene.logic.RollProperties;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

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

public class GameMapData {
    private int foxMovementCount;
    private GameMapCallback gameMapCallback;
    private Random random;
    private boolean isShowHints;
    private boolean isAutoScroll;
    private float soundsVolume;
    private float musicVolume;
    private int physicalPlayersCount;
    private ResourceBundle resourceBundle;
    private Resourcepack resourcepack;
    private RollProperties rollProperties;
    private List<Player> players;
    private Path mapInfoPath;

    public int getFoxMovementCount() {
        return foxMovementCount;
    }

    public void setFoxMovementCount(int foxMovementCount) {
        this.foxMovementCount = foxMovementCount;
    }

    public GameMapCallback getGameMapCallback() {
        return gameMapCallback;
    }

    public void setGameMapCallback(GameMapCallback gameMapCallback) {
        this.gameMapCallback = gameMapCallback;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public boolean isShowHints() {
        return isShowHints;
    }

    public void setShowHints(boolean showHints) {
        isShowHints = showHints;
    }

    public boolean isAutoScroll() {
        return isAutoScroll;
    }

    public void setAutoScroll(boolean autoScale) {
        isAutoScroll = autoScale;
    }

    public float getSoundsVolume() {
        return soundsVolume;
    }

    public void setSoundsVolume(float soundsVolume) {
        this.soundsVolume = soundsVolume;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = musicVolume;
    }

    public int getPhysicalPlayersCount() {
        return physicalPlayersCount;
    }

    public void setPhysicalPlayersCount(int physicalPlayersCount) {
        this.physicalPlayersCount = physicalPlayersCount;
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    public Resourcepack getResourcepack() {
        return resourcepack;
    }

    public void setResourcepack(Resourcepack resourcepack) {
        this.resourcepack = resourcepack;
    }

    public RollProperties getRollProperties() {
        return rollProperties;
    }

    public void setRollProperties(RollProperties rollProperties) {
        this.rollProperties = rollProperties;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Path getMapInfoPath() {
        return mapInfoPath;
    }

    public void setMapInfoPath(Path mapInfoPath) {
        this.mapInfoPath = mapInfoPath;
    }
}
