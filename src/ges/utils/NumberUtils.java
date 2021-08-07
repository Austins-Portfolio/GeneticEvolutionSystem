package ges.utils;

import java.util.Random;

public class NumberUtils {

	private static Random random = new Random();
	
	public static int integerInRange(int min, int max) {
		return random.nextInt((max - min) + 1) + min;
	}
	
}
