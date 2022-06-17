package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Starts the entire program
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("CircleBlaster");
		config.setWindowedMode(1200, 800);
		config.useVsync(true);
		config.setForegroundFPS(20);
		// actually initializes the window and goes to CircleBlaster
		new Lwjgl3Application(new CircleBlaster(), config);
	}
}
