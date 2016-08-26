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
			 * 1.����Mysql����
			 */
			Class.forName("com.mysql.jdbc.Driver");
			/*
			 * 2.ʹ��DriverManager��ȡ���ݿ�����
			 */
		     conn = DriverManager
					.getConnection("jdbc:mysql://127.0.0.1:3306/chatdata",
							"root", "123456789");
			/*
			 * 3.ʹ��connection����һ��statement����
			 */
			stmt = conn.createStatement();
			System.out.println("success");

		} catch (ClassNotFoundException | SQLException e) {
		
			System.out.println("���ݿ�����ʧ�ܣ�");
			
			e.printStackTrace();
		}

	}

	public static synchronized ResultSet run(String sql) {
        // ��ѯ���ݿ��ϵ��
		ResultSet set = null;
		try {
			set = stmt.executeQuery(sql);

		} catch (SQLException e) {
			System.out.println(" sql ��ѯʧ�ܣ�");
			e.printStackTrace();
		}
		return set;
	}

	public static synchronized boolean insert(String sql) throws SQLException {
		// �����µ��û��˺���Ϣ
		return stmt.execute(sql);

	}

	

}
