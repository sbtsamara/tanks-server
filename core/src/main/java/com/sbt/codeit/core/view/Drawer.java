package com.sbt.codeit.core.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.sbt.codeit.core.model.Bullet;
import com.sbt.codeit.core.model.Tank;
import com.sbt.codeit.core.model.TankState;
import com.sbt.codeit.core.model.World;
import com.sbt.codeit.core.util.FieldHelper;

import java.util.ArrayList;

/**
 * Created by sbt-galimov-rr on 08.02.2017.
 */
public class Drawer {

    private World world;
    private Texture wall;
    private Texture bulletTexture;
    private Texture gray;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont font = new BitmapFont(true);
    private float cellSize;
    private final TextureRegion[][] tanks;
    private final float rightEdgeOfField;

    public Drawer(World world) {
        this.world = world;
        camera = new OrthographicCamera();
        camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0, 0, 0, 0);
        wall = new Texture(Gdx.files.internal("brick.jpg"));
        bulletTexture = new Texture(Gdx.files.internal("bullet.png"));
        gray = new Texture(Gdx.files.internal("gray.png"));
        tanks = TextureRegion.split(new Texture(Gdx.files.internal("tanks.png")), 60, 80);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        cellSize = Gdx.graphics.getHeight() / (float) FieldHelper.FIELD_HEIGHT;
        rightEdgeOfField = FieldHelper.FIELD_WIDTH * cellSize;

    }

    public void draw() {
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        batch.begin();
        drawMap();
        drawTanks();
        drawInfoPanel();
        batch.end();
    }

    private void drawInfoPanel() {
        batch.draw(gray, rightEdgeOfField, 0, Gdx.graphics.getWidth() - rightEdgeOfField, Gdx.graphics.getHeight());
        int i = 0;
        for (Tank tank : world.getTanks()) {
            batch.draw(tanks[tank.getColor()][tank.getModel()], rightEdgeOfField + 5, 25 * i + 5, 20, 20);
            font.draw(batch, tank.getName(), rightEdgeOfField + 30, 25 * i + 10);
            font.draw(batch, String.valueOf(tank.getHits()), Gdx.graphics.getWidth() - 20, 25 * i + 10);
            i++;
        }
    }

    private void drawMap() {
        ArrayList<ArrayList<Character>> map = world.getField();
        for (int y = 0; y < map.size(); y++) {
            for (int x = 0; x < map.get(y).size(); x++) {
                if (map.get(y).get(x).equals('#')) {
                    batch.draw(wall, x * cellSize, y * cellSize, cellSize, cellSize, 0, 0,
                            wall.getWidth() / Tank.SIZE, wall.getHeight() / Tank.SIZE, false, false);
                }
            }
        }
    }

    private void drawTanks() {
        for (Tank tank : world.getTanks()) {
            if(tank.getState() == TankState.EXPLODED) {
                continue;
            }
            batch.draw(tanks[tank.getColor()][tank.getModel()], tank.getX() * cellSize, tank.getY() * cellSize, cellSize * Tank.SIZE / 2, cellSize * Tank.SIZE / 2,
                    cellSize * Tank.SIZE, cellSize * Tank.SIZE, 1, 1, tank.getDirection().toRotation());
            drawBullets(tank);
        }
    }

    private void drawBullets(Tank tank) {
        tank.getBullets().stream().filter(Bullet::isAvailable).forEach(bullet ->
                batch.draw(bulletTexture, bullet.getX() * cellSize, bullet.getY() * cellSize, cellSize / 2, cellSize / 2, cellSize, cellSize, 1, 1,
                        bullet.getDirection().toRotation(), 0, 0, bulletTexture.getWidth(), bulletTexture.getHeight(), false, false)
        );
    }

}
