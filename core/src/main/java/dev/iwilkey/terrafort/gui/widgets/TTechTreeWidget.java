package dev.iwilkey.terrafort.gui.widgets;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;

import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gui.TUserInterface;
import dev.iwilkey.terrafort.tree.TNode;
import dev.iwilkey.terrafort.tree.TTechTree;

/**
 * A tech tree, laying out all knowledge of Terrafort.
 * @author Ian Wilkey (iwilkey)
 */
public final class TTechTreeWidget extends VisTable {
	
	/**
	 * The location of the Terrafort Tech Tree widget structure.
	 */
	public static final ArrayList<TNodeWidget> TREE_NODES;
	
	// an internal flag used to indicate that this is the first frame the tech tree is active after being dormant.
	private boolean start;
	
	/**
	 * Initializes the tech tree for the entire runtime.
	 */
	static {
		TREE_NODES = new ArrayList<>();
		// Create a reference to all node widgets first...
		for(final TNode n : TTechTree.STRUCTURE)
			TREE_NODES.add(new TNodeWidget(n.name, n.level, n.requires));
		// Go through a link node requirements...
		for(final TNodeWidget w : TREE_NODES) {
			final TNode r = w.getNodeNameRequirement();
			if(r == null)
				continue; // parent.
			for(final TNodeWidget ww : TREE_NODES)
				if(ww.getNodeName().equals(r.name))
					w.requires(ww);
		}
		// Finally, sort the tree by highest to lowest level...
		Collections.sort(TREE_NODES, (node1, node2) -> Integer.compare(node2.getNodeLevel(), node1.getNodeLevel()));
	}
	
	private final VisScrollPane   internal;
	private final VisTable        tree;
	
	public TTechTreeWidget() {
		final TTextWidget text = new TTextWidget("Knowledge");
		text.setAlignment(Align.center);
		add(text).center().prefSize(Gdx.graphics.getWidth() / 2f, 32 * TUserInterface.getGlobalScale());
		row();
		addSeparator().padBottom(16f);
		tree = new VisTable();
		internal = new VisScrollPane(tree);
		internal.setScrollbarsVisible(true);
		// from the top to the bottom, put the nodes in the UI widget...
		int currentLevel    = TREE_NODES.get(0).getNodeLevel() + 1;
	    VisTable levelTable = null; // Table for the current level
	    for(final TNodeWidget n : TREE_NODES) {
	        if (n.getNodeLevel() != currentLevel) {
	            if (levelTable != null) {
	                tree.add(levelTable).center().expandX().fillX();
	                tree.row();
	            }
	            levelTable = new VisTable(true);
	            currentLevel = n.getNodeLevel();
	        }
	        levelTable.add(n).padTop(64f * TUserInterface.getGlobalScale()).padLeft(4 * TUserInterface.getGlobalScale()).padRight(4 * TUserInterface.getGlobalScale());
	    }
	    if(levelTable != null) 
	        tree.add(levelTable).center().expandX().fillX();
		add(internal).top().fill().center().prefSize(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
		start  = false;
		internal.setFlingTime(2.0f);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
	    super.draw(batch, parentAlpha);
	    batch.flush();
	    batch.end();
	    final Rectangle scissors   = new Rectangle();
	    final Rectangle clipBounds = new Rectangle(internal.getX(), internal.getY(), internal.getWidth(), internal.getHeight() + (16f * TUserInterface.getGlobalScale()));
	    ScissorStack.calculateScissors(getStage().getCamera(), batch.getTransformMatrix(), clipBounds, scissors);
	    if(ScissorStack.pushScissors(scissors)) {
	        final ShapeRenderer re = TGraphics.GEOMETRIC_RENDERER;
	        re.setProjectionMatrix(getStage().getCamera().combined);
	        re.begin(ShapeRenderer.ShapeType.Filled);
	        re.setColor(Color.GRAY);
	        for(final TNodeWidget node : TREE_NODES) {
	            TNodeWidget parent = node.getNodeWidgetRequirement();
	            if(parent != null) {
	                final Vector2 childPosition = new Vector2(node.getWidth() / 2, 0);
	                node.localToStageCoordinates(childPosition);
	                final Vector2 parentPosition = new Vector2(parent.getWidth() / 2, parent.getHeight());
	                parent.localToStageCoordinates(parentPosition);
	                float lineWidth = 4;
	                re.rectLine(childPosition, parentPosition, lineWidth);
	            }
	        }
	        re.end();
	        ScissorStack.popScissors();
	    }
	    if(!start) {
	    	internal.setScrollY(internal.getMaxY());
	    	start = true;
	    }
	    batch.begin();
	}
	
}
