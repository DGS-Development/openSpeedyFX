package de.dgs.apps.openspeedyfx;

import de.dgs.apps.openspeedyfx.scenes.ballscene.logic.HedgehogIrritation;
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

    @Separator(";")
    @DefaultValue("4;6;8")
    int[] foxMovementCount();

    @Separator(";")
    @DefaultValue("1;1;0")
    int[] difficultiesIrritationImbalance();

    @Separator(";")
    @DefaultValue("0.05f;0.09f;0.13f")
    float[] difficultiesIrritationSlowdownFactor();

    @Separator(";")
    @DefaultValue("0.60f;0.65f;0.70f")
    float[] difficultiesPhysicsLinearDamping();

    @Separator(";")
    @DefaultValue("0.2f;0.5f;0.8f")
    float[] difficultiesPhysicsDestiny();

    @Separator(";")
    @DefaultValue("0.94f;0.99f;1.04f")
    float[] difficultiesPhysicsFriction();

    @Separator(";")
    @DefaultValue("0.3f;0.6f;0.9f")
    float[] difficultiesPhysicsRestitution();
}