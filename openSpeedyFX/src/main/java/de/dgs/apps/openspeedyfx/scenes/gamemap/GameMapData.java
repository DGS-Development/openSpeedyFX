package de.dgs.apps.openspeedyfx.scenes.gamemap;

import de.dgs.apps.openspeedyfx.game.logic.model.Player;
import de.dgs.apps.openspeedyfx.game.resourcepacks.Resourcepack;
import de.dgs.apps.openspeedyfx.scenes.ballscene.logic.RollProperties;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

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
