package dev.iwilkey.terrafort.clk;

/**
 * A {@link TSchedulableEvent} that happens "n" number of times, then disposes itself.
 * @author Ian Wilkey (iwilkey)
 */
public final class TSchedulableDefiniteEvent extends TSchedulableEvent {
	
	private final int n;
	
	/**
	 * Defines a multishot event to happen "n" number of times with given timeout between shots.
	 */
	public TSchedulableDefiniteEvent(TEvent event, int n, float timeout) {
		super(event, timeout);
		this.n = n;
	}

	@Override
	public boolean decideOnExit(int timesOccurred) {
		return timesOccurred >= n;
	}

}
