/*
 * 2017.
 * Huida.Burt
 * CopyRight
 *
 *
 *
 */

package com.ruiyihong.toyshop.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException; 

public class md5util {
	
	/*public static void main(String[] args) {
		try {
			System.out.println("md5:"+getMD5("admin"));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	public static String getMD5(String val) throws NoSuchAlgorithmException{    
		MessageDigest md5 = MessageDigest.getInstance("MD5");    
		md5.update(val.getBytes());    
		byte[] m = md5.digest();
		return getString(m);    
	}

	public static String md5(String val) throws NoSuchAlgorithmException{
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(val.getBytes());
		byte[] m = md5.digest();
		return getString(m);
	}

	private static String getString(byte[] b){    
		StringBuffer buf = new StringBuffer();    
		for(int i = 0; i < b.length; i ++){    
			int a = b[i];
			if(a<0)  
				a+=256;  
			if(a<16)  
				buf.append("0");  
			buf.append(Integer.toHexString(a));  
			
		}            
		return buf.toString();
	}
 
}

  