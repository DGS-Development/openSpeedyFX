package de.dgs.apps.osfxe.physics;

import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;

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
 * Simple POJO container for physics properties.
 */
public class PhysicsProperties {
    private final BodyDef bodyDef;
    private final FixtureDef fixtureDef;

    /**
     * Creates a new container with custom physics properties.
     * @param bodyDef The {@link BodyDef}.
     * @param fixtureDef The {@link FixtureDef}.
     */
    public PhysicsProperties(BodyDef bodyDef, FixtureDef fixtureDef) {
        this.bodyDef = bodyDef;
        this.fixtureDef = fixtureDef;
    }

    /**
     * Creates a new container with a custom body definition and a default fixture definition.
     * @param bodyDef The {@link BodyDef}.
     */
    public PhysicsProperties(BodyDef bodyDef) {
        this(bodyDef, new FixtureDef());
    }

    /**
     * Creates a new container with a custom fixture definition and a default body definition.
     * @param fixtureDef The {@link FixtureDef}.
     */
    public PhysicsProperties(FixtureDef fixtureDef) {
        this(new BodyDef(), fixtureDef);
    }

    public BodyDef getBodyDef() {
        return bodyDef;
    }

    public FixtureDef getFixtureDef() {
        return fixtureDef;
    }
}
