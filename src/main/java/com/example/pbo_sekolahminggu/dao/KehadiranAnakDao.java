package com.example.pbo_sekolahminggu.dao;

import com.example.pbo_sekolahminggu.beans.KehadiranAnak;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class KehadiranAnakDao {

//    public static int getId_kelas_per_tahun(Connection con)
    public static ArrayList<KehadiranAnak> getAll(Connection con, KehadiranAnak ka) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT ka.id, a.nama, a.nis, CONCAT(k.nama_kelas, ' ', COALESCE(kpt.kelas_paralel || ' ', '')) AS kelas, kbk.jenis_kebaktian, kbk.tanggal, ka.presensi\n" +
                "FROM tbl_kehadiran_anak ka\n" +
                "JOIN tbl_histori_kelas_anak hka ON hka.id = ka.id_histori_kelas_anak\n" +
                "JOIN tbl_anak a ON a.id = hka.id_anak\n" +
                "JOIN tbl_kelas_per_tahun kpt ON hka.id_kelas_per_tahun = kpt.id\n" +
                "JOIN tbl_kelas k ON k.id = kpt.id_kelas\n" +
                "JOIN tbl_tahun_ajaran ta ON ta.id = kpt.id_tahun_ajaran\n" +
                "JOIN tbl_kebaktian kbk ON kbk.id = ka.id_kebaktian\n" +
                "WHERE hka.id_kelas_per_tahun = ? AND ka.id_kebaktian = ? AND ka.status_aktif = 1";
        ArrayList<KehadiranAnak> listkehadiranAnak = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
//            ps.setInt(1, ka.getId_kelas_per_tahun());
            ps.setInt(2, ka.getId_kebaktian());

            rs = ps.executeQuery();
            while (rs.next()) {
                KehadiranAnak kehadiranAnak = new KehadiranAnak();
                kehadiranAnak.setID_KEHADIRAN_ANAK(rs.getInt("id"));
                kehadiranAnak.setPresensi(rs.getBoolean("presensi"));
                listkehadiranAnak.add(kehadiranAnak);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listkehadiranAnak;
    }


    // SAVE
    public static void save(Connection con, KehadiranAnak kehadiranAnak) {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_kehadiranAnak (presensi) VALUES (?)";

        try {
            statement = con.prepareStatement(query);
            statement.setBoolean(1, kehadiranAnak.isPresensi());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving kehadiranAnak: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // EDIT
    public static void edit(Connection con, KehadiranAnak kehadiranAnak) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_kehadiranAnak SET presensi = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setBoolean(1, kehadiranAnak.isPresensi());
            statement.setInt(2, kehadiranAnak.getID_KEHADIRAN_ANAK());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error editing kehadiranAnak: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // DELETE
    public static void delete(Connection con, KehadiranAnak kehadiranAnak) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_kehadiranAnak SET status_aktif = 0 WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, kehadiranAnak.getID_KEHADIRAN_ANAK());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting kehadiranAnak: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }
}
