package dev.iwilkey.terrafort;

import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class TerrafortDesktopLauncher {
	
	public static class TerrafortDesktopApplicationConfiguration extends Lwjgl3ApplicationConfiguration {
		
		public static final float MIN_SCREEN_WIDTH_RATIO = 0.50f;
		public static final float DEFAULT_SCREEN_WIDTH_RATIO = 0.50f;
		public static final float DEFAULT_ASPECT_RATIO = (16 / 9.0f);
		
		public TerrafortDesktopApplicationConfiguration(String appName, boolean fullscreen) {
			// Set window title.
			this.setTitle(appName);
			// Set FPS target.
			this.setForegroundFPS(120);
			DisplayMode display = Lwjgl3ApplicationConfiguration.getDisplayMode();
			// Set fullscreen mode, if desired.
			if(fullscreen) 
				this.setFullscreenMode(display);
			else {
				// Set windowed mode size and minimum size.
				final int width = (int)(display.width * DEFAULT_SCREEN_WIDTH_RATIO);
				final int height =(int)(width * (1 / DEFAULT_ASPECT_RATIO));
				this.setWindowedMode(width, height);
			}
			// Set minimum dimensions in windowed mode.
			final int minHostScreenWidth = (int)(display.width * MIN_SCREEN_WIDTH_RATIO);
			final int minHostScreenHeight = (int)(minHostScreenWidth * (1 / DEFAULT_ASPECT_RATIO));
			this.setWindowSizeLimits(minHostScreenWidth, minHostScreenHeight, -1, -1);
			// Resizable by default.
			this.setResizable(true);
			// Try to set the window visible later to try and combat the focusing issue referenced on Trello.
			this.setInitialVisible(false);
			// Set up OpenGL back buffer.
			this.setBackBufferConfig(1 << 3, 
					1 << 3, 
					1 << 3, 
					1 << 3, 
					1 << 5, 
					0x00, 
					0x03);
			// Enable OpenGL debug output.
			this.enableGLDebugOutput(true, System.out);
			// Configure OpenAL.
			this.setAudioConfig(16, 512, 9);
		}
		
	}

	public static void main(String[] args) {
		TerrafortDesktopApplicationConfiguration config = new TerrafortDesktopApplicationConfiguration("Terrafort", false);
		TerrafortEngine engine = new TerrafortEngine();
		new Terrafort(engine, config);
	}
	
}
