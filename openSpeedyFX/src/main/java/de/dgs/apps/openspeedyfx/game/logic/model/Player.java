package de.dgs.apps.openspeedyfx.game.logic.model;

public class Player extends Actor {
    private final String name;
    private final Color color;

    public Player(String name, Color color){
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Name: " + name + " Farbe: " + color + " " + super.toString();
    }
}
