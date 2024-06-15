package com.example.pbo_sekolahminggu.dao;

import com.example.pbo_sekolahminggu.beans.KehadiranGuru;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class KehadiranGuruDao {
    public static ArrayList<KehadiranGuru> getAll(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select * from tbl_kehadiranGuru where status_aktif = 1";
        ArrayList<KehadiranGuru> listkehadiranGuru = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                KehadiranGuru kehadiranGuru = new KehadiranGuru();
                kehadiranGuru.setID_KEHADIRAN_GURU(rs.getInt("id"));
                kehadiranGuru.setPresensi(rs.getBoolean("presensi"));
                listkehadiranGuru.add(kehadiranGuru);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listkehadiranGuru;
    }


    // SAVE
    public static void save(Connection con, KehadiranGuru kehadiranGuru) {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_kehadiranGuru (presensi) VALUES (?)";

        try {
            statement = con.prepareStatement(query);
            statement.setBoolean(1, kehadiranGuru.isPresensi());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving kehadiranGuru: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // EDIT
    public static void edit(Connection con, KehadiranGuru kehadiranGuru) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_kehadiranGuru SET presensi = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setBoolean(1, kehadiranGuru.isPresensi());
            statement.setInt(2, kehadiranGuru.getID_KEHADIRAN_GURU());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error editing kehadiranGuru: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // DELETE
    public static void delete(Connection con, KehadiranGuru kehadiranGuru) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_kehadiranGuru SET status_aktif = 0 WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, kehadiranGuru.getID_KEHADIRAN_GURU());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting kehadiranGuru: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }
}
