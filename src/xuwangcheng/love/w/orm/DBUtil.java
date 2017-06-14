package xuwangcheng.love.w.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import xuwangcheng.love.w.util.LovePropUtil;

/**
 * 管理数据库连接的工具类
 * 可以获取数据库的连接对象
 * 以及关闭数据库连接
 * @author xuwangcheng
 * @version 20170217
 *
 */
public class DBUtil {
	
	private static final Logger LOGGER = Logger.getLogger(DBUtil.class);
	
	public static Connection getConnection() throws Exception {
		Connection con = null;
		try {
			Class.forName(LovePropUtil.DB_CLASS_NAME);
			con = DriverManager.getConnection(LovePropUtil.DB_URL, LovePropUtil.DB_USERNAME, LovePropUtil.DB_PASSWORD);		
		} catch (Exception e) {
			//e.printStackTrace();
			LOGGER.error("Could not get the DBConnection:", e);
			throw e;
		}
		return con;
	}
	public static void close(Connection con) throws SQLException {
		if(con != null){
			try {
				con.close();
			} catch (SQLException e) {				
				//e.printStackTrace();
				LOGGER.error("Could not close the DBConnection:", e);
				throw e;
			}
    	 }
     }
}

