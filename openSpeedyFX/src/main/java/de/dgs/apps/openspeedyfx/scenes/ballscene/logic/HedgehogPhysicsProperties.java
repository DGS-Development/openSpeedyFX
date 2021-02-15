package de.dgs.apps.openspeedyfx.scenes.ballscene.logic;

/**
 * POJO class to represent the hedgehog physics.
 */
public class HedgehogPhysicsProperties {
    private float linearDamping;
    private float density;
    private float friction;
    private float restitution;

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
