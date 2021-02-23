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
    @DefaultValue("3;3;4")
    int[] foxMovementCount();

    @Separator(";")
    @DefaultValue("false;true;true")
    boolean[] difficultiesIrritationImbalance();

    @Separator(";")
    @DefaultValue("0.09f;0.15f;0.25f")
    float[] difficultiesIrritationSlowdownFactor();

    @Separator(";")
    @DefaultValue("0.65f;0.70f;0.75f")
    float[] difficultiesPhysicsLinearDamping();

    @Separator(";")
    @DefaultValue("0.5f;0.5f;0.5f")
    float[] difficultiesPhysicsDensity();

    @Separator(";")
    @DefaultValue("0.90f;0.99f;1.15f")
    float[] difficultiesPhysicsFriction();

    @Separator(";")
    @DefaultValue("0.6f;0.6f;0.6f")
    float[] difficultiesPhysicsRestitution();
}