package com.example.pbo_sekolahminggu.dao;

import com.example.pbo_sekolahminggu.beans.Anak;
import com.example.pbo_sekolahminggu.beans.Kebaktian;
import com.example.pbo_sekolahminggu.beans.KehadiranAnak;
import com.example.pbo_sekolahminggu.beans.KelasPerTahun;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class KehadiranAnakDao {
    private static KelasPerTahun selectedKelas;
    private static Kebaktian selectedKebaktian;

    public static KelasPerTahun getSelectedKelas() {
        return selectedKelas;
    }

    public static void setSelectedKelas(KelasPerTahun selectedKelas) {
        KehadiranAnakDao.selectedKelas = selectedKelas;
    }

    public static Kebaktian getSelectedKebaktian() {
        return selectedKebaktian;
    }

    public static void setSelectedKebaktian(Kebaktian selectedKebaktian) {
        KehadiranAnakDao.selectedKebaktian = selectedKebaktian;
    }

    //    public static int getId_kelas_per_tahun(Connection con)
    //ini function untuk di main menu kehadiran anak, bukan di assign
    public static ArrayList<KehadiranAnak> getAll(Connection con, KelasPerTahun kelas, Kebaktian kbk) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT ka.id, a.nama, a.nis, CONCAT(k.nama_kelas, ' ', COALESCE(kpt.kelas_paralel || ' ', ''))AS kelas, ta.tahun_ajaran, kbk.jenis_kebaktian, kbk.tanggal, ka.presensi\n" +
                "                FROM tbl_kehadiran_anak ka\n" +
                "                JOIN tbl_histori_kelas_anak hka ON hka.id = ka.id_histori_kelas_anak\n" +
                "                JOIN tbl_anak a ON a.id = hka.id_anak\n" +
                "                JOIN tbl_kelas_per_tahun kpt ON hka.id_kelas_per_tahun = kpt.id\n" +
                "                JOIN tbl_kelas k ON k.id = kpt.id_kelas\n" +
                "                JOIN tbl_tahun_ajaran ta ON ta.id = kpt.id_tahun_ajaran\n" +
                "                JOIN tbl_kebaktian kbk ON kbk.id = ka.id_kebaktian\n" +
                "                WHERE hka.id_kelas_per_tahun = ? AND ka.id_kebaktian = ? AND ka.status_aktif = 1";
        ArrayList<KehadiranAnak> listkehadiranAnak = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, kelas.getID_KELAS_PER_TAHUN());
            ps.setInt(2, kbk.getID_KEBAKTIAN());

            rs = ps.executeQuery();
            while (rs.next()) {
                KehadiranAnak kehadiranAnak = new KehadiranAnak();
                kehadiranAnak.setID_KEHADIRAN_ANAK(rs.getInt("id"));
                kehadiranAnak.setNama_anak(rs.getString("nama"));
                kehadiranAnak.setNIS(rs.getString("nis"));
                kehadiranAnak.setKelas(rs.getString("kelas"));
                kehadiranAnak.setTahun_ajaran(rs.getString("tahun_ajaran"));
                kehadiranAnak.setKebaktian(rs.getString("jenis_kebaktian"));
                kehadiranAnak.setTgl_kebaktian(rs.getDate("tanggal"));
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

    // INI UNTUK DI WINDOW ASSIGN, ADA 2 QUERY (HADIR DAN TDK HADIR)
    public static ArrayList<Anak> getAllAnakHadir(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT a.*\n" +
                "FROM tbl_kehadiran_anak ka\n" +
                "JOIN tbl_histori_kelas_anak hka on hka.id = ka.id_histori_kelas_anak\n" +
                "JOIN tbl_anak a on a.id = hka.id_anak\n" +
                "WHERE ka.presensi = true AND a.status_aktif = 1 AND ka.status_aktif = 1 AND hka.status_aktif = 1\n" +
                "AND hka.id_kelas_per_tahun = ? AND ka.id_kebaktian = ?";
        ArrayList<Anak> listAnak = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, selectedKelas.getID_KELAS_PER_TAHUN());
            ps.setInt(2, selectedKebaktian.getID_KEBAKTIAN());

            rs = ps.executeQuery();
            while (rs.next()) {
                Anak anak = new Anak();
                anak.setID_ANAK(rs.getInt("id"));
                anak.setNama(rs.getString("nama"));
                anak.setNIS(rs.getString("nis"));
                listAnak.add(anak);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listAnak;
    }

    public static ArrayList<Anak> getAllAnakTidakHadir(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT a.*\n" +
                "FROM tbl_kehadiran_anak ka\n" +
                "JOIN tbl_histori_kelas_anak hka on hka.id = ka.id_histori_kelas_anak\n" +
                "JOIN tbl_anak a on a.id = hka.id_anak\n" +
                "WHERE ka.presensi = false AND a.status_aktif = 1 AND ka.status_aktif = 1 AND hka.status_aktif = 1\n" +
                "AND hka.id_kelas_per_tahun = ? AND ka.id_kebaktian = ?";
        ArrayList<Anak> listAnak = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, selectedKelas.getID_KELAS_PER_TAHUN());
            ps.setInt(2, selectedKebaktian.getID_KEBAKTIAN());

            rs = ps.executeQuery();
            while (rs.next()) {
                Anak anak = new Anak();
                anak.setID_ANAK(rs.getInt("id"));
                anak.setNama(rs.getString("nama"));
                anak.setNIS(rs.getString("nis"));
                listAnak.add(anak);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listAnak;
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

    // EDIT or UPDATE
    public static void updateHadir(Connection con, Anak anak) {
        String query = "UPDATE tbl_kehadiran_anak\n" +
                "SET presensi = true\n" +
                "WHERE id_histori_kelas_anak = (SELECT id\n" +
                "\t\t   FROM tbl_histori_kelas_anak\n" +
                "\t\t   WHERE id_anak = ? AND id_kelas_per_tahun = ?)";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, anak.getID_ANAK());
            ps.setInt(2, selectedKelas.getID_KELAS_PER_TAHUN());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps);
        }
    }

    public static void updateTidakHadir(Connection con, Anak anak) {
        String query = "UPDATE tbl_kehadiran_anak\n" +
                "SET presensi = false\n" +
                "WHERE id_histori_kelas_anak = (SELECT id\n" +
                "\t\t   FROM tbl_histori_kelas_anak\n" +
                "\t\t   WHERE id_anak = ? AND id_kelas_per_tahun = ?)";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, anak.getID_ANAK());
            ps.setInt(2, selectedKelas.getID_KELAS_PER_TAHUN());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps);
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

    // Extra query: populate the tbl_kehadiran_anak
    public static void populateTblKehadiranAnak(Connection con) {
        PreparedStatement ps = null;
        String query = "INSERT INTO tbl_kehadiran_anak (id_histori_kelas_anak, id_kebaktian)\n" +
                "                SELECT hka.id, ? \n" +
                "                FROM tbl_histori_kelas_anak hka\n" +
                "                WHERE hka.id_kelas_per_tahun = ?";
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, selectedKebaktian.getID_KEBAKTIAN());
            ps.setInt(2, selectedKelas.getID_KELAS_PER_TAHUN());
            ps.executeUpdate(); // Use executeUpdate instead of executeQuery for INSERT statement
        } catch (SQLException e ){
            throw new RuntimeException(e);
        } finally {
            if (ps != null) {
                ConnectionManager.close(ps);
            }
        }
    }


}
