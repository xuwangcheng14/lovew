package xuwangcheng.love.w.example.orm;

import java.util.List;

public class PersonDao {
	
	public List<Person> list() throws Exception {
		PersonMapper mapper = new PersonMapper();
		String sql = "select * from person";
		
		try {
			mapper.execSQL(sql);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return mapper.getObjectList();
	}
	
	public Person get(Integer id) throws Exception {
		PersonMapper mapper = new PersonMapper();
		String sql = "select * from person where id=?";
		
		try {
			mapper.execSQL(sql, id);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		
		return mapper.getObject();
	}
	
	public int getCount() throws Exception {
		PersonMapper mapper = new PersonMapper();
		String sql = "select count(1) from person";
		
		try {
			mapper.execSQL(sql);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		
		return (int) mapper.getReturnObjects().get(0)[0];
	}
	
	public int save(Person person) throws Exception {
		PersonMapper mapper = new PersonMapper();
		String sql = "insert into person(id,name,city,age) values(?,?,?,?)";
		
		try {
			mapper.execSQL(sql, person.getId(), person.getId(), person.getName(), person.getCity(), person.getAge());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		
		return mapper.getEffectRowCount();
	}
}
