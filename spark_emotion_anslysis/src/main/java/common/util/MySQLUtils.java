package common.util;

import common.beans.RankMovie;
import common.beans.RankMovieList;
import common.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class MySQLUtils {

    private static Logger logger = LoggerFactory.getLogger(MySQLUtils.class.getName());
	// JDBC 驱动名及数据库 URL
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:33006/large_db?useSSL=false";

	private static Connection conn = null;
	private static Statement stmt = null;

	// 数据库的用户名与密码
	private static final String USER = "root";
	private static final String PASS = "123456";

	private static Set<String> movieIds = new HashSet<String>();

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            stmt = conn.createStatement();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //查询当前已有的电影ID，避免重复下载
    static {
        String sql = "select id from RankMovie";
        try {
            getAllId(sql, "id").stream().forEach(ele -> movieIds.add(ele));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	/**
	 * 获取查询结果集
	 * @param sqlStr
	 * @return
	 * @throws ClassNotFoundException
	 */
	private static ResultSet getQueryResult(String sqlStr) throws ClassNotFoundException {
		try{
			// 注册 JDBC 驱动
			Class.forName(JDBC_DRIVER);
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
	 * 得到ids
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

    /**
     * 插入电影基本信息
     * @param list
     * @return
     */
	public static void insertRankMovies (RankMovieList list) throws SQLException, ClassNotFoundException {
        Connection conn =  getConn();
        conn.setAutoCommit(false);
        String sql = "insert into RankMovie values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        ArrayList<RankMovie> movies = list.getMovies();
        int cnt = 0;
        for (RankMovie rankMovie : movies) {
            if (movieIds.contains(rankMovie.getId())) {
                continue;
            } else {
                movieIds.add(rankMovie.getId());
            }
            cnt ++;
            pst.setString(1, rankMovie.getId());

            final StringBuilder rating = new StringBuilder();
            rankMovie.getRating().stream().forEach( ele -> rating.append(ele + Constants.DELEMITER));
            pst.setString(2, rating.toString());

            pst.setInt(3, rankMovie.getRank());
            pst.setString(4, rankMovie.getCover_url());
            pst.setBoolean(5, rankMovie.isIs_playable());

            final StringBuilder types = new StringBuilder();
            rankMovie.getTypes().stream().forEach(ele -> types.append(ele + Constants.DELEMITER));
            pst.setString(6, types.toString());

            final StringBuilder regions = new StringBuilder();
            rankMovie.getRegions().stream().forEach(ele -> regions.append(ele + Constants.DELEMITER));
            pst.setString(7, regions.toString());

            pst.setString(8, rankMovie.getTitle());
            pst.setString(9, rankMovie.getUrl());
            pst.setString(10, rankMovie.getRelease_date());
            pst.setInt(11, rankMovie.getActor_count());
            pst.setInt(12, rankMovie.getVote_count());
            pst.setString(13, rankMovie.getScore());

            final StringBuilder actors = new StringBuilder();
            rankMovie.getActors().stream().forEach(ele -> actors.append(ele + Constants.DELEMITER));
            pst.setString(14, actors.toString());
            pst.setBoolean(15, rankMovie.getIs_watched());
            pst.addBatch();
            logger.info("{}", pst.toString());
        }
        //批量任务提交
        pst.executeBatch();
        conn.commit();
        pst.close();
        logger.info("insert {} rows!", cnt);
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

	private static void closeMySQL() throws SQLException {
		if(!stmt.isClosed()) {
			stmt.close();
		}
		if(!conn.isClosed()) {
			conn.close();
		}
	}

	@MyTestIgnore
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Connection connection = getConn();
        Properties clientInfo = connection.getClientInfo();
    }
}