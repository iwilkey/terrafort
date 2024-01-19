package dev.iwilkey.terrafort.clk;

/**
 * A {@link TSchedulableEvent} that happens until the {@link TEvent} returns true.
 * @author Ian Wilkey (iwilkey)
 */
public final class TSchedulableIndefiniteEvent extends TSchedulableEvent {
	
	/**
	 * Defines an event that happens until the given {@link TEvent} returns true.
	 */
	public TSchedulableIndefiniteEvent(TEvent event, float timeout) {
		super(event, timeout);
	}

	@Override
	public boolean decideOnExit(int timesOccurred) {
		return false;
	}

}
