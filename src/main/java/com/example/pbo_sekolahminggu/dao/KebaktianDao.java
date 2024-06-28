package com.example.pbo_sekolahminggu.dao;

import com.example.pbo_sekolahminggu.beans.Kebaktian;
import com.example.pbo_sekolahminggu.beans.TahunAjaran;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class KebaktianDao {
    public static ArrayList<Kebaktian> getAll(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select * from tbl_kebaktian where status_aktif = 1";
        ArrayList<Kebaktian> listkebaktian = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                Kebaktian kebaktian = new Kebaktian();
                kebaktian.setID_KEBAKTIAN(rs.getInt("id"));
                kebaktian.setJenisKebaktian(rs.getString("jenis_kebaktian"));
                kebaktian.setTanggal(rs.getDate("tanggal"));;
                listkebaktian.add(kebaktian);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listkebaktian;
    }


    public static ArrayList<Kebaktian> getFilteredKebaktian(Connection con, TahunAjaran th) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT * FROM tbl_kebaktian \n" +
                "WHERE \n" +
                "    ((EXTRACT(YEAR FROM tanggal) = SUBSTR(?, 0, 5)::integer AND EXTRACT(MONTH FROM tanggal) > 6)\n" +
                "    OR\n" +
                "    (EXTRACT(YEAR FROM tanggal) = SUBSTR(?, 6, 8)::integer AND EXTRACT(MONTH FROM tanggal) <= 6)) AND status_aktif=1\n";
        ArrayList<Kebaktian> listkebaktian = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            ps.setString(1, th.getTahunAjaran()); //extract tahun semester ganjil
            ps.setString(2, th.getTahunAjaran()); //extract tahun semester genap
            rs = ps.executeQuery();
            while (rs.next()) {
                Kebaktian kebaktian = new Kebaktian();
                kebaktian.setID_KEBAKTIAN(rs.getInt("id"));
                kebaktian.setJenisKebaktian(rs.getString("jenis_kebaktian"));
                kebaktian.setTanggal(rs.getDate("tanggal"));;
                listkebaktian.add(kebaktian);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listkebaktian;
    }



    // CREATE
    public static void create (Connection con, Kebaktian kebaktian) {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_kebaktian (jenis_kebaktian, tanggal) VALUES (?, ?)";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, kebaktian.getJenisKebaktian());
            statement.setDate(2, kebaktian.getTanggal());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving kebaktian: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // EDIT
    public static void edit(Connection con, Kebaktian kebaktian) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_kebaktian SET jenis_kebaktian = ?, tanggal = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, kebaktian.getJenisKebaktian());
            statement.setDate(2, kebaktian.getTanggal());
            statement.setInt(3, kebaktian.getID_KEBAKTIAN());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error editing kebaktian: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // DELETE
    public static void delete(Connection con, Kebaktian kebaktian) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_kebaktian SET status_aktif = 0 WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, kebaktian.getID_KEBAKTIAN());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting kebaktian: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }
}
