package com.example.pbo_sekolahminggu.dao;

import com.example.pbo_sekolahminggu.beans.Guru;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;

public class GuruDao {
    public static ArrayList<Guru> getAll(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select * from tbl_guru where status_aktif = 1";
        ArrayList<Guru> listGuru = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                Guru guru = new Guru();
                guru.setID_GURU(rs.getInt("id"));
                guru.setNamaGuru(rs.getString("nama"));
                guru.setNIP(rs.getString("nip"));
                guru.setNoTelp(rs.getString("no_telp"));
                guru.setAlamat(rs.getString("alamat"));
                listGuru.add(guru);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listGuru;
    }

    // CREATE
    public static void create (Connection con, Guru guru) {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_guru (nama, nip, no_telp, alamat) VALUES (?, ?, ?, ?)";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, guru.getNamaGuru());
            statement.setString(2, guru.getNIP());
            statement.setString(3, guru.getNoTelp());
            statement.setString(4, guru.getAlamat());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving guru: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // UPDATE
    public static void update(Connection con, Guru guru) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_guru SET nama = ?, nip = ?, no_telp = ?, alamat = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, guru.getNamaGuru());
            statement.setString(2, guru.getNIP());
            statement.setString(3, guru.getNoTelp());
            statement.setString(4, guru.getAlamat());
            statement.setInt(5, guru.getID_GURU());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error editing guru: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // DELETE
    public static void delete(Connection con, Guru guru) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_guru SET status_aktif = 0 WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, guru.getID_GURU());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting guru: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

}
