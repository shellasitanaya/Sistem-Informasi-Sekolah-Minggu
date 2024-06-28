package com.example.pbo_sekolahminggu.dao.master_data;

import com.example.pbo_sekolahminggu.beans.master_data.TahunAjaran;
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
        String query = "select * from tbl_tahun_ajaran where status_aktif = 1 ORDER BY tahun_ajaran DESC";
        ArrayList<TahunAjaran> listtahunAjaran = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                TahunAjaran tahunAjaran = new TahunAjaran();
                tahunAjaran.setID_TAHUN_AJARAN(rs.getInt("id"));
                tahunAjaran.setTahunAjaran(rs.getString("tahun_ajaran"));
                listtahunAjaran.add(tahunAjaran);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listtahunAjaran;
    }

    // CREATE
    public static void create(Connection con, TahunAjaran tahunAjaran) {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_tahun_ajaran(tahun_ajaran) VALUES (?)";

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

    // UPDATE
    public static void update(Connection con, TahunAjaran tahunAjaran) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_tahun_ajaran SET tahun_ajaran = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, tahunAjaran.getTahunAjaran());
            statement.setInt(2, tahunAjaran.getID_TAHUN_AJARAN());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error editing tahun ajaran: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // DELETE
    public static void delete(Connection con, TahunAjaran tahunAjaran) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_tahun_ajaran SET status_aktif = 0 WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, tahunAjaran.getID_TAHUN_AJARAN());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting tahun ajaran: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

}
