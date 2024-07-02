package com.example.pbo_sekolahminggu.dao.master.data;

import com.example.pbo_sekolahminggu.beans.master.data.Guru;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class GuruDao {
    public static ArrayList<Guru> getAll(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select * \n" +
                "from tbl_guru \n" +
                "where status_aktif = 1\n" +
                "ORDER BY id";
        ArrayList<Guru> listGuru = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                Guru guru = new Guru();
                guru.setIdGuru(rs.getInt("id"));
                guru.setNamaGuru(rs.getString("nama"));
                guru.setNip(rs.getString("nip"));
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
    public static void create (Connection con, Guru guru) throws SQLException {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_guru (nama, nip, no_telp, alamat) VALUES (?, ?, ?, ?)";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, guru.getNamaGuru());
            statement.setString(2, guru.getNip());
            statement.setString(3, guru.getNoTelp());
            statement.setString(4, guru.getAlamat());
            statement.executeUpdate();
        } finally {
            ConnectionManager.close(statement);
        }
    }
    public static Map<String, Object[]> getAllArrayObject(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select id, nama from tbl_guru where status_aktif = 1 order by id asc";
        Map<String, Object[]> listBrand = new TreeMap<String, Object[]>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            int i = 1;
            while(rs.next()) {
                Object[] object = new Object[2];
                object[0] = rs.getInt("id");
                object[1] = rs.getString("nama");
                listBrand.put(String.valueOf(i), object);
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(rs, ps);
        }
        return listBrand;
    }
    // UPDATE
    public static void update(Connection con, Guru guru) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_guru SET nama = ?, nip = ?, no_telp = ?, alamat = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, guru.getNamaGuru());
            statement.setString(2, guru.getNip());
            statement.setString(3, guru.getNoTelp());
            statement.setString(4, guru.getAlamat());
            statement.setInt(5, guru.getIdGuru());
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
            statement.setInt(1, guru.getIdGuru());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting guru: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // CLEAR -> cuma bersihin tampilan tabel
//    public void clear(Connection con) throws SQLException {
//        PreparedStatement statement = null;
//        String query = "TRUNCATE TABLE tbl_guru";
//
//        try {
//            statement = con.prepareStatement(query);
//            statement.executeUpdate();
//        } catch (SQLException e) {
//            throw new SQLException("Error clearing table: " + e.getMessage());
//        } finally {
//            ConnectionManager.close(statement);
//        }
//    }
}
