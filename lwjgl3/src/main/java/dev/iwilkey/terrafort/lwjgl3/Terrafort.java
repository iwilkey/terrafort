package dev.iwilkey.terrafort.lwjgl3;

import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import dev.iwilkey.terrafort.TEngine;

/** Launches the desktop (LWJGL3) application. */
public class Terrafort {
	
	public static final float MIN_SCREEN_WIDTH_RATIO     = 0.50f;
	public static final float DEFAULT_SCREEN_WIDTH_RATIO = 0.50f;
	public static final float DEFAULT_ASPECT_RATIO       = (16 / 9.0f);
	
    public static void main(String[] args) {
        if (Startup.startNewJvmIfRequired()) return;
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new TEngine(), getDefaultConfiguration(false));
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration(final boolean fullscreen) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Terrafort");
        configuration.setForegroundFPS(60);
        configuration.useVsync(true);
		final DisplayMode display = Lwjgl3ApplicationConfiguration.getDisplayMode();
		configuration.setWindowPosition(-1, -1);
		if (fullscreen)
			configuration.setFullscreenMode(display);
		else {
			final int width = (int) (display.width * DEFAULT_SCREEN_WIDTH_RATIO);
			final int height = (int) (width * (1 / DEFAULT_ASPECT_RATIO));
			configuration.setWindowedMode(width, height);
		}
		final int minHostScreenWidth = (int) (display.width * MIN_SCREEN_WIDTH_RATIO);
		final int minHostScreenHeight = (int) (minHostScreenWidth * (1 / DEFAULT_ASPECT_RATIO));
		configuration.setWindowSizeLimits(minHostScreenWidth, minHostScreenHeight, -1, -1);
		configuration.setResizable(true);
		configuration.setInitialVisible(true);
		configuration.setBackBufferConfig(0x8, 0x8, 0x8, 0x8, 0, 0, 4);
		configuration.setWindowIcon("icon-raw.png", "icon-raw.png", "icon-raw.png", "icon-raw.png");
		configuration.enableGLDebugOutput(true, System.out);
		configuration.setAudioConfig(16, 512, 9);
		return configuration;
    }
    
}