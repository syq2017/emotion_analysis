package common.util;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import common.beans.MovieCommon;
import common.beans.RankMovie;
import common.beans.RankMovieList;
import common.constants.Constants;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import step1.corpus.process.CorpusSegUtils;
import step1.corpus.process.MovieCommonCorpusUtils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
        getAllId(sql, "id").stream().forEach(ele -> movieIds.add(ele));
    }

	/**
	 * 获取查询结果集
	 * @param sqlStr
	 * @return
	 * @throws ClassNotFoundException
	 */
	private static ResultSet getQueryResult(String sqlStr) {
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
		} catch (ClassNotFoundException e) {
            e.printStackTrace();
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
	public static ArrayList<String> getAllId(String sql,String idName) {
		ResultSet rs = getQueryResult(sql);
		ArrayList<String> ids = new ArrayList<>();
        try {
		    while(rs.next()) {
                String id = rs.getString(idName);
                ids.add(id);
            }
		} catch (SQLException e) {
            e.printStackTrace();
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

    /**
     * 插入电影短评信息
     * @param datas
     */
    public static void insertMovieCommons(ArrayList<MovieCommon> datas) {
        if (datas == null || datas.size() == 0) {
            return;
        }
        Connection conn = null;
        PreparedStatement pst = null;
        String sql = "insert into MovieCommon values(?, ?, ?, ?, ?)";
        try {
            conn = getConn();
            conn.setAutoCommit(false);
            pst = conn.prepareStatement(sql);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            for (MovieCommon movieCommon : datas) {
                pst.setString(1, movieCommon.getMovieId());
                pst.setString(2, movieCommon.getUserName());
                if (!Constants.COMMENT_LEVEL_SET.contains(movieCommon.getCommonLevel())) {
                    continue;
                }
                pst.setString(3, movieCommon.getCommonLevel());
                pst.setString(4, movieCommon.getDate());
                pst.setString(5, movieCommon.getCommon());
                pst.addBatch();
                logger.info("{}", pst.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //批量任务提交
        try {
            pst.executeBatch();
            conn.commit();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        logger.info("insert {} rows!", datas.size());
    }

    /**
     * 查询所有的短评并分词，写入文件 dstFile
     * @param dstFile
     */
    public static void storeAllSegMovieCommons(String dstFile) {
        BufferedWriter writer = null;
        try {
            writer= new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(dstFile, true), "GB18030"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String sql = "select common_level,common from MovieCommon limit ";
        int start = 0;
        int step = 5000;
        while (true){
            StopWatch stopWatch = new StopWatch();
            String querySql = sql + start + "," + step;
            start += step;
            System.out.println("querySql:" + querySql);
            try {
                ResultSet queryResult = getQueryResult(querySql);
                if (queryResult.getFetchSize() == 0) break;
                while (queryResult.next()) {
                    String common = queryResult.getString("common");
                    JiebaSegmenter jiebaSegmenter = CorpusSegUtils.jiebaSegmenterPool.borrowObject();
                    StringBuilder stringBuilder = CorpusSegUtils.stringBuilderPool.borrowObject();

                    List<SegToken> segsTitle = jiebaSegmenter.process(common, JiebaSegmenter.SegMode.SEARCH);
                    segsTitle.removeIf(seg -> CorpusSegUtils.stopWords.contains(seg.word));
                    segsTitle.stream().forEach(ele -> stringBuilder.append(ele.word + " "));

                    String result = stringBuilder.toString() + "\r\n";
                    CorpusSegUtils.jiebaSegmenterPool.returnObject(jiebaSegmenter);
                    CorpusSegUtils.stringBuilderPool.returnObject(stringBuilder);
                    writer.write(result);
                }
                writer.flush();
                long time = stopWatch.getTime();
                logger.info("sql:{},cost:{}ms", sql, time);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询所有的短评级及评，写入文件 dstFile，格式： level    common(中间一个制表符分隔)
     * @param dstFile
     */
    public static void storeAllMovieCommonAndLevel(String dstFile) {
        BufferedWriter writer = null;
        try {
            writer= new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(dstFile, true), "GB18030"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String sql = "select common_level,common from MovieCommon limit ";
        int start = 0;
        int step = 5000;
        int total = 1120000;
        while (true) {
            String querySql = sql + start + "," + step;
            start += step;
            if (start > total) break;
            System.out.println("querySql:" + querySql);
            try {
                ResultSet queryResult = getQueryResult(querySql);
                while (queryResult.next()) {
                    String level = queryResult.getString("common_level");
                    String common = queryResult.getString("common");
                    writer.write(level + "\t" + common + "\r\n");
                }
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
//        Connection connection = getConn();
//        Properties clientInfo = connection.getClientInfo();
        String sql = "select common_level,common from MovieCommon limit 22875000,5000";
        ResultSet queryResult = getQueryResult(sql);
        System.out.println(queryResult.getFetchSize());

    }
}
