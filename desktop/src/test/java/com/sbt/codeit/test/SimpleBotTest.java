package com.sbt.codeit.test;

import com.sbt.codeit.bot.SimpleBot;
import com.sbt.codeit.java.TanksGameDesktop;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by Roman on 08.02.2017.
 */
public class SimpleBotTest {

    @Test
    public void connectToServerTest() {
        TanksGameDesktop.main();
        try {
            TimeUnit.SECONDS.sleep(1);
            SimpleBot.main();
        } catch (Exception ex) {
            assertEquals(null, ex);
        }
    }

    @Test
    @Ignore
    public void multiThreadTest() {
        TanksGameDesktop.main();
        try {
            TimeUnit.SECONDS.sleep(1);
            for (int i = 0; i < 100; i++) {
                new Thread(SimpleBot::main).start();
                TimeUnit.SECONDS.sleep(1);
            }
            SimpleBot.main();
        } catch (Exception ex) {
            assertEquals(null, ex);
        }
    }

}
