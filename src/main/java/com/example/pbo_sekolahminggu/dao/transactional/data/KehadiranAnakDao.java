package com.example.pbo_sekolahminggu.dao.transactional.data;


import com.example.pbo_sekolahminggu.beans.master.data.Anak;
import com.example.pbo_sekolahminggu.beans.master.data.Kebaktian;
import com.example.pbo_sekolahminggu.beans.master.data.TahunAjaran;
import com.example.pbo_sekolahminggu.beans.transactional.data.HistoriKelasAnak;
import com.example.pbo_sekolahminggu.beans.transactional.data.KehadiranAnak;
import com.example.pbo_sekolahminggu.beans.transactional.data.KelasPerTahun;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

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


    public static ArrayList<KehadiranAnak> getAll(Connection con) {
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
                "                WHERE kbk.status_aktif = 1 AND a.status_aktif = 1 AND kpt.status_aktif = 1 AND k.status_aktif = 1 AND ta.status_aktif = 1" +
                " ORDER BY ka.id ASC";
        ArrayList<KehadiranAnak> listkehadiranAnak = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                KehadiranAnak kehadiranAnak = new KehadiranAnak();
                kehadiranAnak.setIdKehadiranAnak(rs.getInt("id"));
                kehadiranAnak.setNamaAnak(rs.getString("nama"));
                kehadiranAnak.setNis(rs.getString("nis"));
                kehadiranAnak.setKelas(rs.getString("kelas"));
                kehadiranAnak.setTahunAjaran(rs.getString("tahun_ajaran"));
                kehadiranAnak.setKebaktian(rs.getString("jenis_kebaktian"));
                kehadiranAnak.setTglKebaktian(rs.getDate("tanggal"));
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
    public static ArrayList<KehadiranAnak> getAllFiltered(Connection con, KelasPerTahun kelas, Kebaktian kbk) {
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
                "                WHERE hka.id_kelas_per_tahun = ? AND ka.id_kebaktian = ? AND kbk.status_aktif = 1 AND a.status_aktif = 1 AND kpt.status_aktif = 1 " +
                "AND k.status_aktif = 1 AND ta.status_aktif = 1 ORDER BY ka.id";
        ArrayList<KehadiranAnak> listkehadiranAnak = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, kelas.getIdKelasPerTahun());
            ps.setInt(2, kbk.getIdKebaktian());
            rs = ps.executeQuery();
            while (rs.next()) {
                KehadiranAnak kehadiranAnak = new KehadiranAnak();
                kehadiranAnak.setIdKehadiranAnak(rs.getInt("id"));
                kehadiranAnak.setNamaAnak(rs.getString("nama"));
                kehadiranAnak.setNis(rs.getString("nis"));
                kehadiranAnak.setKelas(rs.getString("kelas"));
                kehadiranAnak.setTahunAjaran(rs.getString("tahun_ajaran"));
                kehadiranAnak.setKebaktian(rs.getString("jenis_kebaktian"));
                kehadiranAnak.setTglKebaktian(rs.getDate("tanggal"));
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
            ps.setInt(1, selectedKelas.getIdKelasPerTahun());
            ps.setInt(2, selectedKebaktian.getIdKebaktian());

            rs = ps.executeQuery();
            while (rs.next()) {
                Anak anak = new Anak();
                anak.setIdAnak(rs.getInt("id"));
                anak.setNama(rs.getString("nama"));
                anak.setNis(rs.getString("nis"));
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
            ps.setInt(1, selectedKelas.getIdKelasPerTahun());
            ps.setInt(2, selectedKebaktian.getIdKebaktian());

            rs = ps.executeQuery();
            while (rs.next()) {
                Anak anak = new Anak();
                anak.setIdAnak(rs.getInt("id"));
                anak.setNama(rs.getString("nama"));
                anak.setNis(rs.getString("nis"));
                listAnak.add(anak);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listAnak;
    }

    // EXPORT
    public static Map<String, Object[]> getAllArrayObject(Connection con, TahunAjaran ta) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "WITH info_anak AS (\n" +
                "                    SELECT \n" +
                "                        hka.id AS id_histori_kelas_anak, \n" +
                "                        a.nis, \n" +
                "                        a.nama,\n" +
                "                        k.nama_kelas || \n" +
                "                            CASE\n" +
                "                                WHEN kpt.kelas_paralel IS NOT NULL THEN ' ' || kpt.kelas_paralel\n" +
                "                                ELSE ''\n" +
                "                            END AS kelas,\n" +
                "                        kpt.id_tahun_ajaran\n" +
                "                    FROM tbl_histori_kelas_anak hka\n" +
                "                    JOIN tbl_kelas_per_tahun kpt ON kpt.id = hka.id_kelas_per_tahun\n" +
                "                    JOIN tbl_kelas k ON k.id = kpt.id_kelas\n" +
                "                    JOIN tbl_anak a ON a.id = hka.id_anak\n" +
                "                ),\n" +
                "                banyak_kehadiran AS (\n" +
                "                    SELECT \n" +
                "                        ka.id_histori_kelas_anak, \n" +
                "                        COUNT(*) AS n_kehadiran\n" +
                "                    FROM tbl_kehadiran_anak ka\n" +
                "                    WHERE presensi = true\n" +
                "                    GROUP BY ka.id_histori_kelas_anak\n" +
                "                ),\n" +
                "               data_kehadiran_lengkap AS (\n" +
                "                    SELECT *\n" +
                "                   FROM banyak_kehadiran bk\n" +
                "                    JOIN info_anak ia ON ia.id_histori_kelas_anak = bk.id_histori_kelas_anak\n" +
                "                    WHERE ia.id_tahun_ajaran = ?\n" +
                "                ),\n" +
                "                max_per_kelas AS (\n" +
                "                    SELECT dkl.kelas, MAX(dkl.n_kehadiran) AS max_n_kehadiran\n" +
                "                   FROM data_kehadiran_lengkap dkl\n" +
                "                    GROUP BY dkl.kelas\n" +
                "                )\n" +
                "                SELECT * \n" +
                "                FROM data_kehadiran_lengkap dkl\n" +
                "                JOIN max_per_kelas mpk ON mpk.kelas = dkl.kelas\n" +
                "                WHERE dkl.n_kehadiran = mpk.max_n_kehadiran AND dkl.kelas = mpk.kelas";
        Map<String, Object[]> listKehadiran = new TreeMap<>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, ta.getIdTahunAjaran());
            rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                Object[] object = new Object[5];
                object[0] = rs.getInt("id_histori_kelas_anak");
                object[1] = rs.getString("nis");
                object[2] = rs.getString("nama");
                object[3] = rs.getString("kelas");
                object[4] = rs.getInt("max_n_kehadiran");
                listKehadiran.put(String.valueOf(i), object);
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(rs, ps);
        }
        return listKehadiran;
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
            ps.setInt(1, anak.getIdAnak());
            ps.setInt(2, selectedKelas.getIdKelasPerTahun());
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
            ps.setInt(1, anak.getIdAnak());
            ps.setInt(2, selectedKelas.getIdKelasPerTahun());
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
            statement.setInt(1, kehadiranAnak.getIdKehadiranAnak());
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
            ps.setInt(1, selectedKebaktian.getIdKebaktian());
            ps.setInt(2, selectedKelas.getIdKelasPerTahun());
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
