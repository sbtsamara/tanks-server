package com.sbt.codeit.core.model;

import com.badlogic.gdx.math.Vector2;
import com.sbt.codeit.core.control.ServerListener;
import com.sbt.codeit.core.util.FieldHelper;
import com.sbt.codeit.core.util.GameLogHelper;
import com.sbt.codeit.core.util.IdHelper;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.sbt.codeit.core.util.FieldHelper.FIELD_HEIGHT;
import static com.sbt.codeit.core.util.FieldHelper.FIELD_WIDTH;

/**
 * Created by sbt-galimov-rr on 09.02.2017.
 */
public class World implements TankExplodeListener {

    private final static int HEARTBEAT_DELAY = 30;
    private final static int TIMEOUT = 1000 * 60 * 1;
    private final ConcurrentHashMap<ServerListener, Tank> tanks = new ConcurrentHashMap<>();
    private final ArrayList<ArrayList<Character>> field = FieldHelper.loadField();
    private final Random random = new Random();
    private Tank winner;
    private GameLogHelper logHelper = new GameLogHelper();

    private int currentColor;
    private int heartBeats = 0;

    public World() {
        currentColor = random.nextInt(3);
    }

    public ArrayList<ArrayList<Character>> getField() {
        return field;
    }

    public void startUpdates() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (heartBeats % 2 == 0) {
                    updateTanks();
                }
                updateBullets();
                heartBeats++;
            }
        }, 0, HEARTBEAT_DELAY);
    }

    public Character addTank(ServerListener listener, String name) {
        synchronized (tanks) {
            Tank tank = createRandomTank(name);
            tanks.put(listener, tank);

            if (tanks.keySet().size() > 1) {
                Tank[] tanksArray = tanks.values().toArray(new Tank[2]);
                logHelper.setFirst(tanksArray[0]);
                logHelper.setSecond(tanksArray[1]);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (getWinner() == null) {
                            logHelper.writeTimeoutWinner();
                            System.exit(-1);
                        }
                    }
                }, TIMEOUT);
                logHelper.write(String.format("Two tanks were initiated: '%1$s' (%2$s) and '%3$s' (%4$s)",
                        logHelper.getFirst().getName(), logHelper.getFirst().getId(), logHelper.getSecond().getName(), logHelper.getSecond().getId()));
            }
            return tank.getId();
        }
    }

    private Tank createRandomTank(String name) {
        Tank tank = new Tank(this, IdHelper.getId(name), name, currentColor, random.nextInt(3));
        tank.moveTo(getPosition());
        currentColor = currentColor < 2 ? currentColor + 1 : 0;
        return tank;
    }

    public Tank getTank(ServerListener listener) {
        return tanks.get(listener);
    }

    public Collection<Tank> getTanks() {
        return tanks.values();
    }

    private synchronized void updateTanks() {
        for (Tank tank : tanks.values()) {
            if (tank.getState() == TankState.EXPLODED) {
                continue;
            }
            tank.update(field);
            if (heartBeats % 20 == 0) {
                tank.enableFire();
                heartBeats = 0;
            }
            for (int i = 0; i < Tank.SIZE; i++) {
                for (int j = 0; j < Tank.SIZE; j++) {
                    FieldHelper.clearCell(field, tank.getPreviousX() + j, tank.getPreviousY() + i);
                }
            }
            for (int i = 0; i < Tank.SIZE; i++) {
                for (int j = 0; j < Tank.SIZE; j++) {
                    FieldHelper.addTankToCell(field, tank.getId(), tank.getX() + j, tank.getY() + i);
                }
            }
        }
        logHelper.writeField(field);
        notifyListeners();
    }

    private synchronized void updateBullets() {
        for (Tank tank : tanks.values()) {
            tank.getBullets().stream().filter(Bullet::isAvailable).forEach(bullet -> {
                bullet.update(field);
                if (bullet.isAvailable()) {
                    FieldHelper.clearCell(field, bullet.getPreviousX(), bullet.getPreviousY());
                    FieldHelper.addBulletToCell(field, bullet.getX(), bullet.getY());
                }
            });
        }
    }

    private void notifyListeners() {
        for (ServerListener listener : tanks.keySet()) {
            try {
                listener.update(field);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void hit(Tank owner, int x, int y) {
        Tank tank = getTankById(field.get(y).get(x));
        for (int i = 0; i < Tank.SIZE; i++) {
            for (int j = 0; j < Tank.SIZE; j++) {
                FieldHelper.clearCell(field, tank.getPreviousX() + j, tank.getPreviousY() + i);
                FieldHelper.clearCell(field, tank.getX() + j, tank.getY() + i);
            }
        }
        tank.moveTo(getPosition());
        owner.incrementHits();
        logHelper.write("Tank " + owner.getName() + " hit tank " + tank.getName());

        if (owner.getHits() >= 3) {
            winner = owner;
            logHelper.write("Tank " + owner.getName() + " wins.");
            logHelper.writeWinner(winner, tank);
            System.out.println("Game stopped with winner");
            System.exit(-1);
        }
    }

    private Tank getTankById(Character character) {
        for (Tank tank : getTanks()) {
            if (tank.getId().equals(character)) {
                return tank;
            }
        }
        throw new IllegalArgumentException();
    }

    private Vector2 getPosition() {
        if (getTanks().size() == 0) {
            return new Vector2(0, 0);
        }
        return new Vector2(FIELD_WIDTH - Tank.SIZE, FIELD_HEIGHT - Tank.SIZE);
    }

    public Tank getWinner() {
        return winner;
    }
}
