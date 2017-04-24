package com.sbt.codeit.core.model;

/**
 * Created by SBT-Galimov-RR on 09.02.2017.
 */
public enum Direction {

    UP(0),
    DOWN(180),
    LEFT(270),
    RIGHT(90);

    private int rotation;

    Direction(int rotation) {
        this.rotation = rotation;
    }

    public int toRotation(){
        return rotation;
    }

}
