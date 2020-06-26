package ELT;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionDB {

	static Connection con;

	public static Connection getConnection(String database) throws ClassNotFoundException, SQLException {
		String url = "jdbc:mysql://localhost:3306/" + database;
		String user = "root";
		String password = "";

		Class.forName("com.mysql.jdbc.Driver");
		System.out.println(url);
		con = DriverManager.getConnection(url, user, password);
		return con;

	}
}
