package com.sbt.codeit.core.model;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Created by SBT-Galimov-RR on 13.02.2017.
 */
public abstract class GameObject {

    protected ArrayList<ArrayList<Character>> field;
    protected ArrayList<ArrayList<Vector2>> position = new ArrayList<>();
    protected Vector2 previousPosition = new Vector2();
    protected Direction direction = Direction.DOWN;
    private Vector2 velocity = new Vector2();

    public int getX() {
        return (int) position.get(0).get(0).x;
    }

    public int getY() {
        return (int) position.get(0).get(0).y;
    }

    public int getPreviousX() {
        return (int) previousPosition.x;
    }

    public int getPreviousY() {
        return (int) previousPosition.y;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
        switch (direction) {
            case UP:
                velocity.set(0, -1);
                break;
            case DOWN:
                velocity.set(0, 1);
                break;
            case LEFT:
                velocity.set(-1, 0);
                break;
            default:
                velocity.set(1, 0);
        }
    }

    public void update(ArrayList<ArrayList<Character>> field) {
        this.field = field;
        previousPosition.set(getX(), getY());
        switch (direction) {
            case UP:
                if (moveUpIsImpossible()) {
                    return;
                }
                break;
            case DOWN:
                if (moveDownIsImpossible()) {
                    return;
                }
                break;
            case LEFT:
                if (moveLeftIsImpossible()) {
                    return;
                }
                break;
            default:
                if (moveRightIsImpossible()) {
                    return;
                }
        }
        position.forEach(vectors -> vectors.forEach(point -> point.add(velocity)));
    }

    protected boolean moveUpIsImpossible() {
        return false;
    }

    protected boolean moveDownIsImpossible() {
        return false;
    }

    protected boolean moveLeftIsImpossible() {
        return false;
    }

    protected boolean moveRightIsImpossible() {
        return false;
    }

}
