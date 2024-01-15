package dev.iwilkey.terrafort.knowledge;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import dev.iwilkey.terrafort.gui.TDrawable;
import dev.iwilkey.terrafort.gui.lang.TLocale;
import dev.iwilkey.terrafort.obj.mob.TPlayer;
import dev.iwilkey.terrafort.world.TWorld;

/**
 * The ability to understand numbers, shapes, and their applications in various fields and everyday life.
 * @author Ian Wilkey (iwilkey)
 */
public final class TGeneralMathematicsKnowledge implements TKnowledge {

	public static final Drawable ICO  = TDrawable.fromSpriteSheet("sheets/items-icons.png", 3, 1, 1, 1);
	public static final String   NAME = TLocale.getLine(16);
	public static final String   DESC = TLocale.getLine(17);

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return DESC;
	}

	@Override
	public Drawable getIcon() {
		return ICO;
	}

	@Override
	public boolean practical() {
		return false;
	}

	@Override
	public void practice(TPlayer player, TWorld world) {
		
	}

	@Override
	public long getPracticeValue() {
		return 0;
	}

	@Override
	public long getLearnValue() {
		return 128;
	}

}
