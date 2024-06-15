package com.example.pbo_sekolahminggu.dao;

import com.example.pbo_sekolahminggu.beans.HistoriMengajar;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class HistoriMengajarDao {
    // tampilin nama_guru dan kelasnya

    public static ArrayList<HistoriMengajar> getAll(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        // select id, nama_guru, kelas
        String query = "select * from tbl_historiMengajar where status_aktif = 1";
        ArrayList<HistoriMengajar> listhistoriMengajar = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                HistoriMengajar historiMengajar = new HistoriMengajar();
                historiMengajar.setID_HISTORI_MENGAJAR(rs.getInt("id"));
                // historiMengajar.set nama_guru
                // historiMengajar.set kelas

                listhistoriMengajar.add(historiMengajar);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listhistoriMengajar;
    }

    // SAVE
    public static void save(Connection con, HistoriMengajar historiMengajar) {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_historiMengajar (kelas, nama_guru) VALUES (?, ?, ?)";

        try {
            statement = con.prepareStatement(query);
//            statement.setString(2, historiMengajar. ); // kelas
//            statement.setString(2, historiMengajar. ); // nama_guru
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving historiMengajar: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }


    // EDIT
    public static void edit(Connection con, HistoriMengajar historiMengajar) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_historiMengajar SET kelas = ?, nama_guru = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
//            statement.setString(1, historiMengajar.); // get kelas
//            statement.setString(2, historiMengajar.); // get nama_guru
            statement.setInt(3, historiMengajar.getID_HISTORI_MENGAJAR());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error editing historiMengajar: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // DELETE
    public static void delete(Connection con, HistoriMengajar historiMengajar) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_historiMengajar SET status_aktif = 0 WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, historiMengajar.getID_HISTORI_MENGAJAR());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting historiMengajar: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }


}
