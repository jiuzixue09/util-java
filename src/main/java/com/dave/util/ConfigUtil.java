package com.dave.util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class ConfigUtil
{
	public static final String CONFIG_KEY = "config_properties_file_name";
	private static Properties prop = new Properties();
	static String configName =  "config.properties";
	//init para
	static{
		InputStream strm = null;
		try {
			String property = System.getProperty(CONFIG_KEY);
			if(StringUtils.isNotBlank(property)){
				configName = property;
			}
			File fname = new File(configName);
			if (fname.canRead()) {
				System.out.println("Using configuration file: " + fname.toString());
				strm = new FileInputStream(fname);
			} else {
				System.out.println("Configuration file " + fname.toString() + " not found, loading it from resources");
				strm = ConfigUtil.class.getResourceAsStream("/" + configName);
				if (strm == null) throw new RuntimeException("Cannot find resource: " + configName);
			}
			prop.load(new InputStreamReader(strm, "UTF-8"));
//			System.out.println("PROPERTIES:");
//			for (Map.Entry<Object, Object> ent: prop.entrySet())
//				System.out.println((String) ent.getKey() + " = " + (String) ent.getValue());
		} catch (Exception e) { throw new RuntimeException(e); } // config.property load failures are serious matters...
		finally {
			if (strm != null) try { strm.close(); } catch (Throwable e) { e.printStackTrace(); }
		}
	}
	//get para
	public static String getPara(String key)
	{  		  
		  return prop.getProperty(key);		  
	}
	// The value should be 1 or 0
	public static boolean getBooleanParameterWithDefault(String key, boolean defaultValue) {
		String valStr = getPara(key);
		if (valStr == null) return defaultValue;
		else return Integer.valueOf(valStr) != 0;
	}
	public static int getIntParameterWithDefault(String key, int defaultValue) {
		String valStr = getPara(key);
		if (valStr == null) return defaultValue;
		else return Integer.valueOf(valStr);
	}
	public static String getStringParameterWithDefault(String key, String defaultValue) {
		String valStr = getPara(key);
		return (valStr != null) ? valStr : defaultValue;
	}

}
