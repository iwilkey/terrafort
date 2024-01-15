package dev.iwilkey.terrafort.knowledge;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import dev.iwilkey.terrafort.gui.TDrawable;
import dev.iwilkey.terrafort.gui.lang.TLocale;
import dev.iwilkey.terrafort.obj.mob.TPlayer;
import dev.iwilkey.terrafort.world.TWorld;

/**
 * The basic ability to defend yourself against entities that do not like you. Parent to all attainable portable weapons.
 * @author Ian Wilkey (iwilkey)
 */
public final class TSelfDefenseKnowledge implements TKnowledge {

	public static final Drawable ICO  = TDrawable.fromSpriteSheet("sheets/items-icons.png", 2, 1, 1, 1);
	public static final String   NAME = TLocale.getLine(19);
	public static final String   DESC = TLocale.getLine(20);

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
