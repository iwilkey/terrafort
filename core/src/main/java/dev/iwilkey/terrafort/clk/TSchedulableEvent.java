package dev.iwilkey.terrafort.clk;

/**
 * A schedulable event managed by the {@link TClock} module. 
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TSchedulableEvent {

	private final TEvent event;
	private final float  timeout;
	
	private boolean isDone = false;
	private float   tslt   = 0.0f;
	private int     tocc   = 0;
	
	/**
	 * Creates a new schduleable event. Must supply the event. Event cannot be reassigned.
	 */
	public TSchedulableEvent(TEvent event, float timeout) {
		this.event   = event;
		this.timeout = timeout;
	}
	
	/**
	 * If this function returns true, this scheduable event has done it's job and will be added to garbage.
	 */
	public abstract boolean decideOnExit(int timesOccurred);

	/**
	 * Called by the clock module to update the internal timeout clock.
	 */
	public final void tick(float dt) {
		if(isDone)
			return;
		tslt += dt;
		if(tslt >= timeout) {
			isDone = decideOnExit(tocc);
			if(isDone)
				return;
			fire();
			tslt = 0.0f;
		}
	}
	
	/**
	 * Fires the event and increases the times occurred variable.
	 */
	public final void fire() {
		isDone = event.fire();
		tocc++;
	}
	
	/**
	 * Whether or not this event has completed it's job.
	 */
	public final boolean done() {
		return isDone;
	}
	
}
