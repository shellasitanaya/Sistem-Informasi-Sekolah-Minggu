package com.example.pbo_sekolahminggu.dao.transactional_data;

import com.example.pbo_sekolahminggu.beans.transactional_data.HistoriMengajar;
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

        String query = "SELECT \n" +
                "thm.id,\n" +
                "(SELECT nama FROM tbl_guru g WHERE g.id = thm.id_guru),\n" +
                "(SELECT nip FROM tbl_guru g WHERE g.id = thm.id_guru),\n" +
                "(SELECT nama_kelas FROM tbl_kelas k WHERE k.id = (SELECT id_kelas FROM tbl_kelas_per_tahun k WHERE thm.id_kelas_per_tahun = k.id)) || ' ' || COALESCE((SELECT kelas_paralel FROM tbl_kelas_per_tahun k WHERE thm.id_kelas_per_tahun = k.id), '') AS kelas,--kelas\n" +
                "(SELECT tahun_ajaran FROM tbl_tahun_ajaran ta WHERE ta.id = (SELECT id_tahun_ajaran FROM tbl_kelas_per_tahun k WHERE thm.id_kelas_per_tahun = k.id)),--tahun ajaran\n" +
                "--foreign keys\n" +
                "id_guru,\n" +
                "id_kelas_per_tahun\n" +
                "FROM tbl_histori_mengajar thm\n" +
                "WHERE thm.status_aktif = 1";
        ArrayList<HistoriMengajar> listhistoriMengajar = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                HistoriMengajar historiMengajar = new HistoriMengajar();
                historiMengajar.setID_HISTORI_MENGAJAR(rs.getInt("id"));
                historiMengajar.setNamaGuru(rs.getString("nama"));
                historiMengajar.setNip(rs.getString("nip"));
                historiMengajar.setKelas(rs.getString("kelas"));
                historiMengajar.setTahunAjaran(rs.getString("tahun_ajaran"));
                historiMengajar.setID_GURU(rs.getInt("id_guru"));
                historiMengajar.setID_KELAS_PER_TAHUN(rs.getInt("id_kelas_per_tahun"));
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
        String query = "INSERT INTO tbl_histori_mengajar (id_kelas_per_tahun, id_guru) VALUES (?, ?)";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, historiMengajar.getID_KELAS_PER_TAHUN() ); // kelas
            statement.setInt(2, historiMengajar.getID_GURU()); // guru
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
        String query = "UPDATE tbl_histori_mengajar SET id_kelas_per_tahun = ?, id_guru = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, historiMengajar.getID_KELAS_PER_TAHUN());
            statement.setInt(2, historiMengajar.getID_GURU());
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
        String query = "UPDATE tbl_histori_mengajar SET status_aktif = 0 WHERE id = ?";

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
