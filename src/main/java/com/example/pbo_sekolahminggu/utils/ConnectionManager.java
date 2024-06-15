package com.example.pbo_sekolahminggu.utils;

import java.sql.*;

public class ConnectionManager {
    public static Connection getConnection() throws SQLException {
        //localhost: server, 5432: port, prakPBO: namaDatabase, admin: password
        // url = jdbc (utk java):(pakai app apa)://(host name/address):(port number)/(database name)?user=(user name)&password=(password)
        String url = "jdbc:postgresql://localhost:5432/SekolahMinggu?user=postgres&password=admin";
        Connection con = DriverManager.getConnection(url);
        return con;

    }

    public static void closeConnection(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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