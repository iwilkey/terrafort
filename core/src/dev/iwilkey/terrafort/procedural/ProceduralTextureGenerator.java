package dev.iwilkey.terrafort.procedural;

import java.util.Random;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public final class ProceduralTextureGenerator implements Disposable {
	
	public static final int TEXTURE_RESOLUTION = 1024;
	public static final float POS_SCALE_MIN = 1.0f;
	public static final float POS_SCALE_MAX = 1000.0f;

	private final int width;
	private final int height;
	private final PerlinNoise noiseR;
	private final PerlinNoise noiseG;
	private final PerlinNoise noiseB;
	private final float posScaleHorR;
	private final float posScaleVertR;
	private final float posScaleHorG;
	private final float posScaleVertG;
	private final float posScaleHorB;
	private final float posScaleVertB;
	private final Pixmap pixmap;
	
	public ProceduralTextureGenerator() {
		// Create a square texture.
		this.width = TEXTURE_RESOLUTION;
		this.height = TEXTURE_RESOLUTION;
		final Random random = new Random();
		this.noiseR = new PerlinNoise(random.nextInt((int)Math.pow(2, 13)));
		this.noiseG = new PerlinNoise(random.nextInt((int)Math.pow(2, 13)));
		this.noiseB = new PerlinNoise(random.nextInt((int)Math.pow(2, 13)));
		posScaleHorR = POS_SCALE_MIN + random.nextFloat() * (POS_SCALE_MAX - POS_SCALE_MIN);
		posScaleHorG = POS_SCALE_MIN + random.nextFloat() * (POS_SCALE_MAX - POS_SCALE_MIN);
		posScaleHorB = POS_SCALE_MIN + random.nextFloat() * (POS_SCALE_MAX - POS_SCALE_MIN);
		posScaleVertR = POS_SCALE_MIN + random.nextFloat() * (POS_SCALE_MAX - POS_SCALE_MIN);
		posScaleVertG = POS_SCALE_MIN + random.nextFloat() * (POS_SCALE_MAX - POS_SCALE_MIN);
		posScaleVertB = POS_SCALE_MIN + random.nextFloat() * (POS_SCALE_MAX - POS_SCALE_MIN);
		this.pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
	}
	
	public Texture generate() {
		pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        int midWidth = width / 2;
        for(int x = 0; x < midWidth; ++x) {
            for(int y = 0; y < height; ++y) {
            	final float nr = (float)noiseR.noise(x / posScaleHorR, y / posScaleVertR);
            	final float ng = (float)noiseG.noise(x / posScaleHorG, y / posScaleVertG);
            	final float nb = (float)noiseB.noise(x / posScaleHorB, y / posScaleVertB);
            	final float valueR = (nr + 1) / 2.0f;
            	final float valueG = (ng + 1) / 2.0f;
            	final float valueB = (nb + 1) / 2.0f;
                pixmap.setColor(valueR, valueG, valueB, 1);
                pixmap.drawPixel(x, y);
                pixmap.drawPixel(width - x - 1, y);
            }
        }
        return getTextureFromCurrentPixmap();
	}
	
	public Pixmap getPixmap() {
		return pixmap;
	}
	
	public Texture getTextureFromCurrentPixmap() {
		return new Texture(pixmap);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	@Override
	public void dispose() {
		pixmap.dispose();
	}
	
}
