package dev.iwilkey.terrafort.ui.containers;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.TEngine;
import dev.iwilkey.terrafort.ui.TWidgets;

/**
 * A ready-made {@link TContainer} for monitoring the metrics of the Terrafort engine during runtime.
 */
public final class TEngineMonitor extends TContainer {
	
	private VisLabel metrics;
	
	@Override
	public void pack(VisWindow window) {
		setInternalPadding(16, 16, 16, 16);
		final VisTable metricsSection = new VisTable();
		metrics = TWidgets.label();
		metrics.setFontScale(0.25f);
		metrics.setText(getMetrics());
		metricsSection.add(metrics);
		window.add(metricsSection).expand();
	}
	
	float ut = 0.0f;

	@Override
	public void update() {
		ut += TClock.dt();
		if(ut > 1.0f) {
			metrics.setText(getMetrics());
			ut = 0.0f;
		}
	}
	
	private String getMetrics() {
		return String.format("Terrafort %s\n"
				+ "Unlicensed copy :(\n\n"
				+ "dt: %.4f s\n"
				+ "fps: %.2f\n"
				+ "pt: %.4f s\n\n"
				+ "dim: %d x %d\n"
				+ "tle bat: %d\n"
				+ "tle d-cnt: %d\n"
				+ "tle-geo d-cnt: %d\n"
				+ "obj d-cnt: %d\n"
				+ "obj-geo d-cnt: %d\n\n"
				+ "chks mem-cnt: %d\n"
				+ "chks dorm-cnt: %d\n"
				+ "phys b-cnt: %d"
				+ "",
				TEngine.VERSION,
				TEngine.mDeltaTime, 
				(1.0f / TEngine.mDeltaTime),
				TEngine.mFrameProcessTime,
				TEngine.mScreenWidth,
				TEngine.mScreenHeight,
				TEngine.mTileBatches,
				TEngine.mTileDrawCount,
				TEngine.mTileLevelGeometryDrawCount,
				TEngine.mObjectDrawCount,
				TEngine.mObjectLevelGeometryDrawCount,
				TEngine.mChunksInMemory,
				TEngine.mChunksDormant,
				TEngine.mPhysicalBodies);
	}

}
