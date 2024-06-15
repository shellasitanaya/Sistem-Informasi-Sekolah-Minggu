package com.example.pbo_sekolahminggu.dao;

import com.example.pbo_sekolahminggu.beans.TahunAjaran;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TahunAjaranDao {
    public static ArrayList<TahunAjaran> getAll(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select * from tbl_tahunAjaran where status_aktif = 1";
        ArrayList<TahunAjaran> listtahunAjaran = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                TahunAjaran tahunAjaran = new TahunAjaran();
                tahunAjaran.setID_TAHUN_AJARAN(rs.getInt("id"));
                tahunAjaran.setTahunAjaran(rs.getString("nama"));
                listtahunAjaran.add(tahunAjaran);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listtahunAjaran;
    }

    // SAVE
    public static void save(Connection con, TahunAjaran tahunAjaran) {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_tahunAjaran(tahun_ajaran) VALUES (?)";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, tahunAjaran.getTahunAjaran());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving tahunAjaran: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // EDIT
    public static void edit(Connection con, TahunAjaran tahunAjaran) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_tahunAjaran SET tahun_ajaran = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, tahunAjaran.getTahunAjaran());
            statement.setInt(2, tahunAjaran.getID_TAHUN_AJARAN());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error editing tahunAjaran: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // DELETE
    public static void delete(Connection con, TahunAjaran tahunAjaran) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_tahunAjaran SET status_aktif = 0 WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, tahunAjaran.getID_TAHUN_AJARAN());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting tahunAjaran: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

}
