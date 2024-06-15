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
        ArrayList<Anak> listanak = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                Anak anak = new Anak();
                anak.setID_ANAK(rs.getInt("id"));
                anak.setNamaAnak(rs.getString("nama"));
                anak.setNIS(rs.getString("nis"));
                anak.setJenisKelamin(rs.getString("jenis_kelamin"));
                anak.setAlamat(rs.getString("alamat"));
                listanak.add(anak);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listanak;
    }

    // SAVE
    public static void save(Connection con, Anak anak) {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_anak (nama, nis, jenis_kelamin, alamat) VALUES (?, ?, ?, ?)";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, anak.getNamaAnak());
            statement.setString(2, anak.getNIS());
            statement.setString(3, anak.getJenisKelamin());
            statement.setString(4, anak.getAlamat());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving anak: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // EDIT
    public static void edit(Connection con, Anak anak) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_anak SET nama = ?, nis = ?, jenis_kelamin = ?, alamat = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, anak.getNamaAnak());
            statement.setString(2, anak.getNIS());
            statement.setString(3, anak.getJenisKelamin());
            statement.setString(4, anak.getAlamat());
            statement.setInt(5, anak.getID_ANAK());
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
