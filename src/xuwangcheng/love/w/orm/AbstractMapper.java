package xuwangcheng.love.w.orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import xuwangcheng.love.w.util.LovePropUtil;

/**
 * 对JDBC的简单封装类<br>
 * 使用：class UserMapper extends EntityMapper&lt;User&gt;{}<br>
 * UserMapper mapper = new UserMapper();<br>
 * mapper.execSQL(sqlStr, [Object[]]);<br>
 * 	
 * 注意：不支持属性为复杂对象类型的对象
 * @author xuwangcheng
 * @version 20170327
 *
 * @param <T>
 */
public abstract class AbstractMapper<T> {
	
	protected static final Logger LOGGER = Logger.getLogger(AbstractMapper.class);
	
	private static final String SELECT_MODE = "select";
	
	private static final String DELETE_MODE = "delete";
	
	private static final String UPDATE_MODE = "update";
	
	private static final String INSERT_MODE = "insert";
	
	/**
	 * 数据库连接
	 */
	private Connection conn;
	
	/**
	 * 预编译sql
	 */
	private PreparedStatement ps;
	
	/**
	 * 结果集
	 */
	private ResultSet rs;
	
	/**
	 * 执行update/delete等语句返回的操作行数
	 */
	private int ret = 0;
	
	/**
	 * 查询语句查询的结果数量
	 */
	private int queryCount = 0;
	
	/**
	 * sql语句
	 */
	private String sqlStr;
	
	/**
	 * 替换sql语句中的占位符?的参数
	 */
	private Object[] arguments;
	
	private Class<T> clazz;
	
	
	/**
	 * 返回的对象集合<br>
	 * 目前只针对select * from语句
	 */
	private List<T> objectList;
	
	/**
	 * 返回对象数组集合<br>
	 * 例如select count(*) from  结果获取为objectArray.get(0)[0]<br>
	 * 
	 */
	private List<Object[]> objectArray;
	
	private boolean executeFlag = false;
	
	public AbstractMapper() {
		//通过反射获取传入的实体类型
		ParameterizedType type = (ParameterizedType)this.getClass().getGenericSuperclass();
		this.clazz = (Class)type.getActualTypeArguments()[0];		
	}
	
	/**
	 * 传入sql语句和占位参数<br>
	 * 通过相关方法获取执行的结果
	 * @param sqlStr
	 * @param arguments
	 * @throws Exception
	 */
	public void execSQL(String sqlStr, Object... arguments) throws Exception {
		
		this.ret = 0;
		this.queryCount = 0;
		this.arguments = null;
		this.objectList = null;
		this.objectArray = null;
		this.executeFlag = false;
		
		this.sqlStr = sqlStr;
		
		if (arguments != null) {
			this.arguments = arguments;
		}
		
		LOGGER.debug("Exec the sql:" + sqlStr);
		
		try {
			execSQL();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}
	
	/**
	 * 获取返回的一个指定的对象实例<br>
	 * 多个只返回第一个
	 * 
	 * @return
	 */
	public T getObject() {
		if (objectList.size() != 0) {
			return objectList.get(0);
		}
		return null;
	}
	
	/**
	 * 返回对象集合,只有select * from才会用此接收<br>
	 * 要设置的属性必须要有set方法
	 * @return
	 */
	public List<T> getObjectList() {
		return this.objectList;
		
	}
	
	/**
	 * 影响到的记录行数<br>
	 * 默认为0
	 * @return
	 */
	public int getEffectRowCount() {			
		return this.ret;			
	}	
	
	/**
	 * 返回对象数组集合,除select * from之外其他的返回都用此接收
	 * @return
	 */
	public List<Object[]> getReturnObjects() {
		return this.objectArray;
	}
	
	/**
	 * 通过查询语句查询到的记录数
	 * @return
	 */
	public int getQueryCount() {
		return this.queryCount;
	}
	
	/**
	 * 通过工具类获取连接
	 * @throws Exception
	 */
	private void getConnection() throws Exception {
		this.conn = DBUtil.getConnection();
	}
	
	/**
	 * 替换sql语句中的占位符
	 * 参数必须对应顺序
	 * @throws Exception
	 */
	private void setSQLArguments() throws Exception {
		int agruNum = StringUtils.countMatches(sqlStr, "?");
		
		if (agruNum == 0) {
			return;
		}
		
		//没有参数或者占位符数量大于参数数组长度
		if (this.arguments == null || agruNum > this.arguments.length) {
			LOGGER.error("SQL statements and parameters are not corresponding");
			throw new Exception("SQL statements and parameters are not corresponding");
		}
		
		//根据参数类型设置
		for (int i = 0;i < agruNum;i++) {
			
			if (this.arguments[i] instanceof String) {
				ps.setString((i + 1), (String) this.arguments[i]);
			}
			
			if (this.arguments[i] instanceof Date) {
				ps.setDate((i + 1), (Date) this.arguments[i]);
			}
			
			if (this.arguments[i] instanceof Integer) {
				ps.setInt((i + 1), (Integer) this.arguments[i]);
			}
			
			if (this.arguments[i] instanceof Double) {
				ps.setDouble((i + 1), (Double) this.arguments[i]);
			}
			
			if (this.arguments[i] instanceof Long) {
				ps.setLong((i + 1), (Long)this.arguments[i]);
			}
			
			if (this.arguments[i] instanceof Boolean) {
				ps.setBoolean((i + 1), (Boolean)this.arguments[i]);
			}
			
			if (this.arguments[i] instanceof Float) {
				ps.setFloat((i + 1), (Float)this.arguments[i]);
			}
			
		}
				
	}
	
	
	/**
	 * 执行sql语句
	 * @throws Exception
	 */
	private void execSQL () throws Exception {
		if (this.executeFlag || this.sqlStr.isEmpty()) {
			return;
		}
		String mode = sqlStr.split("\\s+")[0];
		
		getConnection();
		
		ps = this.conn.prepareStatement(sqlStr);
		setSQLArguments();
			
		try {
			
			switch (mode) {
			
			case SELECT_MODE:
				this.rs = ps.executeQuery();
			
				if ("*".equals(sqlStr.split("\\s+")[1]) || !sqlStr.split("\\s+")[1].contains("count")) {
					
					objectList = new ArrayList<T>();
					while (rs.next()) {
						objectList.add(newObject(rs));	
						this.queryCount++;
					}	
					
				} else {
					String[] resultNames = sqlStr.split("\\s+")[1].split(",");
					objectArray = new ArrayList<Object[]>();
					Object[] objects = null;
					while (rs.next()) {
						objects = new Object[resultNames.length];
						for (int i = 0;i < resultNames.length;i++) {
							objects[i] = rs.getString(resultNames[i]);
						}
						objectArray.add(objects);
						this.queryCount++;
					}					
				}
								
				break;
				
			case DELETE_MODE:
				this.ret = ps.executeUpdate();
				break;
				
			case UPDATE_MODE:
				this.ret = ps.executeUpdate();
				break;
				
			default:
				this.ret = ps.executeUpdate();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error("Execute SQL Fail:" + sqlStr, e);
			throw e;
		} finally {
			DBUtil.close(this.conn);
		}
		executeFlag = true;
		
	}
	
	private T newObject(ResultSet rset) 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException
			, InvocationTargetException, SQLException {
		T t = clazz.newInstance();
		
		Method[] methods = clazz.getMethods();
		Field[] fields = clazz.getDeclaredFields();
		
		for (Field f:fields) {
			for (Method m:methods) {
				if (m.getName().equals("set" + f.getName().substring(0, 1).toUpperCase() + f.getName().substring(1))) {
					Class<?>[] paramTypes = m.getParameterTypes();
							
					String paramTypeName = paramTypes[0].getCanonicalName();
					
					String columnName = LovePropUtil.getValue(clazz.getName().substring(clazz.getName().lastIndexOf(".") + 1) + "." + f.getName(), f.getName());
					
					try {
						if ("java.lang.Integer".equals(paramTypeName) || "int".equals(paramTypeName)) {
							m.invoke(t, rs.getInt(columnName));
						}
															
						if ("java.lang.String".equals(paramTypeName)) {
							m.invoke(t, rs.getString(columnName));
						}
						
						if ("java.lang.Double".equals(paramTypeName) || "double".equals(paramTypeName)) {
							m.invoke(t, rs.getDouble(columnName));
						}
						
						if ("java.lang.Long".equals(paramTypeName) || "long".equals(paramTypeName)) {
							m.invoke(t, rs.getLong(columnName));
						}
						
						if ("java.sql.Date".equals(paramTypeName)) {
							m.invoke(t, rs.getDate(columnName));
						}
						
					} catch (Exception e) {
						// TODO: handle exception
						//e.printStackTrace();
						LOGGER.debug("During parse resultSet occur a error:", e);
					}														
					break;
				}
			}
		}		
		return t;
	}
	
	
	
}
