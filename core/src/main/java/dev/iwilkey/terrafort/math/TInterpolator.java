package dev.iwilkey.terrafort.math;

import com.badlogic.gdx.math.Interpolation;

import dev.iwilkey.terrafort.TClock;

/**
 * TInterpolator is a utility class for smooth interpolation between two float values. 
 * It provides a flexible way to interpolate between a current value and a target value 
 * using various interpolation equations provided by the LibGDX framework.
 * 
 * The speed of interpolation and the specific interpolation equation can be customized. 
 * This class can be used in scenarios where smooth transitions of numeric values are needed, 
 * such as animations, gradual changes in game properties, etc.
 * @author Ian Wilkey (iwilkey)
 */
public final class TInterpolator {
    
    private Interpolation equ;
    private float         prog;
    private float         speed;
    private float         current;
    private float         target;

    /**
     * Constructs a new TInterpolator with default settings.
     * Initializes the progress to 0.0 and speed to 1.0.
     */
    public TInterpolator(final float initial) {
    	equ     = Interpolation.sineIn;
        prog    = 0.0f;
        speed   = 1.0f;
        current = initial;
        target  = initial;
    }

    /**
     * Sets the target value for the interpolation.
     * Resets the interpolation progress to 0.0.
     * 
     * @param value The target value to interpolate towards.
     */
    public void set(final float value) {
        target = value;
        prog   = 0.0f;
    }
    
    /**
     * Force the target and current value to a specified value.
     */
    public void force(final float value) {
    	target  = value;
    	current = value;
    	prog    = 1.0f;
    }

    /**
     * Sets the interpolation equation to be used for the transition.
     * 
     * @param equation The Interpolation instance defining the transition behavior.
     */
    public void setEquation(final Interpolation equation) {
        equ = equation;
    }

    /**
     * Sets the speed of the interpolation.
     * Higher values result in faster transitions.
     * 
     * @param speed The speed factor for interpolation.
     */
    public void setSpeed(final float speed) {
        this.speed = speed;
    }

    /**
     * Retrieves the current interpolated value.
     * 
     * @return The current value resulting from the interpolation process.
     */
    public float get() {
        return current;
    }
    
    /**
     * Retrieves the target value.
     * 
     * @return The target value
     */
    public float getTarget() {
    	return target;
    }

    /**
     * Updates the interpolation progress and calculates the current interpolated value.
     * This method should be called regularly, for example in a game loop, to update 
     * the value based on the defined speed and interpolation equation.
     */
    public void update() {
        if(prog >= 1.0f)
            return;
        prog += ((float)TClock.dt()) * speed;
        current = equ.apply(current, target, prog);
    }
}
