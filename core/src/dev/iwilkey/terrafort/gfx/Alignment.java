package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.Gdx;

import imgui.ImGui;
import imgui.ImVec2;

public class Alignment {
	
	private static ImVec2 anchorTo(ImVec2 dimensions, Anchor anchor) {
		ImVec2 ret = new ImVec2(-1, -1);
		final int ww = Gdx.graphics.getWidth();
		final int hh = Gdx.graphics.getHeight();
		final int cax = ww / 2;
		final int cay = hh / 2;
		final int dx = (int)dimensions.x;
		final int dy = (int)dimensions.y;
		final int cdx = dx / 2;
		final int cdy = dy / 2;
		switch(anchor) {
			case CENTER:
				ret = new ImVec2(cax - cdx, cay - cdy);
				break;
			case TOP_CENTER:
				ret = new ImVec2(cax - cdx, 0);
				break; 
			case TOP_RIGHT:
				ret = new ImVec2(ww - dx, 0);
				break;
			case CENTER_LEFT:
				ret = new ImVec2(0, cay - cdy);
				break;
			case CENTER_RIGHT:
				ret = new ImVec2(ww - dx, cay - cdy);
				break;
			case BOTTOM_LEFT:
				ret = new ImVec2(0, hh - dy);
				break;
			case BOTTOM_CENTER:
				ret = new ImVec2(cax - cdx, hh - dy);
				break;
			case BOTTOM_RIGHT:
				ret = new ImVec2(ww - dx, hh - dy);
				break;
			case TOP_LEFT:
			default:	
				ret = new ImVec2(0, 0);
		}
		return ret;
	}
	
	/**
	 * NOTE: This must be called a line before the ImGui.end() call to account for the correct dimensions of the window.
	 */
	public static void alignGui(Anchor anchor) {
		ImVec2 pos = anchorTo(new ImVec2(ImGui.getWindowWidth(), ImGui.getWindowHeight()), anchor);
		ImGui.setWindowPos(pos.x, pos.y);
	}
	
	/**
	 * NOTE: This must be called a line before the ImGui.end() call to account for the correct dimensions of the window.
	 */
	public static void alignGui(Anchor anchor, float padX, float padY) {
		ImVec2 pos = anchorTo(new ImVec2(ImGui.getWindowWidth(), ImGui.getWindowHeight()), anchor);
		ImGui.setWindowPos(pos.x + padX, pos.y + padY);
	}
	
	/**
	 * NOTE: This must be called a line before the ImGui.end() call to account for the correct dimensions of the window.
	 */
	public static void alignGui(Anchor anchor, float lerpCenter) {
		lerpCenter = (float)Math.min(100.0f, lerpCenter);
		lerpCenter = (float)Math.max(0.0f, lerpCenter);
		ImVec2 center = anchorTo(new ImVec2(ImGui.getWindowWidth(), ImGui.getWindowHeight()), Anchor.CENTER);
		ImVec2 anc = anchorTo(new ImVec2(ImGui.getWindowWidth(), ImGui.getWindowHeight()), anchor);
		float x = anc.x + ((center.x - anc.x) * (lerpCenter / 100.0f));
		float y = anc.y + ((center.y - anc.y) * (lerpCenter / 100.0f));
		ImGui.setWindowPos(x, y);
	}
	
}
