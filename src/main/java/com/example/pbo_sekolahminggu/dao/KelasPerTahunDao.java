package com.example.pbo_sekolahminggu.dao;

import com.example.pbo_sekolahminggu.beans.KelasPerTahun;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class KelasPerTahunDao {
    public static ArrayList<KelasPerTahun> getAll(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select * from tbl_kelasPerTahun where status_aktif = 1";
        ArrayList<KelasPerTahun> listkelasPerTahun = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                KelasPerTahun kelasPerTahun = new KelasPerTahun();
                kelasPerTahun.setID_KELAS_PER_TAHUN(rs.getInt("id"));
                kelasPerTahun.setRuangKelas(rs.getString("ruang_kelas"));
                kelasPerTahun.setKelasParalel(rs.getString("kelas_paralel"));
                listkelasPerTahun.add(kelasPerTahun);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listkelasPerTahun;
    }

    // SAVE
    public static void save(Connection con, KelasPerTahun kelasPerTahun) {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_kelasPerTahun(ruang_kelas, kelas_paralel) VALUES (?, ?)";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, kelasPerTahun.getRuangKelas());
            statement.setString(2, kelasPerTahun.getKelasParalel());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving kelasPerTahun: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // EDIT
    public static void edit(Connection con, KelasPerTahun kelasPerTahun) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_kelasPerTahun SET ruang_kelas = ?, kelas_paralel = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, kelasPerTahun.getRuangKelas());
            statement.setString(2, kelasPerTahun.getKelasParalel());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error editing kelasPerTahun: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // DELETE
    public static void delete(Connection con, KelasPerTahun kelasPerTahun) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_kelasPerTahun SET status_aktif = 0 WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, kelasPerTahun.getID_KELAS_PER_TAHUN());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting kelasPerTahun: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }
}
