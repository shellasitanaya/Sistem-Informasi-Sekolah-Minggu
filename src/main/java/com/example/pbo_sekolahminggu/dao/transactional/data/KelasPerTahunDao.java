package com.example.pbo_sekolahminggu.dao.transactional.data;

import com.example.pbo_sekolahminggu.beans.transactional.data.KelasPerTahun;
import com.example.pbo_sekolahminggu.beans.master.data.TahunAjaran;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class KelasPerTahunDao {
    public static ArrayList<KelasPerTahun> getAll(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT \n" +
                "kpt.id,\n" +
                "(SELECT nama_kelas FROM tbl_kelas k WHERE k.id = kpt.id_kelas),\n" +
                "kpt.kelas_paralel,\n" +
                "(SELECT tahun_ajaran FROM tbl_tahun_ajaran tj WHERE tj.id = kpt.id_tahun_ajaran),\n" +
                "kpt.ruang_kelas,\n" +
                "--foreign keys\n" +
                "kpt.id_kelas,\n" +
                "kpt.id_tahun_ajaran\n" +
                "FROM tbl_kelas_per_tahun kpt\n" +
                "WHERE kpt.status_aktif = 1\n";
        ArrayList<KelasPerTahun> listkelasPerTahun = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                KelasPerTahun kelasPerTahun = new KelasPerTahun();
                kelasPerTahun.setID_KELAS_PER_TAHUN(rs.getInt("id"));
                kelasPerTahun.setRuangKelas(rs.getString("ruang_kelas"));
                kelasPerTahun.setKelasParalel(rs.getString("kelas_paralel"));
                kelasPerTahun.setNamaKelas(rs.getString("nama_kelas"));
                kelasPerTahun.setTahunAjaran(rs.getString("tahun_ajaran"));
                kelasPerTahun.setID_KELAS(rs.getInt("id_kelas"));
                kelasPerTahun.setID_TAHUN_AJARAN(rs.getInt("id_tahun_ajaran"));
                listkelasPerTahun.add(kelasPerTahun);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listkelasPerTahun;
    }

    // EXPORT
    public static Map<String, Object[]> getAllArrayObject(Connection con, KelasPerTahun kpt) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query =
                "WITH banyak_murid_kelas_per_tahun AS(\n" +
                        "\tSELECT id_kelas_per_tahun, COUNT(*) AS banyak_murid\n" +
                        "\tFROM tbl_histori_kelas_anak\n" +
                        "\tGROUP BY id_kelas_per_tahun\n" +
                        "),kelas_dan_banyak_murid_per_tahun AS (\n" +
                        "\tSELECT (SELECT nama_kelas FROM tbl_kelas k WHERE k.id = kpt.id_kelas), kelas_paralel, bmkpt.banyak_murid\n" +
                        "\tFROM banyak_murid_kelas_per_tahun bmkpt\n" +
                        "\tLEFT JOIN tbl_kelas_per_tahun kpt ON bmkpt.id_kelas_per_tahun = kpt.id\n" +
                        "\tWHERE kpt.id_tahun_ajaran = ?\n" +
                        ")\n" +
                        "SELECT *\n" +
                        "FROM kelas_dan_banyak_murid_per_tahun\n" +
                        "ORDER BY banyak_murid DESC";

        Map<String, Object[]> listMengajar = new TreeMap<String, Object[]>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, kpt.getID_KELAS_PER_TAHUN());
            rs = ps.executeQuery();

            int i = 1;
            while(rs.next()) {
                Object[] object = new Object[3];
                object[0] = rs.getString("nama_kelas");
                object[1] = rs.getString("kelas_paralel");
                object[2] = rs.getInt("banyak_murid");

                listMengajar.put(String.valueOf(i), object);
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(rs, ps);
        }
        return listMengajar;
    }


    // SAVE
    public static void save(Connection con, KelasPerTahun kelasPerTahun) {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_kelas_per_tahun(id_kelas, id_tahun_ajaran, ruang_kelas, kelas_paralel) VALUES (?, ?, ?, ?)";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, kelasPerTahun.getID_KELAS());
            statement.setInt(2, kelasPerTahun.getID_TAHUN_AJARAN());
            statement.setString(3, kelasPerTahun.getRuangKelas());
            statement.setString(4, kelasPerTahun.getKelasParalel());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving kelasPerTahun: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // EDIT
    public static void edit(Connection con, KelasPerTahun kelasPerTahun) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_kelasPerTahun SET ruang_kelas = ?, kelas_paralel = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, kelasPerTahun.getRuangKelas());
            statement.setString(2, kelasPerTahun.getKelasParalel());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error editing kelasPerTahun: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // DELETE
    public static void delete(Connection con, KelasPerTahun kelasPerTahun) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_kelas_per_tahun SET status_aktif = 0 WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, kelasPerTahun.getID_KELAS_PER_TAHUN());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting kelasPerTahun: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }


    public static ArrayList<KelasPerTahun> getFilteredClasses(Connection con, TahunAjaran th) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT kpt.id,\n" +
                "                (SELECT nama_kelas FROM tbl_kelas k WHERE k.id = kpt.id_kelas),\n" +
                "                kpt.kelas_paralel,\n" +
                "                (SELECT tahun_ajaran FROM tbl_tahun_ajaran tj WHERE tj.id = kpt.id_tahun_ajaran),\n" +
                "                kpt.ruang_kelas, \n" +
                "                kpt.id_kelas, \n" +
                "                kpt.id_tahun_ajaran\n" +
                "                FROM tbl_kelas_per_tahun kpt\n" +
                "                WHERE kpt.status_aktif = 1 AND kpt.id_tahun_ajaran = ?";
        ArrayList<KelasPerTahun> listkelasPerTahun = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, th.getID_TAHUN_AJARAN());
            rs = ps.executeQuery();
            while (rs.next()) {
                KelasPerTahun kelasPerTahun = new KelasPerTahun();
                kelasPerTahun.setID_KELAS_PER_TAHUN(rs.getInt("id"));
                kelasPerTahun.setRuangKelas(rs.getString("ruang_kelas"));
                kelasPerTahun.setKelasParalel(rs.getString("kelas_paralel"));
                kelasPerTahun.setNamaKelas(rs.getString("nama_kelas"));
                kelasPerTahun.setTahunAjaran(rs.getString("tahun_ajaran"));
                kelasPerTahun.setID_KELAS(rs.getInt("id_kelas"));
                kelasPerTahun.setID_TAHUN_AJARAN(rs.getInt("id_tahun_ajaran"));
                listkelasPerTahun.add(kelasPerTahun);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listkelasPerTahun;
    }
}
