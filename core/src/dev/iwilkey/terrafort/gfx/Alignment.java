package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.object.GameObject2;
import imgui.ImGui;
import imgui.ImVec2;

/**
 * Provides methods for aligning GUI elements and game objects based on specified anchors.
 * The alignment is based on the dimensions of the window or game object and the desired anchor position.
 * Supports ImGui GUI and GameObject2.
 * 
 * The anchor positions are as follows:
 * - TOP_LEFT: Aligns to the top-left corner of the window or game object.
 * - TOP_CENTER: Aligns to the top-center of the window or game object.
 * - TOP_RIGHT: Aligns to the top-right corner of the window or game object.
 * - CENTER_LEFT: Aligns to the center-left of the window or game object.
 * - CENTER: Aligns to the center of the window or game object.
 * - CENTER_RIGHT: Aligns to the center-right of the window or game object.
 * - BOTTOM_LEFT: Aligns to the bottom-left corner of the window or game object.
 * - BOTTOM_CENTER: Aligns to the bottom-center of the window or game object.
 * - BOTTOM_RIGHT: Aligns to the bottom-right corner of the window or game object.
 * 
 * For GUI alignment, the methods should be called before the ImGui.end() call to account for the correct dimensions of the window.
 * For game object alignment, the methods return the aligned position vector.
 * 
 * NOTE: The tab size may vary depending on your text editor or IDE settings.
 * 
 * @author iwilkey
 */
public final class Alignment {

    /**
     * Aligns the ImGui GUI based on the specified anchor.
     * This method should be called before the ImGui.end() call to account for the correct dimensions of the window.
     *
     * @param anchor the anchor position to align the GUI
     */
    public static void alignGui(Anchor anchor) {
        ImVec2 pos = anchorTo(new ImVec2(ImGui.getWindowWidth(), ImGui.getWindowHeight()), anchor);
        ImGui.setWindowPos(pos.x, pos.y);
    }

    /**
     * Aligns the ImGui GUI with padding based on the specified anchor and padding values.
     * This method should be called before the ImGui.end() call to account for the correct dimensions of the window.
     *
     * @param anchor the anchor position to align the GUI
     * @param padX   the padding value on the X-axis
     * @param padY   the padding value on the Y-axis
     */
    public static void alignGui(Anchor anchor, float padX, float padY) {
        ImVec2 pos = anchorTo(new ImVec2(ImGui.getWindowWidth(), ImGui.getWindowHeight()), anchor);
        ImGui.setWindowPos(pos.x + padX, pos.y + padY);
    }

    /**
     * Aligns the ImGui GUI with lerp centering based on the specified anchor and lerp center value.
     * This method should be called before the ImGui.end() call to account for the correct dimensions of the window.
     *
     * @param anchor      the anchor position to align the GUI
     * @param lerpCenter  the lerp center value for smooth transition
     */
    public static void alignGui(Anchor anchor, float lerpCenter) {
        lerpCenter = Math.min(100.0f, lerpCenter);
        lerpCenter = Math.max(0.0f, lerpCenter);
        ImVec2 center = anchorTo(new ImVec2(ImGui.getWindowWidth(), ImGui.getWindowHeight()), Anchor.CENTER);
        ImVec2 anc = anchorTo(new ImVec2(ImGui.getWindowWidth(), ImGui.getWindowHeight()), anchor);
        float x = anc.x + ((center.x - anc.x) * (lerpCenter / 100.0f));
        float y = anc.y + ((center.y - anc.y) * (lerpCenter / 100.0f));
        ImGui.setWindowPos(x, y);
    }

    /**
     * Aligns the GameObject2 based on the specified anchor and returns the aligned position vector.
     *
     * @param go     the GameObject2 to align
     * @param anchor the anchor position to align the GameObject2
     * @return the aligned position vector
     */
    public static Vector2 alignGameObject2(GameObject2 go, Anchor anchor) {
        ImVec2 pos = anchorTo(new ImVec2(go.getWidth(), go.getHeight()), anchor);
        return new Vector2((int) pos.x, (int) pos.y);
    }

    /**
     * Aligns the GameObject2 with padding based on the specified anchor, padding values, and returns the aligned position vector.
     *
     * @param go     the GameObject2 to align
     * @param anchor the anchor position to align the GameObject2
     * @param padX   the padding value on the X-axis
     * @param padY   the padding value on the Y-axis
     * @return the aligned position vector with padding
     */
    public static Vector2 alignGameObject2(GameObject2 go, Anchor anchor, float padX, float padY) {
        ImVec2 pos = anchorTo(new ImVec2(go.getWidth(), go.getHeight()), anchor);
        return new Vector2((int) pos.x + padX, (int) pos.y + padY);
    }

    /**
     * Aligns the GameObject2 with lerp centering based on the specified anchor, lerp center value, and returns the aligned position vector.
     *
     * @param go          the GameObject2 to align
     * @param anchor      the anchor position to align the GameObject2
     * @param lerpCenter  the lerp center value for smooth transition
     * @return the aligned position vector with lerp centering
     */
    public static Vector2 alignGameObject2(GameObject2 go, Anchor anchor, float lerpCenter) {
        lerpCenter = Math.min(100.0f, lerpCenter);
        lerpCenter = Math.max(0.0f, lerpCenter);
        ImVec2 center = anchorTo(new ImVec2(go.getWidth(), go.getHeight()), Anchor.CENTER);
        ImVec2 anc = anchorTo(new ImVec2(go.getWidth(), go.getHeight()), anchor);
        float x = anc.x + ((center.x - anc.x) * (lerpCenter / 100.0f));
        float y = anc.y + ((center.y - anc.y) * (lerpCenter / 100.0f));
        return new Vector2((int) x, (int) y);
    }

    private static ImVec2 anchorTo(ImVec2 dimensions, Anchor anchor) {
        ImVec2 ret = new ImVec2(-1, -1);
        final int ww = Gdx.graphics.getWidth();
        final int hh = Gdx.graphics.getHeight();
        final int cax = ww / 2;
        final int cay = hh / 2;
        final int dx = (int) dimensions.x;
        final int dy = (int) dimensions.y;
        final int cdx = dx / 2;
        final int cdy = dy / 2;
        switch (anchor) {
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
}
