package com.shopoholic.network;

import android.util.Log;

import com.google.common.base.Joiner;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HttpDigestAuth
{

	public static String userName="admin";
	public static String password="mypass";
	public static String realM="8AC74BD0018D507238924D65D0184E93";
	public static String requestType="POST";
	public static String nounceCount="12345";
	public static String nounce="12345";
	public static String clientNounce="123";
	public static String qOp="auth";

	public static String getDigestAuthentication(int type, String mUri)
	{
		if(type==1)
			requestType = "POST";
		else if(type==2)
			requestType = "GET";
		MessageDigest md5 = null;
		try{
			md5 = MessageDigest.getInstance("MD5");
		}
		catch(NoSuchAlgorithmException e){
			return null;
		}

		Joiner colonJoiner = Joiner.on(':');

		String HA1 = null;
		String A1 = null;
		try{
			md5.reset();
			String ha1str = colonJoiner.join(userName, realM, password);
			md5.update(ha1str.getBytes("ISO-8859-1"));
			byte[] ha1bytes = md5.digest();
			A1 = bytesToHexString(ha1bytes);
			Log.e("HATTEMP-->",newByteToHex(ha1bytes));
		}
		catch(UnsupportedEncodingException e){
			return null;
		}

		String A2 = null;
		try{
			md5.reset();
			String ha2str = colonJoiner.join(requestType,mUri);
			md5.update(ha2str.getBytes("ISO-8859-1"));
			A2 = bytesToHexString(md5.digest());
			Log.e("HAT2-->",newByteToHex(md5.digest()));

		}
		catch(UnsupportedEncodingException e){
			return null;
		}

		String HA3 = null;
		try{
			md5.reset();
			String ha3str = colonJoiner.join(A1, nounce,nounceCount,clientNounce,qOp,A2);
			md5.update(ha3str.getBytes("ISO-8859-1"));
			HA3 = bytesToHexString(md5.digest());

			Log.e("HAT3-->",newByteToHex(md5.digest()));
		}
		catch(UnsupportedEncodingException e){
			return null;
		}

		StringBuilder sb = new StringBuilder(128);
		sb.append("Digest ");
		sb.append("username"  ).append("=\"").append(userName).append("\",");
		sb.append("realm"  ).append("=\"").append(realM).append("\",");
		sb.append("nonce"  ).append("=\"").append(nounce).append("\",");
		sb.append("uri"     ).append("=\"").append(mUri).append("\",");
		sb.append("qop"     ).append("=\"").append(qOp).append("\",");
		sb.append("nc"     ).append("=\"").append(nounceCount).append("\",");
		sb.append("cnonce"   ).append("=\"").append(clientNounce).append("\",");
		sb.append("response").append("=\"").append(HA3).append("\",");
		sb.append("opaque").append("=\"").append("12").append("\"");
		return sb.toString();

	}




	
	private static final String HEX_LOOKUP = "0123456789abcdef";
	private static String bytesToHexString(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for(int i = 0; i < bytes.length; i++){
			sb.append(HEX_LOOKUP.charAt((bytes[i] & 0xF0) >> 4));
			sb.append(HEX_LOOKUP.charAt((bytes[i] & 0x0F) >> 0));
		}
		return sb.toString();
	}

	private static String newByteToHex(byte[] bytes)
	{
		StringBuffer hexString = new StringBuffer();
		for (int i=0; i<bytes.length; i++)
			hexString.append(Integer.toHexString(0xFF & bytes[i]));
		return hexString.toString();
	}

}
