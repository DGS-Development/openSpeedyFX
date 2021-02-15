package de.dgs.apps.osfxe.scenes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;

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
