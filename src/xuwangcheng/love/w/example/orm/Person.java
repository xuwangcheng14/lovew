package xuwangcheng.love.w.example.orm;

public class Person {
	
	private Integer id;
	private String name;
	private String city;
	private Integer age;
	
	public Person(Integer id, String name, String city, Integer age) {
		super();
		this.id = id;
		this.name = name;
		this.city = city;
		this.age = age;
	}

	public Person() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
	
	
}
