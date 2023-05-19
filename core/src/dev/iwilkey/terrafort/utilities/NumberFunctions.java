package dev.iwilkey.terrafort.utilities;

public class NumberFunctions {
	
	
	public static double round(double num, int places) {
		double shift = Math.pow(10, places - 1);
		return (Math.round(num * shift) / shift);
	}
	
	
	
}
