package dev.iwilkey.terrafort.ui.containers.interfaces;

import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

import dev.iwilkey.terrafort.ui.TAnchor;
import dev.iwilkey.terrafort.ui.TDrawable;
import dev.iwilkey.terrafort.ui.TUserInterface;
import dev.iwilkey.terrafort.ui.containers.TContainer;
import dev.iwilkey.terrafort.ui.widgets.TInformationWidget;

/**
 * UI container that clearly states the Terrafort Copyright Notice & Terms of Use.
 * @author Ian Wilkey (iwilkey)
 */
public final class TTerrafortCopyrightNotice extends TContainer {
	
	public TTerrafortCopyrightNotice() {
		setInternalPadding(8, 8, 8, 8);
		setExternalPadding(0, 8, 8, 0);
		setAnchor(TAnchor.BOTTOM_RIGHT);
	}
	
	@Override
	public void pack(VisWindow window) {
		final VisTable body = new VisTable();
		final VisLabel info = new VisLabel();
		info.setStyle(TUserInterface.LABEL_STYLE);
		info.setFontScale(0.16f);
		info.setText("(c) 2023 Tessellation Specifications. All Rights Reserved.");
		info.setAlignment(Align.center);
		body.add(info).expand().fill().center();
		body.add(new TInformationWidget("Copyright Notice & Terms of Use", 
				"[YELLOW]Terrafort[] is actively developed, owned and copywritten by"
				+ "\n[YELLOW]Ian Wesley Wilkey[] (iwilkey) operating under the independent game studio"
				+ "\npseudonym \"[YELLOW]Tessellation Specifications[].\""
				+ "\n\nAt this time, the source code of [YELLOW]Terrafort[] is proprietary and"
				+ "\nnot available for public use, modification, or distribution. Additionally,"
				+ "\nthere should never be an attempt to reverse-engineer any [YELLOW]Terrafort[]"
				+ "\nexecutable."
				+ "\n\nStill, despite that which is stated above, [YELLOW]Tessellation[]"
				+ "\nwarmly welcomes community involvement in the ongoing development\n"
				+ "of [YELLOW]Terrafort[]. To participate in shaping its future, join the official"
				+ "\n[PURPLE]Discord[] server: https://discord.com")).padLeft(8).padRight(8);
		window.add(body).prefSize(256, 16).expand().fill();
		style.background = TDrawable.solidWithShadow(0x444444ff, 0x000000cc, 256, 32, 4, 4);
		window.setStyle(style);
	}

	@Override
	public void update() {

	}

}
