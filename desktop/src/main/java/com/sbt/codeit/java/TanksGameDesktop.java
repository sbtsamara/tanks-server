package com.sbt.codeit.java;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import com.sbt.codeit.core.TanksGame;

public class TanksGameDesktop {

	public static void main (String ... args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new TanksGame(), config);
	}

}
