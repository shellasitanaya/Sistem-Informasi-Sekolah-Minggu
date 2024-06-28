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

        String query = "SELECT\n" +
                "    thm.id,\n" +
                "    g.nama,\n" +
                "    g.nip,\n" +
                "\tk.nama_kelas  ||\n" +
                "\t\tCASE\n" +
                "\t\t\tWHEN kpt.kelas_paralel IS NOT NULL THEN ' ' || kpt.kelas_paralel\n" +
                "\t\t\tELSE ''\n" +
                "\t\tEND AS kelas,\n" +
                "    kpt.id_tahun_ajaran,\n" +
                "    ta.tahun_ajaran,\n" +
                "    thm.id_guru,\n" +
                "    thm.id_kelas_per_tahun\n" +
                "FROM tbl_histori_mengajar thm\n" +
                "JOIN tbl_guru g ON thm.id_guru = g.id\n" +
                "JOIN tbl_kelas_per_tahun kpt ON thm.id_kelas_per_tahun = kpt.id\n" +
                "JOIN tbl_kelas k ON kpt.id_kelas = k.id\n" +
                "LEFT JOIN tbl_tahun_ajaran ta ON kpt.id_tahun_ajaran = ta.id\n" +
                "WHERE thm.status_aktif = 1;\n";
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
                historiMengajar.setID_TAHUN_AJARAN(rs.getInt("id_tahun_ajaran"));
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


    public static ArrayList<HistoriMengajar> get(Connection con, int tahunAjaranId, int kelasPerTahunId) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT\n" +
                "    thm.id,\n" +
                "    g.nama,\n" +
                "    g.nip,\n" +
                "\tk.nama_kelas  ||\n" +
                "\t\tCASE\n" +
                "\t\t\tWHEN kpt.kelas_paralel IS NOT NULL THEN ' ' || kpt.kelas_paralel\n" +
                "\t\t\tELSE ''\n" +
                "\t\tEND AS kelas,\n" +
                "    kpt.id_tahun_ajaran,\n" +
                "    ta.tahun_ajaran,\n" +
                "    thm.id_guru,\n" +
                "    thm.id_kelas_per_tahun\n" +
                "FROM tbl_histori_mengajar thm\n" +
                "LEFT JOIN tbl_guru g ON thm.id_guru = g.id\n" +
                "LEFT JOIN tbl_kelas_per_tahun kpt ON thm.id_kelas_per_tahun = kpt.id\n" +
                "LEFT JOIN tbl_kelas k ON kpt.id_kelas = k.id\n" +
                "LEFT JOIN tbl_tahun_ajaran ta ON kpt.id_tahun_ajaran = ta.id\n" +
                "WHERE thm.status_aktif = 1 AND ta.id=? AND kpt.id=?";
        ArrayList<HistoriMengajar> listhistoriMengajar = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, tahunAjaranId);
            ps.setInt(2, kelasPerTahunId);
            rs = ps.executeQuery();
            while (rs.next()) {
                HistoriMengajar historiMengajar = new HistoriMengajar();
                historiMengajar.setID_HISTORI_MENGAJAR(rs.getInt("id"));
                historiMengajar.setNamaGuru(rs.getString("nama"));
                historiMengajar.setNip(rs.getString("nip"));
                historiMengajar.setKelas(rs.getString("kelas"));
                historiMengajar.setID_TAHUN_AJARAN(rs.getInt("id_tahun_ajaran"));
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
