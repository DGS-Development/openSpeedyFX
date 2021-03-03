package de.dgs.apps.openspeedyfx.scenes.ballscene.logic;

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
 * POJO class to represent the hedgehog physics.
 */
public class HedgehogPhysicsProperties {
    private final float linearDamping;
    private final float density;
    private final float friction;
    private final float restitution;

    /**
     * Creates new {@link HedgehogPhysicsProperties}.
     * @param linearDamping The damping amount after pushing the hedgehog into a certain direction.
     * @param density The density of the hedgehog representation.
     * @param friction The friction effect after pushing the hedgehog into a certain direction.
     * @param restitution The restitution amount when the hedgehog collides with a solid object.
     */
    public HedgehogPhysicsProperties(float linearDamping, float density, float friction, float restitution) {
        this.linearDamping = linearDamping;
        this.density = density;
        this.friction = friction;
        this.restitution = restitution;
    }

    public float getLinearDamping() {
        return linearDamping;
    }

    public float getDensity() {
        return density;
    }

    public float getFriction() {
        return friction;
    }

    public float getRestitution() {
        return restitution;
    }
}
