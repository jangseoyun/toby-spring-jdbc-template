package com.spring.dao;

import com.spring.comtext.AddAllStrategy;
import com.spring.domain.QueryCrud;
import com.spring.domain.UserQueryImpl;
import com.spring.vo.User;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private DataSource dataSource;
    private QueryCrud userQuery;//적용 단계 AddAllStrategy 이외 쿼리 인터페이스 사용

    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
        this.userQuery = new UserQueryImpl();
    }

    public void add(User user) {

        try {
            Connection conn = dataSource.getConnection();
            //쿼리 바인딩
            PreparedStatement pstmt = new AddAllStrategy().makePreparedStatement(conn);
            pstmt.setInt(1, user.getId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getPassword());

            pstmt.executeUpdate();

            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findById(int id) {

        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(userQuery.findOne());
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            User user;
            if (rs.next()) {
                user = new User(
                        rs.getInt("id")
                        , rs.getString("name")
                        , rs.getString("password")
                );
            } else {
                throw new EmptyResultDataAccessException(1);
            }

            rs.close();
            pstmt.close();
            conn.close();

            return user;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAll() throws SQLException {
        Connection conn = dataSource.getConnection();

        PreparedStatement ps = conn.prepareStatement(userQuery.deleteAll());
        ps.executeUpdate();

        conn.close();
        ps.close();
    }

    public List<User> findAll() throws SQLException {
        Connection conn = dataSource.getConnection();

        PreparedStatement ps = conn.prepareStatement(userQuery.findAll());
        ResultSet rs = ps.executeQuery();

        List<User> userList = new ArrayList<>();
        while (rs.next()) {
            userList.add(
                    new User(rs.getInt("id")
                            , rs.getString("name")
                            , rs.getString("password"))
            );
        }

        return userList;
    }

    public int getCountAll() {
        int count = 0;
        Connection conn;
        try {
            conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(userQuery.getCountAll());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.getMessage();
        }
        return count;
    }


    public static void main(String[] args) throws SQLException {
        UserDao userDao = new UserDaoFactory().localUserDao();
        //userDao.add();
        //System.out.println(userDao.findAll());
        User user = userDao.findById(1);
        System.out.println(user.getName());
    }
}
