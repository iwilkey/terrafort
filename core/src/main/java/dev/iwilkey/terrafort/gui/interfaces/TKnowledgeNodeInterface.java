package dev.iwilkey.terrafort.gui.interfaces;

import com.kotcrab.vis.ui.widget.VisTable;

import dev.iwilkey.terrafort.gui.TEvent;
import dev.iwilkey.terrafort.gui.TUserInterface;
import dev.iwilkey.terrafort.gui.container.TPromptContainer;
import dev.iwilkey.terrafort.gui.lang.TLocale;
import dev.iwilkey.terrafort.gui.widgets.TTextButtonWidget;
import dev.iwilkey.terrafort.gui.widgets.TTextWidget;
import dev.iwilkey.terrafort.knowledge.tree.TKnowledgeTreeNode;
import dev.iwilkey.terrafort.state.TSinglePlayerWorld;

/**
 * An interface that allows the user to learn or manage any knowledge in Terrafort.
 * @author Ian Wilkey (iwilkey)
 */
public final class TKnowledgeNodeInterface extends TPromptContainer {

	private final TKnowledgeTreeNode topic;
	
	public TKnowledgeNodeInterface(TKnowledgeTreeNode topic, Object... objReference) {
		super(objReference);
		this.topic = topic;
		pack(internal);
	}
	
	@Override
	public void behavior(float dt) {
		
	}

	@Override
	public void pack(VisTable internal, Object... objReference) {
		if(!topic.learned) {
			if(TSinglePlayerWorld.getClient().getFunds() > topic.getKnowledge().getLearnValue()) {
				final TTextWidget text = new TTextWidget(TLocale.getLine(46) + " " + topic.getName() + " " + TLocale.getLine(47) + " " + topic.getKnowledge().getLearnValue() + " Funds?");
				text.setWrap(true);
				internal.add(text).padBottom(8f).prefWidth(256f);
				internal.row();
				internal.add(new TTextButtonWidget(TLocale.getLine(24), new TEvent() {
					@Override
					public void fire() {
						TSinglePlayerWorld.getClient().takeFunds(topic.getKnowledge().getLearnValue());
						topic.learned = true;
						TSinglePlayerWorld.getTree().buildTree();
						TUserInterface.mFreePrompt();
						TUserInterface.guiModuleMutexReferences -= 2;
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
		}
		window.add(internal);
		window.pack();
	}

}
