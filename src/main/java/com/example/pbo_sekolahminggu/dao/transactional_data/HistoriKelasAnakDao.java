package com.example.pbo_sekolahminggu.dao.transactional_data;

import com.example.pbo_sekolahminggu.beans.transactional_data.HistoriKelasAnak;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class HistoriKelasAnakDao {
    public static ArrayList<HistoriKelasAnak> getAll(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        // select id, kelas, jumlah murid
        String query = "select * from tbl_HistoriKelasAnak where status_aktif = 1";
        ArrayList<HistoriKelasAnak> listHistoriKelasAnak = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                HistoriKelasAnak historiKelasAnak = new HistoriKelasAnak();
                historiKelasAnak.setID_HISTORI_KELAS_ANAK(rs.getInt("id"));
                // historiKelasAnak.set kelas
                // historiKelasAnak.set jumlah_murid

                listHistoriKelasAnak.add(historiKelasAnak);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listHistoriKelasAnak;
    }

    // SAVE
    public static void save(Connection con, HistoriKelasAnak HistoriKelasAnak) {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_HistoriKelasAnak (kelas, jumlah_murid) VALUES (?, ?, ?)";

        try {
            statement = con.prepareStatement(query);
//            statement.setString(1, HistoriKelasAnak. ); // kelas
//            statement.setString(2, HistoriKelasAnak. ); // jumlah_murid
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving HistoriKelasAnak: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }


    // EDIT
    public static void edit(Connection con, HistoriKelasAnak historiKelasAnak) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_HistoriKelasAnak SET kelas = ?, jumlah_murid = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
//            statement.setString(1, historiKelasAnak.); // get kelas
//            statement.setString(2, historiKelasAnak.); // get jumlah_murid
            statement.setInt(3, historiKelasAnak.getID_HISTORI_KELAS_ANAK());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error editing HistoriKelasAnak: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // DELETE
    public static void delete(Connection con, HistoriKelasAnak historiKelasAnak) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_HistoriKelasAnak SET status_aktif = 0 WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, historiKelasAnak.getID_HISTORI_KELAS_ANAK());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting HistoriKelasAnak: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }



}
