package com.blueberrys.employeetimelogger.data;

import java.io.Serializable;
import java.util.Arrays;

import com.blueberrys.employeetimelogger.data.util.Encoder;

public class Admin implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3953398892390925660L;

	private byte[] encodedPass;

	public boolean testPass(String pass) {
		return (pass != null && Arrays.equals(Encoder.encodePass(pass), encodedPass));
	}

	/**
	 * 
	 * @return
	 */
	public boolean isPassSet() {
		return (encodedPass != null);
	}

	/**
	 * 
	 * @param pass
	 */
	public void setPass(String pass) {
		setEncodedPass(Encoder.encodePass(pass));
	}

	/**
	 * @param encodedPass the encodedPass to set
	 */
	public void setEncodedPass(byte[] encodedPass) {
		this.encodedPass = encodedPass;
	}

}
