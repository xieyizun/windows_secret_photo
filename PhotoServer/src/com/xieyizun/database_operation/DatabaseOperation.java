package com.xieyizun.database_operation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;



public class DatabaseOperation {
	//查询用户
	private final String userQuery = "SELECT user_name, user_password FROM user WHERE user_name = ? AND user_password = ?";
	 
	//注册用户
	private final String userNameQuery = "SELECT user_name FROM user WHERE user_name=?";
	private final String userEmailQuery = "SELECT user_email FROM user WHERE user_email=?";
	
	private final String userInsert = "INSERT INTO user(user_name, user_password, user_email) VALUES (?, ?, ?)";
	//
		
	private PreparedStatement userQueryStat = null;
	private PreparedStatement userInsertStat = null;
	private PreparedStatement userNameQueryStat = null;
	private PreparedStatement userEmailQueryStat = null;
	
	//实现单例模式
	private DatabaseOperation() {}
	private static DatabaseOperation op = new DatabaseOperation();
	//静态工厂
	public static DatabaseOperation getInstance() {
		return op;
	}
	//建立与数据库的连接
	public Connection getConnection() throws SQLException, IOException {
		
		//加载数据库驱动
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("加载驱动失败");
		}
		//连接数据库
		String url = "jdbc:mysql://localhost:3306/photo_project";
		String username = "xyz";
		String password = "jkljkl";
		return DriverManager.getConnection(url, username, password);
		
	}
	
	public void initPreparedStat(Connection conn) throws SQLException {
		userQueryStat = conn.prepareStatement(userQuery);
		userInsertStat = conn.prepareStatement(userInsert);
		userNameQueryStat = conn.prepareStatement(userNameQuery);
		userEmailQueryStat = conn.prepareStatement(userEmailQuery);
	}
	
	//用户登录
	public boolean loginUser(String name, String passwd) throws SQLException {
		if (name != null && passwd != null) {
			userQueryStat.setString(1, name);
			userQueryStat.setString(2, passwd);
			
			ResultSet result = userQueryStat.executeQuery();
			if (result.next()) {
				return true;
			}
		}
		return false;
	}
	
	//用户注册
	//0 OK，1 用户名存放，2 邮箱重复, -1数据库出错
	public int registerUser(String name, String passwd, String email) throws SQLException {
		if (name != null && passwd != null && email != null) {
			userNameQueryStat.setString(1, name);
			ResultSet nameExist = userNameQueryStat.executeQuery();
			if (nameExist.next()) {
				return 1;
			}
			
			userEmailQueryStat.setString(1, email);
			ResultSet emailExist = userEmailQueryStat.executeQuery();
			if (emailExist.next()) {
				return 2;
			}
			
			userInsertStat.setString(1, name);
			userInsertStat.setString(2, passwd);
			userInsertStat.setString(3, email);
			
			int row = userInsertStat.executeUpdate();
			System.out.println("row: " + row);
			if (row == 1) {
				return 0;
			} else {
				return -1;
			}
		}
		return -1;
	}
	
}
