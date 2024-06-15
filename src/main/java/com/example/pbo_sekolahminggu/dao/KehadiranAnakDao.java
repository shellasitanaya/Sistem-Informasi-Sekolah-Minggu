package com.example.pbo_sekolahminggu.dao;

import com.example.pbo_sekolahminggu.beans.KehadiranAnak;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class KehadiranAnakDao {
    public static ArrayList<KehadiranAnak> getAll(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select * from tbl_kehadiranAnak where status_aktif = 1";
        ArrayList<KehadiranAnak> listkehadiranAnak = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                KehadiranAnak kehadiranAnak = new KehadiranAnak();
                kehadiranAnak.setID_KEHADIRAN_ANAK(rs.getInt("id"));
                kehadiranAnak.setPresensi(rs.getBoolean("presensi"));
                listkehadiranAnak.add(kehadiranAnak);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listkehadiranAnak;
    }


    // SAVE
    public static void save(Connection con, KehadiranAnak kehadiranAnak) {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_kehadiranAnak (presensi) VALUES (?)";

        try {
            statement = con.prepareStatement(query);
            statement.setBoolean(1, kehadiranAnak.isPresensi());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving kehadiranAnak: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // EDIT
    public static void edit(Connection con, KehadiranAnak kehadiranAnak) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_kehadiranAnak SET presensi = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setBoolean(1, kehadiranAnak.isPresensi());
            statement.setInt(2, kehadiranAnak.getID_KEHADIRAN_ANAK());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error editing kehadiranAnak: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // DELETE
    public static void delete(Connection con, KehadiranAnak kehadiranAnak) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_kehadiranAnak SET status_aktif = 0 WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, kehadiranAnak.getID_KEHADIRAN_ANAK());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting kehadiranAnak: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }
}
