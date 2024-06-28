package com.example.pbo_sekolahminggu.utils;

import java.sql.*;

public class ConnectionManager {
    public static Connection getConnection() throws SQLException {
        //localhost: server, 5432: port, prakPBO: namaDatabase, admin: password
        // url = jdbc (utk java):(pakai app apa)://(host name/address):(port number)/(database name)?user=(user name)&password=(password)
        String url = "jdbc:postgresql://localhost:5432/SekolahMinggu?user=postgres&password=admin253";
        Connection con = DriverManager.getConnection(url);
        return con;

    }

    public static void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // cuma beda posisi parameter
    public static void close(ResultSet resultSet, PreparedStatement preparedStatement) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void close(PreparedStatement ps) {
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}