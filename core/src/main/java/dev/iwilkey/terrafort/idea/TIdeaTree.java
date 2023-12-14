package dev.iwilkey.terrafort.idea;

import com.badlogic.gdx.utils.Array;

import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.TItemFunction;

/**
 * A static segment of memory allocated at the beginning of runtime that organizes the arbitrary list of {@link TItem}s into a tree for efficient use by the {@link TIdeaTreeInterface}.
 * @author Ian Wilkey (iwilkey)
 */
@SuppressWarnings("unchecked")
public final class TIdeaTree {

	public static final Array<TIdeaNode> ROOTS      = new Array<>();
	public static final Array<TIdeaNode> CREATABLES = new Array<>();
	
	static {
		// traverse through every possible TItemFunction...
		for(TItemFunction func : TItemFunction.values()) {
			// We don't want to make an idea tree node for natural items, though...
			if(func == TItemFunction.NATURAL)
				continue;
			final TIdeaNode funcRoot = new TIdeaNode(" " + func.name());
			ROOTS.add(funcRoot);
			// traverse through every possible item to see if the function matches...
			for(TItem item : TItem.values()) {
				if(item.getFunction() == func) {
					TIdeaNode funcChild = new TIdeaNode(" " + item.name());
					funcRoot.add(funcChild);
					CREATABLES.add(funcChild);
					TItem[] spec = item.getRecipe();
					final String r0 = (spec[0] != null) ? String.format("[ORANGE]%s[]", spec[0].name()) : "[GRAY]EMPTY[]";
					final String r1 = (spec[1] != null) ? String.format("[ORANGE]%s[]", spec[1].name()) : "[GRAY]EMPTY[]";
					final String r2 = (spec[2] != null) ? String.format("[ORANGE]%s[]", spec[2].name()) : "[GRAY]EMPTY[]";
					final String r3 = (spec[3] != null) ? String.format("[ORANGE]%s[]", spec[3].name()) : "[GRAY]EMPTY[]";
					final String recipe = String.format("%s | %s | %s | %s", r0, r1, r2, r3);
					final TIdeaNode funcChildSpec = new TIdeaNode(recipe);
					funcChild.add(funcChildSpec);
				}
			}
		}
	}
}
