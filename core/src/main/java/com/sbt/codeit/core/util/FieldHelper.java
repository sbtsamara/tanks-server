package com.sbt.codeit.core.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by sbt-galimov-rr on 08.02.2017.
 */
public class FieldHelper {

    public static int FIELD_WIDTH;
    public static int FIELD_HEIGHT;

    public synchronized static ArrayList<ArrayList<Character>> loadField() {
        final ArrayList<ArrayList<Character>> field = new ArrayList<>();
        FileHandle mapFile = Gdx.files.internal("map.txt");
        new BufferedReader(mapFile.reader()).lines().forEach(line ->
                field.add(line.chars().mapToObj(ch -> (char) ch).collect(Collectors.toCollection(ArrayList::new))));
        FIELD_HEIGHT = field.size();
        FIELD_WIDTH = field.get(0).size();
        checkMap(field);
        return field;
    }

    private static void checkMap(ArrayList<ArrayList<Character>> field) {
        for (int i = 1; i < field.size(); i++) {
            int lineSize = field.get(i).size();
            if(lineSize != FIELD_WIDTH) {
                throw new RuntimeException(String.format("Invalid map. Line #%s", i + 1));
            }
        }
    }

    public static boolean isEmpty(ArrayList<ArrayList<Character>> field, float x, float y) {
        Character character = field.get((int) y).get((int) x);
        return character.equals(' ') || character.equals('*');
    }

    public static boolean isWall(ArrayList<ArrayList<Character>> field, float x, float y) {
        Character character = field.get((int) y).get((int) x);
        return character.equals('#');
    }

    public static boolean isBullet(ArrayList<ArrayList<Character>> field, float x, float y) {
        Character character = field.get((int) y).get((int) x);
        return character.equals('*');
    }

    public static boolean isTank(ArrayList<ArrayList<Character>> field, float x, float y) {
        return !isEmpty(field, x, y) && !isWall(field, x, y) && !isBullet(field, x, y);
    }

    public static void clearCell(ArrayList<ArrayList<Character>> field, float x, float y) {
        field.get((int)y).set((int)x, ' ');
    }

    public static void addTankToCell(ArrayList<ArrayList<Character>> field, Character id, float x, float y) {
        field.get((int)y).set((int)x, id);
    }

    public static void addBulletToCell(ArrayList<ArrayList<Character>> field, float x, float y) {
        field.get((int)y).set((int)x, '*');
    }

}
