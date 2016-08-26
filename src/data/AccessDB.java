package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccessDB {

	public static Statement stmt = null;

	static {

		Connection conn=null;
		try{
			/*
			 * 1.加载Mysql驱动
			 */
			Class.forName("com.mysql.jdbc.Driver");
			/*
			 * 2.使用DriverManager获取数据库连接
			 */
		     conn = DriverManager
					.getConnection("jdbc:mysql://127.0.0.1:3306/chatdata",
							"root", "123456789");
			/*
			 * 3.使用connection创建一个statement对象
			 */
			stmt = conn.createStatement();
			System.out.println("success");

		} catch (ClassNotFoundException | SQLException e) {
		
			System.out.println("数据库连接失败！");
			
			e.printStackTrace();
		}

	}

	public static synchronized ResultSet run(String sql) {
        // 查询数据库关系表
		ResultSet set = null;
		try {
			set = stmt.executeQuery(sql);

		} catch (SQLException e) {
			System.out.println(" sql 查询失败！");
			e.printStackTrace();
		}
		return set;
	}

	public static synchronized boolean insert(String sql) throws SQLException {
		// 插入新的用户账号信息
		return stmt.execute(sql);

	}

	

}
