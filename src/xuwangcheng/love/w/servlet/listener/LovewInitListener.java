package xuwangcheng.love.w.servlet.listener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import xuwangcheng.love.w.orm.DBUtil;
import xuwangcheng.love.w.util.LovePropUtil;

/**
 * 
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,20170531
 *
 */
public class LovewInitListener implements ServletContextListener {
	
	private Logger LOGGER = Logger.getLogger(LovewInitListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		//自动建表
		if ("true".equalsIgnoreCase(LovePropUtil.DB_AUTO_CREATE_FLAG)) {
			
			Connection conn = null;
			PreparedStatement ps = null;
			
			try {
				conn = DBUtil.getConnection();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return;
			}			
			
			for (String s:LovePropUtil.TABLE_CREATE_SQL) {
				try {
					ps = conn.prepareStatement(s);
					ps.executeUpdate();
					LOGGER.info("Create table success:" + s);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LOGGER.error("An error occurred when exec sql:" + s, e);
				}							
			}
		}
	}

}
