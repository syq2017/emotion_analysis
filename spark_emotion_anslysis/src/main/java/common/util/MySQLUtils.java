package common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MySQLUtils {
	// JDBC 驱动名及数据库 URL
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/court_info";

	private static Connection conn = null;
	private static Statement stmt = null;

	// 数据库的用户名与密码
	private static final String USER = "root";
	private static final String PASS = "123456";

	/**
	 * 获取查询结果集
	 * @param sqlStr
	 * @return
	 * @throws ClassNotFoundException
	 */
	private static ResultSet getQueryResult(String sqlStr) throws ClassNotFoundException {
		try{
			// 注册 JDBC 驱动
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			// 执行查询
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sqlStr);
			return rs;
		}catch (SQLException se) {
			se.printStackTrace();
		}
		return null;
	}


	/**
	 * 得到id
	 * @param sql
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static ArrayList<String> getAllId(String sql,String idName) throws ClassNotFoundException, SQLException{
		ResultSet rs = getQueryResult(sql);
		ArrayList<String> ids = new ArrayList<>();
		while(rs.next()) {
			String id = rs.getString(idName);
			ids.add(id);
		}
		return ids;
	}






	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 讲数据分析结果存入数据库
	 * @param tableName
	 * @param dataPath
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void addAnalysisData(String tableName,String dataPath) throws ClassNotFoundException, SQLException, IOException {
		String tName = "t_ag_" + tableName;
		String sql = "insert into " + tName + "(year,product,"+tableName+") values(?,?,?)";
		//MySQL 批量插入
		Connection conn =  getConn();
		conn.setAutoCommit(false);
		PreparedStatement pst = conn.prepareStatement(sql);
		BufferedReader br = new BufferedReader(new FileReader(new File(dataPath)));
		String line = null;
		while((line = br.readLine()) != null) {
			String[] items = line.split("\t");
			pst.setString(1, items[0]);
			pst.setString(2, items[1]);
			pst.setString(3, items[2]);
			pst.addBatch();
		}
		//批量任务提交
		pst.executeBatch();
		conn.commit();
		pst.close();
	}



	public static Connection getConn() throws ClassNotFoundException, SQLException {

		return conn;
	}

	public static Statement getStmt(Connection conn) throws SQLException {
		return stmt;
	}

	public static boolean executeSql(String sql) throws ClassNotFoundException {
		try{
			conn = getConn();
			stmt = getStmt(conn);
			boolean res = stmt.execute(sql);
			return res;
		}catch (SQLException se) {
			se.printStackTrace();
		}
		return false;
	}




	public static void main(String[] args) throws ClassNotFoundException, SQLException {
	}

	private static void closeMySQL() throws SQLException {
		if(!stmt.isClosed()) {
			stmt.close();
		}
		if(!conn.isClosed()) {
			conn.close();
		}
	}
}
