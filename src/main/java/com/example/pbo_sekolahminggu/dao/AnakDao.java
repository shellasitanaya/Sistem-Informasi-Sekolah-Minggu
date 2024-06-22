package com.example.pbo_sekolahminggu.dao;


import com.example.pbo_sekolahminggu.beans.Anak;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AnakDao {
    public static ArrayList<Anak> getAll(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select * from tbl_anak where status_aktif = 1";
        ArrayList<Anak> listAnak = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                Anak anak = new Anak();
                anak.setID_ANAK(rs.getInt("id"));
                anak.setNama(rs.getString("nama"));
                anak.setNIS(rs.getString("nis"));
                anak.setJenisKelamin(rs.getString("jenis_kelamin"));
                listAnak.add(anak);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listAnak;
    }

    // SAVE
    public static void create(Connection con, Anak anak) {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_anak (nama, nis, jenis_kelamin) VALUES (?, ?, ?)";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, anak.getNama());
            statement.setString(2, anak.getNIS());
            statement.setString(3, anak.getJenisKelamin());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving anak: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // EDIT
    public static void update(Connection con, Anak anak) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_anak SET nama = ?, nis = ?, jenis_kelamin = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, anak.getNama());
            statement.setString(2, anak.getNIS());
            statement.setString(3, anak.getJenisKelamin());
            statement.setInt(4, anak.getID_ANAK());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error editing anak: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // DELETE
    public static void delete(Connection con, Anak anak) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_anak SET status_aktif = 0 WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, anak.getID_ANAK());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting anak: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

}
