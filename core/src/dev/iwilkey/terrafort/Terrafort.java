package dev.iwilkey.terrafort;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/**
	The Terrafort class represents the main entry point for the Terrafort engine.
	It extends the Lwjgl3Application class and provides additional logging functionality.
*/
public final class Terrafort extends Lwjgl3Application {

	/**
	Constructs a new Terrafort object with the specified application listener and configuration.
	@param listener the application listener for handling callbacks
	@param config the configuration for the LWJGL3 application
	*/
	public Terrafort(ApplicationListener listener, Lwjgl3ApplicationConfiguration config) {
		super(listener, config);
	}
	/**
	
	Logs a message to the console with a timestamp.
	@param message the message to be logged
	*/
	public static void log(String message) {
		System.out.println("[Terrafort Engine @ " + getDate() + "] " + message);
	}
	/**
	
	Logs a fatal error message to the error console with a timestamp and exits the application with a status code of -1.
	@param message the fatal error message to be logged
	*/
	public static void fatal(String message) {
		System.err.println("[Terrafort Engine FATAL @ " + getDate() + "] " + message);
		System.exit(-1);
	}
	/**
	
	Retrieves the current date and time in the format "yyyy-MM-dd_HH:mm:ss".
	@return the formatted date and time as a string
	*/
	private static String getDate() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
		String formattedDateTime = currentDateTime.format(formatter);
		return formattedDateTime;
	}
	
}