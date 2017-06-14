package xuwangcheng.love.w.example.servlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import xuwangcheng.love.w.example.orm.Person;
import xuwangcheng.love.w.example.orm.PersonDao;
import xuwangcheng.love.w.servlet.AbstractHttpServlet;
import xuwangcheng.love.w.servlet.annotation.ExecuteRequest;
import xuwangcheng.love.w.servlet.annotation.InjectDao;
import xuwangcheng.love.w.servlet.annotation.RequestBody;
import xuwangcheng.love.w.util.Constants;

public class PersonServlet extends AbstractHttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 需要显式的调用父类中的setServlet(AbstractHttpServlet servlet方法)
	 */
	public PersonServlet() {
		// TODO Auto-generated constructor stub
		super.setServlet(this);
	}
	
	/**
	 * 通过Inject注解将指定dao注入到该servlet中，必须在类中提供该DAO属性的get/set方法
	 */	
	@InjectDao
	private PersonDao personDao;
		
	/**
	 * 前两个为默认参数,从第三个开始为用户自定义参数用于接受指定的请求内容<br>
	 * 自定义参数类型为8种基本数据类型及其包装类加上String，其他类型请用户自行通过request提供的方法接收
	 * @param ajaxData
	 * @param request
	 * @param name
	 * @throws Exception 
	 */
	@ExecuteRequest("getPerson")
	public void get(Map<String, Object> ajaxData, HttpServletRequest request, @RequestBody("name") String name) throws Exception {
		Person p = personDao.get(1);
		ajaxData.put("username", name);
		ajaxData.put(Constants.RETURN_CODE_ATTRIBUTE_NAME, Constants.CORRECT_RETURN_CODE);
		ajaxData.put(Constants.RETURN_MSG_ATTRIBUTE_NAME, "");
	}

	
	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}
	
	public PersonDao getPersonDao() {
		return personDao;
	}
}
