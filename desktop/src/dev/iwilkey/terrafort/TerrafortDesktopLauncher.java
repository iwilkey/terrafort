package dev.iwilkey.terrafort;

import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import dev.iwilkey.terrafort.gfx.GLContext;

public class TerrafortDesktopLauncher {
	
	public static class TerrafortDesktopApplicationConfiguration extends Lwjgl3ApplicationConfiguration {
		
		public static final float MIN_SCREEN_WIDTH_RATIO = 0.50f;
		public static final float DEFAULT_SCREEN_WIDTH_RATIO = 0.50f;
		public static final float DEFAULT_ASPECT_RATIO = (16 / 9.0f);
		
		public TerrafortDesktopApplicationConfiguration(String appName, boolean fullscreen) {
			// Set window title.
			this.setTitle(appName);
			// Set FPS target.
			this.setForegroundFPS(60);
			// No VSync.
			this.useVsync(true);
			DisplayMode display = Lwjgl3ApplicationConfiguration.getDisplayMode();
			this.setWindowPosition(-1, -1);
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
			this.setInitialVisible(true);
			// Set up OpenGL back buffer.
			this.setBackBufferConfig(
							GLContext.RED_BITS, 
							GLContext.GREEN_BITS, 
							GLContext.BLUE_BITS, 
							GLContext.ALPHA_BITS, 
							GLContext.DEPTH_BITS, 
							GLContext.STENCIL_BITS, 
							GLContext.MSAA_SAMPLES
										 );
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
