package com.dave.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressUtil {
	/***
	 * 压缩GZip
	 * 
	 * @param data
	 * @return
	 * @throws IOException 
	 */
	public static byte[] gZip(byte[] data) throws IOException {
		byte[] b = null;
		GZIPOutputStream gzip = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
		
			gzip = new GZIPOutputStream(bos);
			gzip.write(data);
			gzip.finish(); 
			b = bos.toByteArray(); 
		}  finally{
			if(bos!=null){
				 bos.close();
			} 
			if(gzip!=null){
				gzip.close();
			}
		}
		return b;
	}

	/***
	 * 解压GZip
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] unGZip(byte[] data) throws IOException {
		byte[] b = null;
		ByteArrayInputStream bis =null;
		GZIPInputStream gzip = null;
		ByteArrayOutputStream baos = null;
		try { 
			bis = new ByteArrayInputStream(data);
			gzip = new GZIPInputStream(bis);
			byte[] buf = new byte[1024];
			int num = -1;
			baos = new ByteArrayOutputStream();
			while ((num = gzip.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, num);
			}
			b = baos.toByteArray();
			baos.flush(); 
		} finally{
			if(bis!=null){
				bis.close();
			} 
			
			if(baos!=null){
				baos.close();
			} 
			if(gzip!=null){
				gzip.close();
			}
		}
		return b;
	}

 
}
