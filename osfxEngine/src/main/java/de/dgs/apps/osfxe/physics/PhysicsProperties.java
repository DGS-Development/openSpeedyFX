package de.dgs.apps.osfxe.physics;

import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;

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
