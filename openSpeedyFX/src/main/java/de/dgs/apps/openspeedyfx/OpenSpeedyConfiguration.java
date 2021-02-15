package de.dgs.apps.openspeedyfx;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.Mutable;
import org.aeonbits.owner.Reloadable;

@Sources({ "file:" + OpenSpeedyConfiguration.FILENAME})
public interface OpenSpeedyConfiguration extends Mutable, Accessible, Reloadable {
    String FILENAME = "openSpeedy.properties";

    String PROPERTY_LANGUAGE_TAGS = "languageTags";
    String PROPERTY_MUSIC_VOLUME = "musicVolume";
    String PROPERTY_EFFECTS_VOLUME = "effectsVolume";
    String PROPERTY_SHOW_HINTS = "showHints";
    String PROPERTY_AUTO_SCROLL = "autoScroll";
    String PROPERTY_CUSTOM_MAPS_DIRECTORY_PATH = "customMapsDirectoryPath";
    String PROPERTY_FOX_MOVEMENT_COUNT = "foxMovementCount";

    @Separator(";")
    @DefaultValue("DE;EN")
    String[] languageTags();

    @DefaultValue("0.2")
    float musicVolume();

    @DefaultValue("0.5")
    float effectsVolume();

    @DefaultValue("true")
    boolean showHints();

    @DefaultValue("false")
    boolean useCustomSeed();

    @DefaultValue("0")
    long customSeed();

    @DefaultValue("955")
    double menuMinWidth();

    @DefaultValue("615")
    double menuMinHeight();

    @DefaultValue("true")
    boolean autoScroll();

    @DefaultValue("CUSTOM_MAPS")
    String customMapsDirectoryPath();

    @DefaultValue("4")
    int foxMovementCount();
}