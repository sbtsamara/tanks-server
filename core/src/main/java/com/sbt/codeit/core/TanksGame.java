package com.sbt.codeit.core;

import com.badlogic.gdx.Game;
import com.sbt.codeit.core.control.GameControllerImpl;
import com.sbt.codeit.core.model.World;
import com.sbt.codeit.core.server.RMIServer;
import com.sbt.codeit.core.view.Drawer;


public class TanksGame extends Game {

	private Drawer drawer;
	private World world;

	@Override
	public void create () {
		world = new World();
		world.startUpdates();
		drawer = new Drawer(world);
		GameControllerImpl controller = new GameControllerImpl(world);
		new RMIServer(controller).start();
	}

	@Override
	public void render () {
		drawer.draw();
	}

}
