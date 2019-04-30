package com.dave.util;

public class HashUtil {

	public static int MODULUR_VAL = 10000;

	final static int p = 16777619;

	public static int hashFNV1(String data) {
		// 使用FNV1hash算法
		int hash = (int) 2166136261L;
		for (int i = 0; i < data.length(); i++) {
			hash = (hash ^ data.charAt(i)) * p;
		}

		hash += hash << 13;
		hash ^= hash >> 7;
		hash += hash << 3;
		hash ^= hash >> 17;
		hash += hash << 5;
		// 如果算出来的值为负数则取其绝对值
		if (hash < 0) {
			hash = Math.abs(hash);
		}
		return hash;
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	public static int stringToHashMod(String data) {
		String md5val = Md5Digest.md5Utf8(data);
		int modval = HashUtil.hashFNV1(md5val) % MODULUR_VAL;
		return modval;
	}
	
	public static int md5ToHashMod(String md5){
		int modval = HashUtil.hashFNV1(md5) % MODULUR_VAL;
		return modval;
	}

	
	public static void main(String[] args) {
		int i = stringToHashMod("b19963a46a6e471b3388131c82588883");
		System.out.println(i);
	}
}
