package de.dgs.apps.openspeedyfx.scenes.mainmenu;

import de.dgs.apps.openspeedyfx.game.logic.model.Player;

import java.nio.file.Path;
import java.util.List;

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

public interface MenuSceneCallback {
    void onCooperativeStart(int playersCount, Player player, Path mapInfoPath);

    void onCompetitiveStart(List<Player> players, Path mapInfoPath);

    void onLevelEditorClicked();

    void onMainMenuDataUpdate(MainMenuSettingsData mainMenuSettingsData);

    void onException(Exception exception);

    void onQuit();
}