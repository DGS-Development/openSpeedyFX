package de.dgs.apps.openspeedyfx.game.logic.model;

public class NPC extends Actor {
    private final String name;

    public NPC() {
        name = "Fuchs";
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Name: " + name + " " + super.toString();
    }
}
