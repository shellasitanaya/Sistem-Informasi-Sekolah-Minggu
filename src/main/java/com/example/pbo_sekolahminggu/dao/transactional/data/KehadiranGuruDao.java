package com.example.pbo_sekolahminggu.dao.transactional.data;

import com.example.pbo_sekolahminggu.beans.transactional.data.KehadiranGuru;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class KehadiranGuruDao {
    public static ArrayList<KehadiranGuru> getAll(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT\n" +
                "kg.id,\n" +
                "(SELECT nama FROM tbl_guru g WHERE g.id = kg.id_pembimbing),\n" +
                "(SELECT nip FROM tbl_guru g WHERE g.id = kg.id_pembimbing),\n" +
                "(SELECT \n" +
                " \tnama_kelas FROM tbl_kelas k WHERE k.id = \n" +
                " \t(SELECT id_kelas FROM tbl_kelas_per_tahun kpt WHERE kpt.id =\n" +
                "\t\t(SELECT id_kelas_per_tahun FROM tbl_histori_mengajar hm WHERE hm.id =\n" +
                "\t\t\tkg.id_pembimbing\n" +
                "\t\t)\n" +
                "\t)\n" +
                ")\n" +
                "|| ' ' ||\n" +
                "COALESCE(\n" +
                "\t(SELECT kelas_paralel FROM tbl_kelas_per_tahun kpt WHERE kpt.id =\n" +
                "\t\t(SELECT id_kelas_per_tahun FROM tbl_histori_mengajar hm WHERE hm.id =\n" +
                "\t\t\tkg.id_pembimbing\n" +
                "\t\t)\n" +
                "\t),''\n" +
                ") AS kelas\n" +
                ",\n" +
                "(SELECT jenis_kebaktian FROM tbl_kebaktian k WHERE k.id = kg.id_kebaktian),\n" +
                "(SELECT tanggal FROM tbl_kebaktian k WHERE k.id = kg.id_kebaktian),\n" +
                "presensi,\n" +
                "id_pembimbing,\n" +
                "id_kebaktian\n" +
                "FROM tbl_kehadiran_guru kg\n" +
                "WHERE kg.status_aktif = 1\n";
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
                kehadiranGuru.setID_HISTORI_MENGAJAR(rs.getInt("id_pembimbing"));
                kehadiranGuru.setKelas(rs.getString("kelas"));
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

    // EXPORT
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
                "    tbl_histori_mengajar h ON kg.id_pembimbing = h.id " +
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

}
