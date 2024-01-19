package dev.iwilkey.terrafort.clk;

import com.badlogic.gdx.utils.Array;

/**
 * TClock is Terrafort Engine Module responsible for tracking and calculating the time it takes
 * to process each frame (delta time) and the total processing time per frame. It also manages registered 
 * {@link TSchedulableEvent}s.
 * @author Ian Wilkey (iwilkey)
 */
public final class TClock {
	
	/**
	 * The master schedule of the Terrafort engine.
	 */
	private static final Array<TSchedulableEvent> EVENT_SCHEDULE = new Array<>();
	
	/**
	 * Events that are finished and scheduled to be removed from memory.
	 */
	private static final Array<TSchedulableEvent> EVENT_GARBAGE  = new Array<>();
	
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
	
	/**
	 * Schedule an event to fire "n" number of times with given timeout period between each shot.
	 * 
	 * <p>
	 * Note that if you choose "n" to be less than 1, the event will be considered "indefinite," and can
	 * only be stopped when the given {@link TEvent} returns true.
	 * </p>
	 * 
	 * <p>
	 * Even for definite events (where n >= 1), the given {@link TEvent} returning true will render it done,
	 * and it will be disposed next frame.
	 * </p>
	 */
	public static void schedule(TEvent event, int n, float timeout) {
		if(n > 1) 
			EVENT_SCHEDULE.add(new TSchedulableIndefiniteEvent(event, timeout));
		else 
			EVENT_SCHEDULE.add(new TSchedulableDefiniteEvent(event, n, timeout));
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
        // convert to nano to seconds...
        dt = (now - last) / 1000000000.0;
        // manage event events...
        for(final TSchedulableEvent event : EVENT_SCHEDULE) {
        	// update internal clock...
        	event.tick((float)dt);
        	// check to see if it's garbage...
        	if(event.done()) {
        		EVENT_GARBAGE.add(event);
        		continue;
        	}
        }
        // run event garbage collection, if applicable.
        if(EVENT_GARBAGE.size != 0) {
        	EVENT_SCHEDULE.removeAll(EVENT_GARBAGE, false);
        	EVENT_GARBAGE.clear();
        }
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
