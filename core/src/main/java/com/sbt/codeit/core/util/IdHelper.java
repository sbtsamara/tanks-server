package com.sbt.codeit.core.util;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by SBT-Galimov-RR on 17.02.2017.
 */
public class IdHelper {

    private final static HashMap<String, Character> cache = new HashMap<>();
    private final static Random random = new Random();
    private final static char[] ids = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890").toCharArray();

    public static Character getId(String name) {
        Character id = cache.get(name);
        if(id == null) {
            id = getNewId();
            cache.put(name, id);
        }
        return id;
    }

    private static char getNewId() {
        Character randomId;
        do {
             randomId = ids[random.nextInt(ids.length)];
        } while(cache.containsValue(randomId));
        return randomId;
    }

}
