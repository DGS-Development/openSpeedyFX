package de.dgs.apps.osfxe.scenes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

/**
 * Helper class to load a {@link GameController}.
 */
public class GameControllerLoader {
    /**
     * Creates a new {@link GameController}, based on a controller-class.
     * @param controllerClass The class of the {@link GameController} to create.
     * @param <T> The {@link GameController} type.
     * @return The created {@link GameController}.
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IOException
     */
    public static <T extends GameController> T loadController(Class<? extends T> controllerClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {
        return loadController(controllerClass, null);
    }

    /**
     * Creates a new {@link GameController}, based on a controller-class.
     * @param controllerClass The class of the {@link GameController} to create.
     * @param <T> The {@link GameController} type.
     * @param resourceBundle The {@link ResourceBundle} to load localization strings from.
     * @return The created {@link GameController}.
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IOException
     */
    public static <T extends GameController> T loadController(Class<? extends T> controllerClass, ResourceBundle resourceBundle) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {
        T controller = controllerClass.getDeclaredConstructor().newInstance();

        FXMLLoader loader = new FXMLLoader(controllerClass.getResource(controller.getFxmlPath()), resourceBundle);
        loader.setController(controller);

        Parent rootNode = loader.load();
        controller.setRootNode(rootNode);

        return controller;
    }
}
