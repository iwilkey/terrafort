package dev.iwilkey.terrafort.gfx.anim;

import dev.iwilkey.terrafort.gfx.TFrame;

/**
 * A special grouping of {@link TAnimation} that encapsulate and label all movement animation frames
 * for a {@link TLifeform}, following specific rules in the sprite sheet.
 * 
 * <p>
 * The rules are as follows:<br>
 * - The amount of frames for each state should be of uniform length 3.<br>
 * - The first frame should be the idle for that direction, the second the left stride and the third, the right stride.<br>
 * - There should be 8 groups of frames, stacked vertically.<br>
 * - Each group of frames should match up with the following order, given top to bottom:<br><br>
 * 
 * - MOVE SOUTH<br>
 * - MOVE SOUTH-EAST<br>
 * - MOVE EAST<br>
 * - MOVE NORTH-EAST<br>
 * - MOVE NORTH<br>
 * - MOVE NORTH-WEST<br>
 * - MOVE WEST<br>
 * - MOVE SOUTH-WEST<br>
 * </p>
 * 
 * See the official Sprite Sheet for an example.
 * 
 * @author Ian Wilkey (iwilkey)
 */
public final class TMovementAnimationArray {
	
	public static final byte SOUTH      = 0;
	public static final byte SOUTH_EAST = 1;
	public static final byte EAST       = 2;
	public static final byte NORTH_EAST = 3;
	public static final byte NORTH      = 4;
	public static final byte NORTH_WEST = 5;
	public static final byte WEST       = 6;
	public static final byte SOUTH_WEST = 7;
	
	public static final String LABELS[] = new String[8];
	public static final TFrame ATTACK[] = new TFrame[8];
	
	static {
		LABELS[SOUTH]      = "move_south";
		LABELS[SOUTH_EAST] = "move_south_east";
		LABELS[EAST]       = "move_east";
		LABELS[NORTH_EAST] = "move_north_east";
		LABELS[NORTH]      = "move_north";
		LABELS[NORTH_WEST] = "move_north_west";
		LABELS[WEST]       = "move_west";
		LABELS[SOUTH_WEST] = "move_south_west";
		for(int i = 0; i < 8; i++)
			ATTACK[i] = new TFrame(15, i * 2, 1, 2);
	}
	
	private TAnimation idleAnimations[];
	private TAnimation movementAnimations[];
	
	public TMovementAnimationArray(TFrame upperLeftFrame) {
		movementAnimations = new TAnimation[8];
		idleAnimations     = new TAnimation[8];
		for(int i = 0; i < 8; i++) {
			final TFrame frames[] = new TFrame[4];
			for(int j = 0; j < 4; j++) {
				int xOff = ((j % 2 == 0) ? 0 : ((j == 1) ? 1 : 2));
				frames[j] = new TFrame(upperLeftFrame.getDataOffsetX() + xOff,
									   upperLeftFrame.getDataOffsetY() + (i * 2),
									   1,
									   2);
			}
			movementAnimations[i] = new TAnimation(LABELS[i], frames);
			idleAnimations[i]     = new TAnimation(LABELS[i].replace("move_", "idle_"), frames[0]);
		}
	}
	
	public void addToAnimationController(TAnimationController anim) {
		for(int i = 0; i < 8; i++) {
			anim.addAnimation(movementAnimations[i]);
			anim.addAnimation(idleAnimations[i]);
		}
	}
	
}
