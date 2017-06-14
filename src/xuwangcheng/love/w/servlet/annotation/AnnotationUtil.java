package xuwangcheng.love.w.servlet.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import xuwangcheng.love.w.servlet.AbstractHttpServlet;


/**
 * 注解解析类
 * @author xuwangcheng
 * @version 1.0.0.0,20170523
 *
 */
public class AnnotationUtil {
	
	private static Logger LOGGER = Logger.getLogger(Annotation.class);
	
	/**
	 * 解析ExecuteRequest注解,判断对应的servlet中是否有指定的方法来处理指定uri的请求
	 * @param servlet 指定servlet处理类实例
	 * @param uri 当前请求uri
	 * @return Method 请求对应的处理方法,没有返回null
	 */
	public static Method executeRequestMthod(AbstractHttpServlet servlet, String uri) {
		
		Class<?> clazz = servlet.getClass();	
		
		Method[] methods = clazz.getDeclaredMethods();		
		
		for (Method m:methods) {
			if (m.isAnnotationPresent(ExecuteRequest.class)) {
				ExecuteRequest executeRequest = m.getAnnotation(ExecuteRequest.class);
				String requestName = executeRequest.value();
				
				if ("".equals(requestName)) {
					requestName = m.getName();					
				}
				
				if (requestName.equals(uri)) {															
					return m;
				}
			}
		}
		return null;
	}
	/**
	 * 解析RequestBody注解,自动解析请求参数
	 * @param method 处理当前请求的方法
	 * @param request 当前请求request
	 * @throws Exception 
	 */
	public static Object[] parseRequestBody(Method method, HttpServletRequest request) {
		Class<?>[] paramsType = method.getParameterTypes();
		Annotation[][] ans = (Annotation[][]) method.getParameterAnnotations();
		
		Object[] requestParams  = new Object[paramsType.length];;
		
		if (paramsType.length > 2) {		
			for (int i = 2;i < ans.length;i++) {										
				
				for (int j = 0;j < ans[i].length;j++) {
					if (ans[i][j] instanceof RequestBody) {
						RequestBody requestBody = (RequestBody) ans[i][j];
						String parameterName = requestBody.value();
						
						if (!"".equals(parameterName)) {
							String parameterValue = request.getParameter(parameterName);
							requestParams[i] = getParameterValue(paramsType[i], parameterValue);
						}					
						continue;
					}
				}
			}
		}
		
		return requestParams;
	}
	
	private static Object getParameterValue(Class<?> clazz, String parameterValue) {
		String paramTypeName = clazz.getCanonicalName();
		Object defaultValue = null;
		try {
			if ("java.lang.Integer".equals(paramTypeName) || "int".equals(paramTypeName)) {
				defaultValue = 0;
				return Integer.valueOf(parameterValue);
			}		
			if ("java.lang.Double".equals(paramTypeName) || "double".equals(paramTypeName)) {
				defaultValue = 0.0d;
				return Double.valueOf(parameterValue);
			}
			if ("java.lang.Float".equals(paramTypeName) || "float".equals(paramTypeName)) {
				defaultValue = 0.0f;
				return Float.valueOf(parameterValue);
			}
			if ("java.lang.Byte".equals(paramTypeName) || "byte".equals(paramTypeName)) {
				defaultValue = (byte)0;
				return Byte.valueOf(parameterValue);
			}
			if ("java.lang.Short".equals(paramTypeName) || "short".equals(paramTypeName)) {
				defaultValue = (short)0;
				return Short.valueOf(parameterValue);
			}
			if ("java.lang.Long".equals(paramTypeName) || "long".equals(paramTypeName)) {
				defaultValue = 0L;
				return Long.valueOf(parameterValue);
			}
			if ("java.lang.Boolean".equals(paramTypeName) || "boolean".equals(paramTypeName)) {
				defaultValue = false;
				return Boolean.valueOf(parameterValue);
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error("Could not cast [" + parameterValue + "] to the type of " + paramTypeName + ":", e);
			//throw new Exception("Could not cast [" + parameterValue + "] to the type of " + paramTypeName);
			return defaultValue;
		}
		
		return parameterValue;
	}
	
	
	/**
	 * dao注入
	 * @param servlet
	 * @return
	 */
	public static boolean injectDao(AbstractHttpServlet servlet) {
		boolean flag = true;
		
		Class<?> clazz = servlet.getClass();
		
		
		Field[] fields =  clazz.getDeclaredFields();
		
		try {
			for (Field f:fields) {
				if (f.isAnnotationPresent(InjectDao.class)) {
					String daoName = f.getName();
					
					Method setMethod = clazz.getMethod("set" + daoName.substring(0,  1).toUpperCase() + daoName.substring(1), f.getType());
					Method getMethod = clazz.getMethod("get" + daoName.substring(0,  1).toUpperCase() + daoName.substring(1));
					
					Object object = getMethod.invoke(servlet);
					
					if (object == null) {
						setMethod.invoke(servlet, f.getType().newInstance());
					}								
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error("An error occurred during injection" + clazz.getName(), e);
		}
		
		return flag;
	}
	
}	
