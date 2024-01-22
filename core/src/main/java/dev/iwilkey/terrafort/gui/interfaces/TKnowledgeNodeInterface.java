package dev.iwilkey.terrafort.gui.interfaces;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTable;

import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.clk.TEvent;
import dev.iwilkey.terrafort.gui.TUserInterface;
import dev.iwilkey.terrafort.gui.container.TPromptContainer;
import dev.iwilkey.terrafort.gui.lang.TLocale;
import dev.iwilkey.terrafort.gui.widgets.TTextButtonWidget;
import dev.iwilkey.terrafort.gui.widgets.TTextWidget;
import dev.iwilkey.terrafort.knowledge.tree.TKnowledgeTreeNode;
import dev.iwilkey.terrafort.obj.mob.TPlayer;

/**
 * An interface that allows the user to learn or manage any knowledge in Terrafort.
 * @author Ian Wilkey (iwilkey)
 */
public final class TKnowledgeNodeInterface extends TPromptContainer {
	
	private final TPlayer            client;
	private final TKnowledgeTreeNode topic;
	
	public TKnowledgeNodeInterface(TKnowledgeTreeNode topic, TPlayer client, Object... objReference) {
		super(objReference);
		this.client = client;
		this.topic  = topic;
		pack(internal);
	}
	
	@Override
	public void behavior(float dt) {
		// Listen for slot keyboard selection...
		if(topic.learned && topic.knowledge.practical()) {
			int lkp = TInput.lastKeyPressed;
			if(lkp != -1) {
				if(lkp >= 7 && lkp <= 16) {
					int slot = lkp - 7;
					client.knowledgeBar.equip(slot - 1, topic.getKnowledge());
					TUserInterface.mFreePrompt();
				}
			}
		}
	}

	@Override
	public void pack(VisTable internal, Object... objReference) {
		if(!topic.learned) {
			if(client.getFunds() > topic.getKnowledge().getLearnValue()) {
				final TTextWidget text = new TTextWidget(TLocale.getLine(46) + " " + topic.getName() + " " + TLocale.getLine(47) + " " + topic.getKnowledge().getLearnValue() + " Funds?");
				text.setWrap(true);
				internal.add(text).padBottom(8f).prefWidth(256f);
				internal.row();
				internal.add(new TTextButtonWidget(TLocale.getLine(24), new TEvent() {
					@Override
					public boolean fire() {
						client.takeFunds(topic.getKnowledge().getLearnValue());
						topic.learned = true;
						client.knowledgeTree.tree.buildTree();
						TUserInterface.mFreePrompt();
						return false;
					}	
				})).fillX();
			} else {
				final TTextWidget text = new TTextWidget(TLocale.getLine(49) + " " + topic.getName() + " " + TLocale.getLine(50) + ".");
				text.setWrap(true);
				internal.add(text).prefWidth(256f);
			}
		} else {
			final TTextWidget text = new TTextWidget(TLocale.getLine(52) + " " + topic.getName() + ".");
			text.setWrap(true);
			internal.add(text).prefWidth(256f);
			if(topic.knowledge.practical()) {
				internal.row();
				final TTextWidget t2 = new TTextWidget(TLocale.getLine(54));
				t2.setWrap(true);
				internal.add(t2).prefWidth(256f).pad(8f);
				final VisTable equipSlots = new VisTable();
				for(int i = 0; i < TKnowledgeBarInterface.KNOWLEDGE_SLOTS; i++) {
					final TTextButtonWidget slot = new TTextButtonWidget("" + (i + 1), null);
					final int ii = i;
					slot.addListener(new ClickListener() {
						public void clicked(InputEvent event, float x, float y) {
							client.knowledgeBar.equip(ii, topic.getKnowledge());
							TUserInterface.mFreePrompt();
						}
					});
					equipSlots.add(slot).pad(4f);
				}
				internal.row();
				internal.add(equipSlots).center().expand().fill();
			}
		}
		window.add(internal);
		window.pack();
	}

}
