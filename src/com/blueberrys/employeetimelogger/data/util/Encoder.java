package com.blueberrys.employeetimelogger.data.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encoder {
	public static byte[] encodePass(String pass) {
		byte[] encodedData = null;

		try {
			byte[] passBytes = pass.getBytes(StandardCharsets.UTF_8);
			encodedData = MessageDigest.getInstance("MD5").digest(passBytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return encodedData;
	}
}
