package dev.iwilkey.terrafort.input;

import java.util.HashMap;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;

import dev.iwilkey.terrafort.Terrafort;

/**
 * The KeyBinding class represents a mapping of named bindings to input codes.
 * It allows binding keys or buttons to specific actions or functionalities.
 */
public final class KeyBinding {
	
	private HashMap<String, Integer> bindingMap;
	
	/**
	 * Constructs a new KeyBinding instance.
	 */
	public KeyBinding() {
		bindingMap = new HashMap<>();
	}
	
	/**
	 * Binds the specified input code to the given name.
	 * @param name the name of the binding
	 * @param code the input code to bind
	 */
	public void bind(String name, int code) {
		if(getCodeType(code) == InputType.NO_TYPE)
			return;
		bindingMap.put(name, code);
	}
	
	/**
	 * Checks if the binding associated with the given name was just pressed down.
	 * @param binding the name of the binding to check
	 * @return true if the binding was just pressed down, false otherwise
	 */
	public boolean bindingJustDown(String binding) {
		if(!verifyKeyBinding(binding))
			return false;
		int code = bindingMap.get(binding);
		InputType type = getCodeType(code);
		switch(type) {
			case KEY_TYPE:
				return InputHandler.keyJustDown(code);
			case BUTTON_TYPE:
				return InputHandler.cursorJustDown(code);
			default:
				break;
		}
		return false;
	}
	
	/**
	 * Checks if the binding associated with the given name is currently active.
	 * @param binding the name of the binding to check
	 * @return true if the binding is currently active, false otherwise
	 */
	public boolean bindingCurrent(String binding) {
		if(!verifyKeyBinding(binding))
			return false;
		int code = bindingMap.get(binding);
		InputType type = getCodeType(code);
		switch(type) {
			case KEY_TYPE:
				return InputHandler.keyCurrent(code);
			case BUTTON_TYPE:
				return InputHandler.cursorCurrent(code);
			default:
				break;
		}
		return false;
	}
	
	/**
	 * Checks if the binding associated with the given name was just released.
	 * @param binding the name of the binding to check
	 * @return true if the binding was just released, false otherwise
	 */
	public boolean bindingJustUp(String binding) {
		if(!verifyKeyBinding(binding))
			return false;
		int code = bindingMap.get(binding);
		InputType type = getCodeType(code);
		switch(type) {
			case KEY_TYPE:
				return InputHandler.keyJustUp(code);
			case BUTTON_TYPE:
				return InputHandler.cursorJustUp(code);
			default:
				break;
		}
		return false;
	}
	
	private InputType getCodeType(int code) {
		// Check if it is a button.
		if(code == Buttons.LEFT || code == Buttons.RIGHT || 
				code == Buttons.BACK || code == Buttons.MIDDLE ||
				code == Buttons.FORWARD)
			return InputType.BUTTON_TYPE;
		// Check if it is a key.
		if(Input.Keys.toString(code) != null)
			return InputType.KEY_TYPE;
		// It's not a registered input type, therefore illegal.
		Terrafort.log("\"" + code + "\" input code is not recognized by the Terrafort Engine and therefore cannot be bound.");
		return InputType.NO_TYPE;
	}
	
	private boolean verifyKeyBinding(String binding) {
		if(bindingMap.containsKey(binding))
			return true;
		Terrafort.log("\"" + binding + "\" key binding is not recognized by the Terrafort Engine and therefore cannot be processed.");
		return false;
	}
	
}
