package xuwangcheng.love.w.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;
import xuwangcheng.love.w.servlet.annotation.AnnotationUtil;
import xuwangcheng.love.w.util.Constants;


/**
 * servlet处理器<br>
 * 其他servlet继承此父类即可<br>
 * 返回的是json串,子类写法请参考example中的示例
 * 
 * @author xuwangcheng
 * @version 1.0.0.0,20170523
 *
 */

public abstract class AbstractHttpServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected static final Logger LOGGER = Logger.getLogger(AbstractHttpServlet.class);
	
	private AbstractHttpServlet servlet;
	
	protected void setServlet(AbstractHttpServlet servlet) {
		this.servlet = servlet;
		AnnotationUtil.injectDao(servlet);
	}
	
	public void service (HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        
		PrintWriter out = response.getWriter();
		
        String uri = request.getRequestURI();  
        
        LOGGER.info("get the uri:" + uri);
        
        uri = uri.substring(uri.lastIndexOf("/") + 1);
              
        Map<String, Object> ajaxData = new HashMap<String, Object>();      
        
        ajaxData.put(Constants.RETURN_CODE_ATTRIBUTE_NAME, Constants.ERROR_RETURN_CODE);
		
		Method m = AnnotationUtil.executeRequestMthod(servlet, uri);

		
		if (m == null) {
			ajaxData.put(Constants.RETURN_MSG_ATTRIBUTE_NAME, "Did not find the right processing method");
			
			LOGGER.info("Did not find the right processing method:" + uri);
			
			out.println(JSONObject.fromObject(ajaxData));
			return;
		}
		
		Object[] customParams = AnnotationUtil.parseRequestBody(m, request);;
		customParams[0] = ajaxData;
		customParams[1] = request;
		try {
			m.invoke(servlet, customParams);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("The method " + m.getName() + " is not correct,please check! ", e);
			ajaxData.put(Constants.RETURN_MSG_ATTRIBUTE_NAME, "System processing error!");
			e.printStackTrace();
		}
               
        LOGGER.info("[" + uri + "] return json string:" + JSONObject.fromObject(ajaxData));
        out.println(JSONObject.fromObject(ajaxData));
	}
	
}
