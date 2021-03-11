package de.dgs.apps.openspeedyfx;

import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.Mutable;
import org.aeonbits.owner.Reloadable;

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

@Sources({ "file:" + OpenSpeedyConfiguration.FILENAME})
public interface OpenSpeedyConfiguration extends Mutable, Accessible, Reloadable {
    String FILENAME = "openSpeedy.properties";

    String PROPERTY_MUSIC_VOLUME = "musicVolume";
    String PROPERTY_EFFECTS_VOLUME = "effectsVolume";
    String PROPERTY_SHOW_HINTS = "showHints";
    String PROPERTY_AUTO_SCROLL = "autoScroll";

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

    @DefaultValue("740")
    double menuMinHeight();

    @DefaultValue("true")
    boolean autoScroll();

    @DefaultValue("CUSTOM_MAPS")
    String customMapsDirectoryPath();

    @DefaultValue("data/defaultmaps")
    String defaultMapsDirectoryPath();

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