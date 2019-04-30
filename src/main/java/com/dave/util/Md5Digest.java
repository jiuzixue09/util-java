package com.dave.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;
import java.nio.charset.Charset;


public class Md5Digest 
{
	public static final Charset gbkCharset = Charset.forName("GBK"), utf8Charset = Charset.forName("UTF-8"), iso8859Charset = Charset.forName("ISO8859-1");
	
	public static byte[] md5Bytes(String str, Charset charset) {
		return DigestUtils.md5(str.getBytes(charset));
	}
	public static String md5(String str, Charset charset) {
		return toHexString(md5Bytes(str, charset));
	}
	
	public static String md5(byte[] bytes) {
		return toHexString(DigestUtils.md5(bytes));
	}
	
	public static BigInteger md5BigInteger(String str, Charset charset){
		return new BigInteger(1,md5Bytes(str, charset));
	}
	// It is unclear which charset has been used for the entries in the database.  However, according to our tests, at least many of the crawlers are currently using gbk.
	// As of Feb 17 2013, all entries in videos and video_exts tables with the wrong urlmd5 (regarded as UTF-8) has been deleted; there are several hundreds of them, and no incorrect vfmd5 has been detected so far.
	// Consequently, UTF-8 should be used in the future.
	public static String md5Gbk(String string) { return md5(string, gbkCharset); }
	public static String md5Utf8(String string) { return md5(string, utf8Charset); }
	public static byte[] md5Utf8Bytes(String string) { return md5Bytes(string, utf8Charset); }
	// this is used in the database (in principle it would be easier to do this at the database side so that we wouldn't need to keep the md5 values consistent, but this would require too much source changes as well as some extra md5 computation)
	public static String md5(String string) { return md5Utf8(string); }
	
	
	public static final String hexChars = "0123456789abcdef";
	public static final String hexCharsUpper = "0123456789ABCDEF";
	public static String toHexString(byte[] bytes) { // same as MySQL's HEX function
		int strLen = bytes.length * 2;
		StringBuilder buf = new StringBuilder(strLen);
		for (int i = 0; i < bytes.length; ++i) {
			byte val = bytes[i];
			buf.append(hexChars.charAt((val >>> 4) & 0x0f)); // without the &, the sign bit will be set (byte is a signed type...) 
			buf.append(hexChars.charAt(val & 0x0f));
		}
		return buf.toString();
	}

}
