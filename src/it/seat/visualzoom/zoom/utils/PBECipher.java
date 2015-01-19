package it.seat.visualzoom.zoom.utils;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class PBECipher {

	static PBECipher cipher = null;

	Key key;
	Cipher encrypter;
	Cipher decrypter;

	private PBECipher() {
		byte[] salt = { 0xC, 0xA, 0xF, 0xE, 0xB, 0xA, 0xB, 0xE };
		try {
			PBEKeySpec keySpec = new PBEKeySpec(getClass().getName()
					.toCharArray(), salt, 1);
			AlgorithmParameterSpec algParamSpec = new PBEParameterSpec(keySpec
					.getSalt(), keySpec.getIterationCount());
			key = SecretKeyFactory.getInstance("PBE").generateSecret(keySpec);
			encrypter = Cipher.getInstance(key.getAlgorithm());
			decrypter = Cipher.getInstance(key.getAlgorithm());
			encrypter.init(Cipher.ENCRYPT_MODE, key, algParamSpec);
			decrypter.init(Cipher.DECRYPT_MODE, key, algParamSpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static PBECipher getInstance() {
		if (cipher == null) {
			cipher = new PBECipher();
		}
		return cipher;
	}

	public static String encrypt(String plaintext)
			throws IllegalBlockSizeException, BadPaddingException {
		byte[] ciphertext = getInstance().encrypter.doFinal(plaintext
				.getBytes());
		return Base64.encode(ciphertext);
	}

	public static String decrypt(String b64ciphertext)
			throws Base64DecodingException, IllegalBlockSizeException,
			BadPaddingException {
		byte[] ciphertext = Base64.decode(b64ciphertext);
		return new String(getInstance().decrypter.doFinal(ciphertext));
	}

	public static void main(String[] args) throws Exception {
		String ciphertext = PBECipher.encrypt("leonardo");
		System.out.println("Ciphertext(BASE64): " + ciphertext);
		String plaintext = PBECipher.decrypt(ciphertext);
		System.out.println("Plaintext: " + plaintext);
		plaintext = PBECipher.decrypt(ciphertext);
		System.out.println("Plaintext: " + plaintext);
	}

}
