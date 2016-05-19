package es.ubu.lsi.common;

/**
 * Created by erwol on 04/03/2016 at 13:55
 * For further information visit http://erwol.com
 * 
 * Modified by Plamen Petkov on 07/04/2016
 */
public class CaesarCipher {
	
	/**
	 * M�todo encrypt que utiliza el cifrado de Cesar para encriptar un texto,
	 * dada la clave de desplazamiento.
	 * 
	 * @param text el texto a encriptar
	 * @param key la clave de desplazamiento
	 * @return el texto encriptado
	 */
	public static String encrypt(String text, int key) {
		StringBuilder result = new StringBuilder(text.length());
		for (int i = 0; i < text.length(); i++)
			result.append((char) ((int) (text.charAt(i)) + key % 255));
		return result.toString();
	}
	
	/**
	 * M�todo decrypt que utiliza el cifrado de Cesar para desencriptar un texto,
	 * dada la clave de desplazamiento.
	 * 
	 * @param text el texto a desencriptar
	 * @param key la clave de desplazamiento
	 * @return el texto original
	 */
	public static String decrypt(String text, int key) {
		StringBuilder result = new StringBuilder(text.length());
		for (int i = 0; i < text.length(); i++)
			result.append((char) ((int) (text.charAt(i)) - key % 255));
		return result.toString();
	}
		
	/**
	 * Método encryptText. Encripta un texto con el prefijo "encrypted#"
	 * utilizando el algoritmo de cifrado de Cesar.
	 * Si el texto no comienza con el prefijo, se devuelve sin modificar.
	 * 
	 * @param text texto a cifrar
	 * @param key clave usada para cifrar
	 * @return el texto recibido cifrado o sin cifrar
	 */
	public static String encryptText(String text, int key) {
		String result = "encrypted#";
		if (text.startsWith(result)) //Si comienza con el prefijo indicado se cifra
			result = result + encrypt(text.substring(result.length()), key);
		return result;
	}
	
	/**
	 * decryptText method
	 * Método que desencripta texto cifrado con Cesar
	 * @param text Texto a desencriptar
	 * @param key Clave recibida para desencriptar
	 * @return Texto recibido desencriptado o no
	 */
	public static String decryptText(String text, int key) {
		//Si el texto no tiene el prefijo, no esta cifrado
		String prefix = "encrypted#";
		if (text.startsWith(prefix))
			return decrypt(text.substring(prefix.length()), key);
		return text;
	}
}
