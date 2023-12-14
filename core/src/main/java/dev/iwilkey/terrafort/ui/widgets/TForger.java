package dev.iwilkey.terrafort.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTree;

import dev.iwilkey.terrafort.idea.TIdeaNode;
import dev.iwilkey.terrafort.idea.TIdeaTree;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.TItemFunction;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.ui.TDrawable;
import dev.iwilkey.terrafort.ui.TDroppable;
import dev.iwilkey.terrafort.ui.containers.interfaces.TInventoryInterface;

public class TForger extends VisTable implements Disposable {
	
	private TItemStackSlot slots[];
	
	public TForger(TPlayer player) {
		slots = new TItemStackSlot[4];
		final VisTable main = new VisTable();
		final VisTree<TIdeaNode, ?> tree = new VisTree<>();
		tree.getStyle().background = TDrawable.solid(0x111111ff, 1, 1);
		populateTree(tree);
		final VisScrollPane pane = new VisScrollPane(tree);
		main.add(pane).pad(8).expand().fill();
		main.row();
		main.addSeparator();
		add(main).prefSize(64 * 1.5f, 128 * 1.5f).expand().fill();
		row();
		final VisTable footer = new VisTable();
		for(int i = 0; i < 4; i++) {
			final TItemStackSlot slot = new TItemStackSlot(new TDroppable() {
				@Override
				public void dropcall() {
				}
			});
			slots[i] = slot;
			footer.add(slot).pad(TInventoryInterface.PADDING);
		}
		final VisTextButton forge = new VisTextButton("Forge");
		forge.getLabel().setStyle(TIdeaNode.LABEL_STYLE);
		forge.getLabel().setFontScale(0.16f);
		forge.setFocusBorderEnabled(false);
		forge.addListener(new ClickListener() {
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				TItem i = forge();
				if(i != null)
					player.giveItem(i);
	        }
		});
		footer.add(forge).pad(16);
		add(footer).prefSize(256 * 1.5f, 32 * 1.5f).expand().fill();
	}
	
	public void tick() {
		for(int i = 0; i < 4; i++)
			slots[i].tick();
	}
	
	/**
	 * Algorithm to match unique recipes with {@link TItem}s in memory to the users current forge slot configuration.
	 * Returns the matched item which will be added to the players inventory in exchange for slotted items.
	 */
	private TItem forge() {
		// look through every item and see if a recipe has been matched.
		for(TItem i : TItem.values()) {
			if(i.getFunction() == TItemFunction.NATURAL)
				continue;
			final TItem[] recipe = i.getRecipe();
			byte mbit   = 0x0;
			byte nnullb = 0x0;
			for(int bit = 0; bit < 4; bit++) {
				if(recipe[bit] != null) {
					if(slots[bit].getItemStack() != null) {
						if(recipe[bit] == slots[bit].getItemStack().getItem()) {
							// match at slot.
							mbit   |= (1 << bit);
							nnullb |= (1 << bit);
						}
					}
				} else {
					if(slots[bit].getItemStack() == null) {
						// null position match.
						mbit |= (1 << bit);
					}
				}
			}
			if(0b1111 == (mbit & 0b1111)) {
				for(int bit = 0; bit < 4; bit++) {
					if(((nnullb & (1 << bit)) >> bit) == 1) {
						// take one.
						slots[bit].getItemStack().dec();
					}
				}
				return i;
			}
		}
		return null;
	}
	
	private void populateTree(final VisTree<TIdeaNode, ?> tree) {
		for(final TIdeaNode root : TIdeaTree.ROOTS)
			tree.add(root);
	}

	@Override
	public void dispose() {
		
	}
	
}
