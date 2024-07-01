package com.example.pbo_sekolahminggu.dao.transactional.data;

import com.example.pbo_sekolahminggu.beans.transactional.data.HistoriMengajar;
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
                kelasPerTahun.setIdKelasPerTahun(rs.getInt("id"));
                kelasPerTahun.setRuangKelas(rs.getString("ruang_kelas"));
                kelasPerTahun.setKelasParalel(rs.getString("kelas_paralel"));
                kelasPerTahun.setNamaKelas(rs.getString("nama_kelas"));
                kelasPerTahun.setTahunAjaran(rs.getString("tahun_ajaran"));
                kelasPerTahun.setIdKelas(rs.getInt("id_kelas"));
                kelasPerTahun.setIdTahunAjaran(rs.getInt("id_tahun_ajaran"));
                listkelasPerTahun.add(kelasPerTahun);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listkelasPerTahun;
    }

    public static String getNextParalel(Connection con, int idKelas, int idTahunAjaran) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT \n" +
                "    CASE \n" +
                "        WHEN MAX(kelas_paralel) IS NULL THEN 'A'\n" +
                "        ELSE CHR(ASCII(MAX(kelas_paralel)) + 1)\n" +
                "    END AS next_value\n" +
                "FROM tbl_kelas_per_tahun\n" +
                "WHERE id_kelas = ? AND id_tahun_ajaran = ? and status_aktif=1;";

        String ans = "";
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, idKelas);
            ps.setInt(2, idTahunAjaran);
            rs = ps.executeQuery();
            while (rs.next()) {
                ans = rs.getString("next_value");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return ans;
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
            ps.setInt(1, kpt.getIdKelasPerTahun());
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
            statement.setInt(1, kelasPerTahun.getIdKelas());
            statement.setInt(2, kelasPerTahun.getIdTahunAjaran());
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
        String query = "UPDATE tbl_kelas_per_tahun SET ruang_kelas = ?, kelas_paralel = ?, id_kelas=?, id_tahun_ajaran=? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, kelasPerTahun.getRuangKelas());
            statement.setString(2, kelasPerTahun.getKelasParalel());
            statement.setInt(3, kelasPerTahun.getIdKelas());
            statement.setInt(4, kelasPerTahun.getIdTahunAjaran());
            statement.setInt(5, kelasPerTahun.getIdKelasPerTahun());
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
            statement.setInt(1, kelasPerTahun.getIdKelasPerTahun());
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
            ps.setInt(1, th.getIdTahunAjaran());
            rs = ps.executeQuery();
            while (rs.next()) {
                KelasPerTahun kelasPerTahun = new KelasPerTahun();
                kelasPerTahun.setIdKelasPerTahun(rs.getInt("id"));
                kelasPerTahun.setRuangKelas(rs.getString("ruang_kelas"));
                kelasPerTahun.setKelasParalel(rs.getString("kelas_paralel"));
                kelasPerTahun.setNamaKelas(rs.getString("nama_kelas"));
                kelasPerTahun.setTahunAjaran(rs.getString("tahun_ajaran"));
                kelasPerTahun.setIdKelas(rs.getInt("id_kelas"));
                kelasPerTahun.setIdTahunAjaran(rs.getInt("id_tahun_ajaran"));
                listkelasPerTahun.add(kelasPerTahun);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listkelasPerTahun;
    }


    public static int getIdByProperties(Connection con, KelasPerTahun kelasPerTahun) throws SQLException {
        int id = -1; // Initialize with a default value, assuming -1 represents an invalid ID

        // Prepare a SQL query to retrieve the ID based on the properties
        String query = "SELECT * \n" +
                "FROM tbl_kelas_per_tahun\n" +
                "WHERE id_kelas=? AND id_tahun_ajaran=? AND ruang_kelas=? AND kelas_paralel=? AND status_aktif=1";

        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, kelasPerTahun.getIdKelas());
            ps.setInt(2, kelasPerTahun.getIdTahunAjaran());
            ps.setString(3, kelasPerTahun.getRuangKelas());
            ps.setString(4,kelasPerTahun.getKelasParalel());


            // Execute the query
            try (ResultSet rs = ps.executeQuery()) {
                // Check if a record was found
                if (rs.next()) {
                    // Retrieve the ID from the result set
                    id = rs.getInt("id");
                }
            }
        }

        return id;
    }


}
