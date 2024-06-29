package com.example.pbo_sekolahminggu.dao.transactional.data;

import com.example.pbo_sekolahminggu.beans.master.data.Guru;
import com.example.pbo_sekolahminggu.beans.master.data.Kebaktian;
import com.example.pbo_sekolahminggu.beans.transactional.data.KehadiranGuru;
import com.example.pbo_sekolahminggu.beans.transactional.data.KelasPerTahun;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class KehadiranGuruDao {


    // EXPORT
    public static Map<String, Object[]> getAllArrayObject(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT " +
                "    g.nama AS nama_guru, " +
                "    ta.tahun_ajaran, " +
                "    COUNT(*) AS total_kehadiran " +
                "FROM " +
                "    tbl_kehadiran_guru kg " +
                "JOIN " +
                "    tbl_histori_mengajar h ON kg.id_histori_mengajar = h.id " +
                "JOIN " +
                "    tbl_kebaktian k ON kg.id_kebaktian = k.id " +
                "JOIN " +
                "    tbl_guru g ON h.id_guru = g.id " +
                "JOIN " +
                "    tbl_kelas_per_tahun kpt ON h.id_kelas_per_tahun = kpt.id " +
                "JOIN " +
                "    tbl_tahun_ajaran ta ON kpt.id_tahun_ajaran = ta.id " +
                "WHERE " +
                "    kg.presensi = false " +
                "    AND kpt.id_tahun_ajaran = ? " +
                "GROUP BY " +
                "    g.nama, ta.tahun_ajaran " +
                "ORDER BY " +
                "    ta.tahun_ajaran, g.nama";

        Map<String, Object[]> listData = new TreeMap<>();

        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            int i = 1;
            while(rs.next()) {
                Object[] object = new Object[3];
                object[0] = rs.getString("nama_guru");
                object[1] = rs.getString("tahun_ajaran");
                object[2] = rs.getInt("total_kehadiran");
                listData.put(String.valueOf(i), object);
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(rs, ps);
        }

        return listData;
    }
    private static KelasPerTahun selectedKelas;
    private static Kebaktian selectedKebaktian;
    public static ArrayList<KehadiranGuru> getAll(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT\n" +
                "kg.id,\n" +
                "g.nama,\n" +
                "g.nip,\n" +
                "k.nama_kelas ||\n" +
                "CASE\n" +
                "WHEN kpt.kelas_paralel IS NOT NULL THEN ' ' || kpt.kelas_paralel\n" +
                "ELSE ''\n" +
                "END AS kelas,\n" +
                "keb.jenis_kebaktian,\n" +
                "keb.tanggal,\n" +
                "kg.presensi,\n" +
                "kg.id_histori_mengajar,\n" +
                "kg.id_kebaktian,\n" +
                "kpt.id AS id_kelas_per_tahun,\n" +
                "kpt.id_tahun_ajaran\n" +
                "FROM tbl_kehadiran_guru kg\n" +
                "LEFT JOIN tbl_histori_mengajar hm ON hm.id = kg.id_histori_mengajar\n" +
                "LEFT JOIN tbl_guru g ON g.id = hm.id_guru\n" +
                "JOIN tbl_kelas_per_tahun kpt ON kpt.id = hm.id_kelas_per_tahun\n" +
                "JOIN tbl_kelas k ON k.id = kpt.id_kelas\n" +
                "LEFT JOIN tbl_kebaktian keb ON keb.id = kg.id_kebaktian\n" +
                "WHERE kg.status_aktif = 1 \nORDER BY kg.id";
        ArrayList<KehadiranGuru> listkehadiranGuru = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                KehadiranGuru kehadiranGuru = new KehadiranGuru();
                kehadiranGuru.setID_KEHADIRAN_GURU(rs.getInt("id"));
                kehadiranGuru.setPresensi(rs.getBoolean("presensi"));
                kehadiranGuru.setNama(rs.getString("nama"));
                kehadiranGuru.setNip(rs.getString("nip"));
                kehadiranGuru.setJenisKebaktian(rs.getString("jenis_kebaktian"));
                kehadiranGuru.setTanggal(rs.getDate("tanggal"));
                kehadiranGuru.setID_KEBAKTIAN(rs.getInt("id_kebaktian"));
                kehadiranGuru.setID_HISTORI_MENGAJAR(rs.getInt("id_histori_mengajar"));
                kehadiranGuru.setKelas(rs.getString("kelas"));
                kehadiranGuru.setID_KELAS_PER_TAHUN(rs.getInt("id_kelas_per_tahun"));
                kehadiranGuru.setID_TAHUN_AJARAN(rs.getInt("id_tahun_ajaran"));
                listkehadiranGuru.add(kehadiranGuru);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listkehadiranGuru;
    }

    public static ArrayList<KehadiranGuru> get(Connection con, int idKebaktian) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT\n" +
                "kg.id,\n" +
                "g.nama,\n" +
                "g.nip,\n" +
                "k.nama_kelas ||\n" +
                "CASE\n" +
                "WHEN kpt.kelas_paralel IS NOT NULL THEN ' ' || kpt.kelas_paralel\n" +
                "ELSE ''\n" +
                "END AS kelas,\n" +
                "keb.jenis_kebaktian,\n" +
                "keb.tanggal,\n" +
                "kg.presensi,\n" +
                "kg.id_histori_mengajar,\n" +
                "kg.id_kebaktian,\n" +
                "kpt.id AS id_kelas_per_tahun,\n" +
                "kpt.id_tahun_ajaran\n" +
                "FROM tbl_kehadiran_guru kg\n" +
                "LEFT JOIN tbl_histori_mengajar hm ON hm.id = kg.id_histori_mengajar\n" +
                "LEFT JOIN tbl_guru g ON g.id = hm.id_guru\n" +
                "JOIN tbl_kelas_per_tahun kpt ON kpt.id = hm.id_kelas_per_tahun\n" +
                "JOIN tbl_kelas k ON k.id = kpt.id_kelas\n" +
                "LEFT JOIN tbl_kebaktian keb ON keb.id = kg.id_kebaktian\n" +
                "WHERE kg.status_aktif = 1 AND kg.id_kebaktian=?\nORDER BY kg.id";
        ArrayList<KehadiranGuru> listkehadiranGuru = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, idKebaktian);
            rs = ps.executeQuery();
            while (rs.next()) {
                KehadiranGuru kehadiranGuru = new KehadiranGuru();
                kehadiranGuru.setID_KEHADIRAN_GURU(rs.getInt("id"));
                kehadiranGuru.setPresensi(rs.getBoolean("presensi"));
                kehadiranGuru.setNama(rs.getString("nama"));
                kehadiranGuru.setNip(rs.getString("nip"));
                kehadiranGuru.setJenisKebaktian(rs.getString("jenis_kebaktian"));
                kehadiranGuru.setTanggal(rs.getDate("tanggal"));
                kehadiranGuru.setID_KEBAKTIAN(rs.getInt("id_kebaktian"));
                kehadiranGuru.setID_HISTORI_MENGAJAR(rs.getInt("id_histori_mengajar"));
                kehadiranGuru.setKelas(rs.getString("kelas"));
                kehadiranGuru.setID_KELAS_PER_TAHUN(rs.getInt("id_kelas_per_tahun"));
                kehadiranGuru.setID_TAHUN_AJARAN(rs.getInt("id_tahun_ajaran"));
                listkehadiranGuru.add(kehadiranGuru);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listkehadiranGuru;
    }

    public static ArrayList<KehadiranGuru> getSpecial(Connection con, KelasPerTahun kelas, Kebaktian kbk) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT\n" +
                "    ka.id,\n" +
                "    a.nama,\n" +
                "    a.nip,\n" +
                "    CONCAT(k.nama_kelas, ' ', COALESCE(kpt.kelas_paralel, '')) AS kelas,\n" +
                "    ta.tahun_ajaran,\n" +
                "    kbk.jenis_kebaktian,\n" +
                "    kbk.tanggal,\n" +
                "    ka.presensi\n" +
                "FROM\n" +
                "    tbl_kehadiran_guru ka\n" +
                "    JOIN tbl_histori_mengajar hka ON hka.id = ka.id_histori_mengajar\n" +
                "    JOIN tbl_guru a ON a.id = hka.id_guru\n" +
                "    JOIN tbl_kelas_per_tahun kpt ON kpt.id = hka.id_kelas_per_tahun\n" +
                "    JOIN tbl_kelas k ON k.id = kpt.id_kelas\n" +
                "    JOIN tbl_tahun_ajaran ta ON ta.id = kpt.id_tahun_ajaran\n" +
                "    JOIN tbl_kebaktian kbk ON kbk.id = ka.id_kebaktian\n" +
                "WHERE\n" +
                "    hka.id_kelas_per_tahun = ?\n" +
                "    AND ka.id_kebaktian = ?\n" +
                "    AND ka.status_aktif = 1;\n";
        ArrayList<KehadiranGuru> listkehadiranGuru = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, kelas.getID_KELAS_PER_TAHUN());
            ps.setInt(2, kbk.getID_KEBAKTIAN());

            rs = ps.executeQuery();
            while (rs.next()) {
                KehadiranGuru kehadiranGuru = new KehadiranGuru();
                kehadiranGuru.setID_KEHADIRAN_GURU(rs.getInt("id"));
                kehadiranGuru.setNama(rs.getString("nama"));
                kehadiranGuru.setNip(rs.getString("nip"));
                kehadiranGuru.setKelas(rs.getString("kelas"));
                kehadiranGuru.setTahun_ajaran(rs.getString("tahun_ajaran"));
                kehadiranGuru.setKebaktian(rs.getString("jenis_kebaktian"));
                kehadiranGuru.setTgl_kebaktian(rs.getDate("tanggal"));
                kehadiranGuru.setPresensi(rs.getBoolean("presensi"));
                listkehadiranGuru.add(kehadiranGuru);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listkehadiranGuru;
    }

    // SAVE
    public static void save(Connection con, KehadiranGuru kehadiranGuru) {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_kehadiran_guru (id_histori_mengajar, id_kebaktian, presensi) VALUES (?, ?, ?)";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, kehadiranGuru.getID_HISTORI_MENGAJAR());
            statement.setInt(2, kehadiranGuru.getID_KEBAKTIAN());
            statement.setBoolean(3, kehadiranGuru.isPresensi());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving kehadiranGuru: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // EDIT
    public static void edit(Connection con, KehadiranGuru kehadiranGuru) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_kehadiran_guru SET id_histori_mengajar=?, id_kebaktian=?, presensi = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, kehadiranGuru.getID_HISTORI_MENGAJAR());
            statement.setInt(2, kehadiranGuru.getID_KEBAKTIAN());
            statement.setBoolean(3, kehadiranGuru.isPresensi());
            statement.setInt(4, kehadiranGuru.getID_KEHADIRAN_GURU());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error editing kehadiranGuru: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // DELETE
    public static void delete(Connection con, KehadiranGuru kehadiranGuru) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_kehadiran_guru SET status_aktif = 0 WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, kehadiranGuru.getID_KEHADIRAN_GURU());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting kehadiranGuru: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }


    public static void populateTblKehadiranGuru(Connection con) {
        PreparedStatement ps = null;
        String query = "INSERT INTO tbl_kehadiran_guru (id_histori_mengajar, id_kebaktian)\n" +
                "                SELECT hka.id, ? \n" +
                "                FROM tbl_histori_mengajar hka\n" +
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

    public static ArrayList<Guru> getAllGuruHadir(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT a.*\n" +
                "FROM tbl_kehadiran_guru ka\n" +
                "JOIN tbl_histori_mengajar hka on hka.id = ka.id_histori_mengajar\n" +
                "JOIN tbl_guru a on a.id = hka.id_guru\n" +
                "WHERE ka.presensi = true AND a.status_aktif = 1 AND ka.status_aktif = 1 AND hka.status_aktif = 1\n" +
                "AND hka.id_kelas_per_tahun = ? AND ka.id_kebaktian = ?";
        ArrayList<Guru> listAnak = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, selectedKelas.getID_KELAS_PER_TAHUN());
            ps.setInt(2, selectedKebaktian.getID_KEBAKTIAN());

            rs = ps.executeQuery();
            while (rs.next()) {
                Guru anak = new Guru();
                anak.setID_GURU(rs.getInt("id"));
                anak.setNamaGuru(rs.getString("nama"));
                anak.setNIP(rs.getString("nip"));
                listAnak.add(anak);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listAnak;
    }

    public static ArrayList<Guru> getAllGuruTidakHadir(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT a.*\n" +
                "FROM tbl_kehadiran_guru ka\n" +
                "JOIN tbl_histori_mengajar hka on hka.id = ka.id_histori_mengajar\n" +
                "JOIN tbl_guru a on a.id = hka.id_guru\n" +
                "WHERE ka.presensi = false AND a.status_aktif = 1 AND ka.status_aktif = 1 AND hka.status_aktif = 1\n" +
                "AND hka.id_kelas_per_tahun = ? AND ka.id_kebaktian = ?";
        ArrayList<Guru> listAnak = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, selectedKelas.getID_KELAS_PER_TAHUN());
            ps.setInt(2, selectedKebaktian.getID_KEBAKTIAN());

            rs = ps.executeQuery();
            while (rs.next()) {
                Guru guru = new Guru();
                guru.setID_GURU(rs.getInt("id"));
                guru.setNamaGuru(rs.getString("nama"));
                guru.setNIP(rs.getString("nip"));
                listAnak.add(guru);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listAnak;
    }


    // EDIT or UPDATE
    public static void updateHadir(Connection con, Guru guru) {
        String query = "UPDATE tbl_kehadiran_guru\n" +
                "SET presensi = true\n" +
                "WHERE id_histori_mengajar = (SELECT id\n" +
                "\t\t   FROM tbl_histori_mengajar\n" +
                "\t\t   WHERE id_guru = ? AND id_kelas_per_tahun = ?)";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, guru.getID_GURU());
            ps.setInt(2, selectedKelas.getID_KELAS_PER_TAHUN());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps);
        }
    }

    public static void updateTidakHadir(Connection con, Guru guru) {
        String query = "UPDATE tbl_kehadiran_guru\n" +
                "SET presensi = false\n" +
                "WHERE id_histori_mengajar = (SELECT id\n" +
                "\t\t   FROM tbl_histori_mengajar\n" +
                "\t\t   WHERE id_guru = ? AND id_kelas_per_tahun = ?)";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, guru.getID_GURU());
            ps.setInt(2, selectedKelas.getID_KELAS_PER_TAHUN());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps);
        }
    }

    public static KelasPerTahun getSelectedKelas() {
        return selectedKelas;
    }

    public static void setSelectedKelas(KelasPerTahun selectedKelas) {
        KehadiranGuruDao.selectedKelas = selectedKelas;
    }

    public static Kebaktian getSelectedKebaktian() {
        return selectedKebaktian;
    }

    public static void setSelectedKebaktian(Kebaktian selectedKebaktian) {
        KehadiranGuruDao.selectedKebaktian = selectedKebaktian;
    }


}
