package de.dgs.apps.osfxe.scenes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

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

/**
 * Helper class to create and manage {@link Scene} objects for a given {@link Stage}.
 */
public class SceneManager {
	private final Stage mainStage;

	private final Map<String, Scene> nameSceneMap = new HashMap<>();

	/**
	 * Creates a new {@link SceneManager}, responsible for a {@link Stage}.
	 * @param mainStage The {@link Stage} to load scenes into.
	 */
	public SceneManager(Stage mainStage) {
		this.mainStage = mainStage;
	}

	/**
	 * Loads and returns a {@link Scene}. Reloads the {@link Scene} if it was already loaded.
	 * @param fxmlUrl The resource URL to load the FXML from.
	 * @return The loaded {@link Scene}.
	 * @throws IOException Thrown if the FXML-resource wasn't found.
	 */
	public Scene loadScene(String fxmlUrl) throws IOException {
		return loadScene(fxmlUrl, false);
	}

	/**
	 * Loads and returns a {@link Scene}. Reloads the {@link Scene} if it was already loaded.
	 * @param fxmlUrl The resource URL to load the FXML from.
	 * @param switchScene True if the loaded scene should be displayed in the {@link Stage}.
	 * @return The loaded {@link Scene}.
	 * @throws IOException Thrown if the FXML-resource wasn't found.
	 */
	public Scene loadScene(String fxmlUrl, boolean switchScene) throws IOException {
		return loadScene(fxmlUrl, switchScene, null);
	}

	/**
	 * Loads and returns a {@link Scene}. Reloads the {@link Scene} if it was already loaded.
	 * @param fxmlUrl The resource URL to load the FXML from.
	 * @param switchScene True if the loaded scene should be displayed in the {@link Stage}.
	 * @param controller The controller of the {@link Scene}, or null.
	 * @return The loaded {@link Scene}.
	 * @throws IOException Thrown if the FXML-resource wasn't found.
	 */
	public Scene loadScene(String fxmlUrl, boolean switchScene, Object controller) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlUrl));

		if(controller != null)
			loader.setController(controller);

		Parent rootNode = loader.load();
		Scene scene = new Scene(rootNode);

		nameSceneMap.put(fxmlUrl, scene);

		if(switchScene)
			setActiveScene(scene);

		return scene;
	}

	/**
	 * Loads a {@link Scene} and returns the corresponding {@link GameController}. Reloads the {@link Scene} if it was already loaded.
	 * @param controllerClass The class of the {@link GameController} containing the corresponding FXML-resource.
	 * @param switchScene True if the loaded scene should be displayed in the {@link Stage}.
	 * @param <T> The {@link GameController}.
	 * @return The created {@link GameController}.
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 */
	public <T extends GameController> T loadScene(Class<? extends T> controllerClass, boolean switchScene) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		T controller = GameControllerLoader.loadController(controllerClass);

		Scene scene = SceneCreator.createScene(controller);
		nameSceneMap.put(controller.getFxmlPath(), scene);

		if(switchScene)
			setActiveScene(scene);

		return controller;
	}

	/**
	 * Loads a {@link Scene} and returns the corresponding {@link GameController}. Reloads the {@link Scene} if it was already loaded.
	 * @param controllerClass The class of the {@link GameController} containing the corresponding FXML-resource.
	 * @param <T> The {@link GameController}.
	 * @return The created {@link GameController}.
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 */
	public <T extends GameController> T loadScene(Class<? extends T> controllerClass) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		return loadScene(controllerClass, false);
	}

	/**
	 * Adds an existing {@link Scene} with the corresponding FXML-resource path. Replaces the old {@link Scene} if it already exists.
	 * @param fxmlPath The FXML-resource path.
	 * @param sceneToAdd The {@link Scene} to add.
	 * @return The previous {@link Scene}, if any existed.
	 */
	public Scene addScene(String fxmlPath, Scene sceneToAdd) {
		return nameSceneMap.put(fxmlPath, sceneToAdd);
	}

	/**
	 * Changes the displayed {@link Scene} in the {@link Stage}.
	 * @param controller The {@link GameController} corresponding to the {@link Scene}.
	 * @return True if the {@link Scene} was found and switched.
	 */
	public boolean switchScene(GameController controller) {
		if(controller == null || !nameSceneMap.containsKey(controller.getFxmlPath()))
			return false;

		Scene newScene = nameSceneMap.get(controller.getFxmlPath());

		if(newScene == getActiveScene())
			return false;

		setActiveScene(newScene);
		return true;
	}

	/**
	 * Changes the displayed {@link Scene} in the {@link Stage}.
	 * @param fxmlUrl The FXML-resource corresponding to the {@link Scene}.
	 * @return True if the {@link Scene} was found and switched.
	 */
	public boolean switchScene(String fxmlUrl) {
		if(nameSceneMap.containsKey(fxmlUrl))
			return false;

		setActiveScene(nameSceneMap.get(fxmlUrl));
		return true;
	}

	/**
	 * Returns true if a {@link Scene} exists.
	 * @param controller The {@link GameController} corresponding to the {@link Scene}.
	 * @return True if the {@link Scene} exists.
	 */
	public boolean hasScene(GameController controller) {
		if(controller == null)
			return false;

		return hasScene(controller.getFxmlPath());
	}

	/**
	 * Returns true if a {@link Scene} exists.
	 * @param fxmlUrl The FXML-resource corresponding to the {@link Scene}.
	 * @return True if the {@link Scene} exists.
	 */
	public boolean hasScene(String fxmlUrl) {
		return nameSceneMap.containsKey(fxmlUrl);
	}

	private void setActiveScene(Scene scene) {
		mainStage.setScene(scene);
	}

	private Scene getActiveScene() {
		return mainStage.getScene();
	}
}
