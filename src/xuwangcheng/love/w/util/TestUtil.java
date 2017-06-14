package xuwangcheng.love.w.util;

import java.io.IOException;

public class TestUtil {
	public static void main(String[] args) throws IOException {
		/*Set<Class<?>> set = ClassScanner.getClasses("xuwangcheng.love.w.example");
		System.out.println(set.size());
		for (Class<?> cls : set){
		    System.out.println(cls.getName());
		}*/
		
		System.out.println(LovePropUtil.DB_CLASS_NAME);
		System.out.println(LovePropUtil.DB_AUTO_CREATE_FLAG);
		System.out.println(LovePropUtil.TABLE_CREATE_SQL);
	}
}
