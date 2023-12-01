package dev.iwilkey.terrafort;

/**
 * TClock is a utility class responsible for tracking and calculating the time it takes
 * to process each frame (delta time) and the total processing time per frame.
 * It's designed to help synchronize game logic, animations, and other time-sensitive
 * operations with the actual time elapsed, making the behavior consistent regardless of the
 * machine's performance or frame rate variability.
 * 
 * <p>The class provides a global access point to time metrics relevant for game loops, such as
 * delta time and process time, using a high-resolution time source (nanoseconds).</p>
 *
 * <p>This class is not thread-safe and is designed to be used from a single thread.</p>
 * @author Ian Wilkey (iwilkey)
 */
public final class TClock {
	
	private static double dt;
	private static double pt;
	
	/**
     * Initializes a new instance of the TClock class.
     * The constructor is primarily responsible for initializing time variables.
     */
	public TClock() {
		dt = 0.0;
		pt = 0.0;
	}

	///////////////////////////////////////////////////////
	// BEGIN API
	///////////////////////////////////////////////////////

	/**
     * Retrieves the delta time for the last frame in seconds.
     * Delta time is the time it takes for the current frame to process (time between ticks).
     * This method is useful for real-time operations to ensure time-sensitive calculations
     * stay accurate despite the engine's frame rate variability.
     *
     * @return The delta time in seconds.
     */
	public static double dt() {
		return dt;
	}

	/**
     * Retrieves the total processing time for the last frame in seconds.
     * Process time is the amount of time it takes to process one engine frame (time between tick and tock).
     * This method is primarily useful for metrics and performance monitoring.
     *
     * @return The process time in seconds.
     */
	public static double pt() {
		return pt;
	}

	///////////////////////////////////////////////////////
	// END API
	///////////////////////////////////////////////////////
	
    long now = System.nanoTime();
    long last = now;
    long delta = 0L;

    /**
     * Updates the delta time and records the current time.
     * This method should be called at the beginning of each frame (tick).
     */
    public void tick() {
        now = System.nanoTime();
        dt = (now - last) / 1000000000.0; // convert to seconds
        last = now;
    }

    /**
     * Updates the process time.
     * This method should be called at the end of each frame (tock),
     * after all processing for the frame is completed.
     */
    public void tock() {
        pt = (System.nanoTime() - now) / 1000000000.0; // convert to seconds
    }
	
}
