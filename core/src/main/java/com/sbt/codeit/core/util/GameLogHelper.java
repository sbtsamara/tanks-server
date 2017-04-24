package com.sbt.codeit.core.util;

import com.sbt.codeit.core.model.Tank;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by sbt-selin-an on 17.04.2017.
 */
public class GameLogHelper {

    private Tank first;
    private Tank second;
    private PrintWriter writer;
    private final static String path = "winners.txt";
    private final String battlesDir = "battles/";

    public void setFirst(Tank first) {
        this.first = first;
    }

    public void setSecond(Tank second) {
        this.second = second;
    }

    public Tank getFirst() {
        return first;
    }

    public Tank getSecond() {
        return second;
    }

    public void write(String text) {
        try {
            if (first != null && second != null) {
                Path dir = Paths.get(this.battlesDir);
                if(!dir.toFile().exists()) {
                    Files.createDirectory(dir);
                }
                OutputStream outputStream = new FileOutputStream(battlesDir + getFirst().getName() + "_" + getSecond().getName() + ".txt", true);
                writer = new PrintWriter(outputStream, true);
                writer.println(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeWinner(Tank winner, Tank looser) {
        try {
            OutputStream outputStream = new FileOutputStream(path, true);
            writer = new PrintWriter(outputStream, true);
            writer.println(winner.getName() + "=" + winner.getHits());
            writer.println(looser.getName() + "=" + looser.getHits());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void writeTimeoutWinner() {
        if(first.getHits() > second.getHits()){
            write(String.format("Game stopped by timeout. Winner is '%1$s' (%2$s hits) over '%3$s' (%4$s hits)",
                    first.getName(), first.getHits(),
                    second.getName(), second.getHits()));
            writeWinner(first, second);
        }
        if(first.getHits() < second.getHits()){
            write(String.format("Game stopped by timeout. Winner is '%1$s' (%2$s hits) over '%3$s' (%4$s hits)",
                    second.getName(), second.getHits(),
                    first.getName(), first.getHits()));
            writeWinner(first, second);
        }
        if(first.getHits() == second.getHits()){
            write(String.format("Game stopped by timeout. Dead heat between '%1$s' (%2$s hits) and '%3$s' (%4$s hits)",
                    first.getName(), first.getHits(),
                    second.getName(), second.getHits()));
            writeWinner(first, second);
        }
    }

    public void writeField(ArrayList<ArrayList<Character>> field) {
        StringBuilder stringBuilder = new StringBuilder();
        field.forEach(line -> {line.forEach(stringBuilder::append); stringBuilder.append("\r\n");});
        stringBuilder.append("=============================================================================");
        write(stringBuilder.toString());
    }
}
