package de.dgs.apps.openspeedyfx.scenes.mainmenu;

import de.dgs.apps.openspeedyfx.game.logic.model.Player;

import java.nio.file.Path;
import java.util.List;

public interface MenuSceneCallback {
    void onCooperativeStart(int playersCount, Player player, Path mapInfoPath, Difficulty difficulty);

    void onCompetitiveStart(List<Player> players, Path mapInfoPath, Difficulty difficulty);

    void onLevelEditorClicked();

    void onMainMenuDataUpdate(MainMenuSettingsData mainMenuSettingsData);

    void onException(Exception exception);

    void onQuit();
}