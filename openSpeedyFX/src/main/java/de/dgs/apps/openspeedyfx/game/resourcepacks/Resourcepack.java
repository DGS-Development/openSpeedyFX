package de.dgs.apps.openspeedyfx.game.resourcepacks;

import java.io.InputStream;

public interface Resourcepack {
    boolean hasResource(String resourcePath);
    InputStream getResourceAsStream(String resourcePath);
}