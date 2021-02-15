package de.dgs.apps.openspeedyfx.game.resourcepacks;

import java.io.InputStream;

public class DefaultResourcepack implements Resourcepack {
    private static final String BASE_PATH = "/assets/resourcepack/default";

    private static final class InstanceContainer {
        public static final DefaultResourcepack INSTANCE = new DefaultResourcepack();
    }

    public static DefaultResourcepack getInstance () {
        return InstanceContainer.INSTANCE;
    }

    private DefaultResourcepack() {
        //Block...
    }

    @Override
    public boolean hasResource(String resourcePath) {
        return getClass().getResource(BASE_PATH + resourcePath) != null;
    }

    @Override
    public InputStream getResourceAsStream(String resourcePath) {
        return getClass().getResourceAsStream(BASE_PATH + resourcePath);
    }
}
