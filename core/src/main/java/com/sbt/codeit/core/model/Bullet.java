package com.sbt.codeit.core.model;

import com.badlogic.gdx.math.Vector2;
import com.sbt.codeit.core.util.FieldHelper;

import java.util.ArrayList;
import java.util.Collections;

import static com.sbt.codeit.core.model.Tank.SIZE;

/**
 * Created by SBT-Galimov-RR on 10.02.2017.
 */
public class Bullet extends GameObject {

    private boolean available;
    private TankExplodeListener explodeListener;
    private Tank owner;

    public Bullet(Tank owner, TankExplodeListener explodeListener) {
        this.owner = owner;
        this.explodeListener = explodeListener;
        position.add(new ArrayList<>(Collections.singletonList(new Vector2())));
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setUp(int x, int y, Direction direction) {
        setDirection(direction);
        setAvailable(true);
        int posX = x + SIZE / 2;
        int posY = y + SIZE / 2;
        switch (direction){
            case UP:
                posY = y - 1;
                break;
            case DOWN:
                posY = y + SIZE;
                break;
            case LEFT:
                posX = x - 1;
                break;
            default:
                posX = x + SIZE;
        }
        position.get(0).get(0).set(posX, posY);
    }

    @Override
    public void update(ArrayList<ArrayList<Character>> field) {
        super.update(field);
        if (isOnTheField(getX(), getY())) {
            fixStuck();
            if (!FieldHelper.isEmpty(field, getX(), getY())) {
                if(FieldHelper.isWall(field, getX(), getY())) {
                    explode();
                } else if(FieldHelper.isTank(field, getX(), getY())) {
                    FieldHelper.clearCell(field, getPreviousX(), getPreviousY());
                    setAvailable(false);
                    explodeListener.hit(owner, getX(), getY());
                }
            }
        } else {
            if(isOnTheField(getPreviousX(), getPreviousY())) {
                FieldHelper.clearCell(field, getPreviousX(), getPreviousY());
            }
            setAvailable(false);
        }
    }

    private void fixStuck() {
        if(FieldHelper.isWall(field, getPreviousX(), getPreviousY())) {
            position.get(0).get(0).set(getPreviousX(), getPreviousY());
        }
    }

    public boolean isOnTheField(int x, int y) {
        return x >= 0 && y >= 0 && y < FieldHelper.FIELD_HEIGHT && x < FieldHelper.FIELD_WIDTH;
    }

    public void explode() {
        if(!isAvailable()) {
            return;
        }
        FieldHelper.clearCell(field, getPreviousX(), getPreviousY());
        if(direction == Direction.UP || direction == Direction.DOWN) {
            for (int x = getX() - SIZE / 2; x <= getX() + SIZE / 2; x++) {
                if(FieldHelper.isWall(field, x, getY())) {
                    FieldHelper.clearCell(field, x, getY());
                }
            }
        } else {
            for (int y = getY() - SIZE / 2; y <= getY() + SIZE / 2; y++) {
                if(FieldHelper.isWall(field, getX(), y)) {
                    FieldHelper.clearCell(field, getX(), y);
                }
            }
        }
        setAvailable(false);
    }

}
