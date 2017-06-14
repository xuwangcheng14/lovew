package xuwangcheng.love.w.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public class LovePropUtil {
	
	private static final Logger LOGGER = Logger.getLogger(LovePropUtil.class);
	
	private static Properties props = new Properties();
	
	public static String DB_CLASS_NAME;
	public static String DB_URL;
	public static String DB_USERNAME;
	public static String DB_PASSWORD;
	
	public static String DB_AUTO_CREATE_FLAG;
	
	public static List<String> TABLE_CREATE_SQL = new ArrayList<String>();
	
	
	static {
		try {
			props.load(LovePropUtil.class.getClassLoader()
				       .getResourceAsStream("lovew.properties"));
			LOGGER.info("load the lovew.properties Success!");
			
			DB_CLASS_NAME = props.getProperty("db.classname", "org.sqlite.JDBC");
			DB_URL = props.getProperty("db.url", "jdbc:sqlite:info.db");
			DB_USERNAME = props.getProperty("db.username", "");
			DB_PASSWORD = props.getProperty("db.password", "");
			
			LOGGER.info("get the db info:className=" + DB_CLASS_NAME + ",url=" + DB_URL + ",username=" + DB_USERNAME + ",password=" + DB_PASSWORD);
			
			DB_AUTO_CREATE_FLAG = props.getProperty("db.autoCreateTable", "false");
			
			Enumeration keys = props.propertyNames();
			
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				if (key.startsWith("table")) {
					TABLE_CREATE_SQL.add(props.getProperty(key));
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.error("Could not load the file:lovew.properties,Please check!", e);
		}
	}
	
	public static String getValue(String key) {
		return (String) props.get(key);
	}
	
	public static String getValue(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}
}
