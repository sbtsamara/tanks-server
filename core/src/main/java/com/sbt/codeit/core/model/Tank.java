package com.sbt.codeit.core.model;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Optional;

import static com.sbt.codeit.core.util.FieldHelper.isEmpty;

/**
 * Created by sbt-galimov-rr on 09.02.2017.
 */
public class Tank extends GameObject {

    public static final int SIZE = 3;

    private static final int MAX_SHOTS = 10;

    private ArrayList<Bullet> bullets = new ArrayList<>();
    private TankState state = TankState.STAYING;
    private volatile boolean canFire = true;
    private String name;
    private Character id;
    private int color;
    private int model;
    private int hits;

    public Tank(TankExplodeListener explodeListener, Character id, String name, int color, int model) {
        this.name = name;
        this.color = color;
        this.model = model;
        this.id = id;
        for (int i = 0; i < SIZE; i++) {
            position.add(new ArrayList<>());
            for (int j = 0; j < SIZE; j++) {
                position.get(i).add(new Vector2());
            }
        }
        for (int i = 0; i < MAX_SHOTS; i++) {
            bullets.add(new Bullet(this, explodeListener));
        }
    }

    public void moveTo(Vector2 pos) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                position.get(i).set(j, new Vector2(pos.x + j, pos.y + i));
            }
        }
    }

    public int getHits() {
        return hits;
    }

    public Character getId() {
        return id;
    }

    public void incrementHits() {
        hits++;
    }

    public String getName() {
        return name;
    }

    public TankState getState() {
        return state;
    }

    public void setState(TankState state) {
        this.state = state;
    }

    public int getColor() {
        return color;
    }

    public int getModel() {
        return model;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public void fire() {
        if (canFire && state != TankState.EXPLODED) {
            canFire = false;
            Optional<Bullet> availableBullet = bullets.stream().filter(bullet -> !bullet.isAvailable()).findFirst();
            availableBullet.ifPresent(bullet -> bullet.setUp(getX(), getY(), direction));
        }
    }

    public void enableFire() {
        canFire = true;
    }

    @Override
    protected boolean moveUpIsImpossible() {
        return state == TankState.STAYING || isOnTheEdgeUp() ||
                position.get(0).stream().anyMatch(vectors -> !isEmpty(field, vectors.x, getY() - 1));
    }

    private boolean isOnTheEdgeUp() {
        return getY() <= 0;
    }

    @Override
    protected boolean moveDownIsImpossible() {
        return state == TankState.STAYING || isOnTheEdgeDown(field) ||
                position.get(SIZE - 1).stream().anyMatch(vectors -> !isEmpty(field, vectors.x, getY() + SIZE));
    }

    private boolean isOnTheEdgeDown(ArrayList<ArrayList<Character>> map) {
        return getY() + SIZE - 1 >= map.size() - 1;
    }

    @Override
    protected boolean moveLeftIsImpossible() {
        return state == TankState.STAYING || isOnTheEdgeLeft() ||
                position.stream().anyMatch(vectors -> !isEmpty(field, getX() - 1, vectors.get(0).y));
    }

    private boolean isOnTheEdgeLeft() {
        return getX() <= 0;
    }

    @Override
    protected boolean moveRightIsImpossible() {
        return state == TankState.STAYING || isOnTheEdgeRight() ||
                position.stream().anyMatch(vectors -> !isEmpty(field, getX() + SIZE, vectors.get(SIZE - 1).y));
    }

    private boolean isOnTheEdgeRight() {
        return getX() + SIZE - 1 >= field.get(0).size() - 1;
    }

}
