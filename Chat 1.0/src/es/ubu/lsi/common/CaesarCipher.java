package es.ubu.lsi.common;

/**
 * Created by erwol on 04/03/2016 at 13:55
 * For further information visit http://erwol.com
 * 
 * Modified by Plamen Petkov on 07/04/2016
 */
public class CaesarCipher {

	public static String encrypt(String text, int key) {
		StringBuilder result = new StringBuilder(text.length());
		for (int i = 0; i < text.length(); i++)
			result.append((char) ((int) (text.charAt(i)) + key % 255));
		return result.toString();
	}

	public static String decrypt(String text, int key) {
		StringBuilder result = new StringBuilder(text.length());
		for (int i = 0; i < text.length(); i++)
			result.append((char) ((int) (text.charAt(i)) - key % 255));
		return result.toString();
	}
}
