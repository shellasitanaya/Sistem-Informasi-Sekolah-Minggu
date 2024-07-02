package com.example.pbo_sekolahminggu.dao.master.data;


import com.example.pbo_sekolahminggu.beans.master.data.Kebaktian;
import com.example.pbo_sekolahminggu.beans.master.data.TahunAjaran;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class KebaktianDao {
    public static ArrayList<Kebaktian> getAll(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select * from tbl_kebaktian where status_aktif = 1 ORDER BY id ASC";
        ArrayList<Kebaktian> listkebaktian = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                Kebaktian kebaktian = new Kebaktian();
                kebaktian.setIdKebaktian(rs.getInt("id"));
                kebaktian.setJenisKebaktian(rs.getString("jenis_kebaktian"));
                kebaktian.setTanggal(rs.getDate("tanggal"));;
                listkebaktian.add(kebaktian);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listkebaktian;
    }

    public static ArrayList<Kebaktian> getFilteredKebaktian(Connection con, TahunAjaran th) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT * FROM tbl_kebaktian \n" +
                "WHERE \n" +
                "    ((EXTRACT(YEAR FROM tanggal) = SUBSTR(?, 0, 5)::integer AND EXTRACT(MONTH FROM tanggal) > 6)\n" +
                "    OR\n" +
                "    (EXTRACT(YEAR FROM tanggal) = SUBSTR(?, 6, 8)::integer AND EXTRACT(MONTH FROM tanggal) <= 6)) AND status_aktif=1\n";
        ArrayList<Kebaktian> listkebaktian = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            ps.setString(1, th.getTahunAjaran()); //extract tahun semester ganjil
            ps.setString(2, th.getTahunAjaran()); //extract tahun semester genap
            rs = ps.executeQuery();
            while (rs.next()) {
                Kebaktian kebaktian = new Kebaktian();
                kebaktian.setIdKebaktian(rs.getInt("id"));
                kebaktian.setJenisKebaktian(rs.getString("jenis_kebaktian"));
                kebaktian.setTanggal(rs.getDate("tanggal"));;
                listkebaktian.add(kebaktian);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listkebaktian;
    }

    // CREATE
    public static void create (Connection con, Kebaktian kebaktian) throws SQLException {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_kebaktian (jenis_kebaktian, tanggal) VALUES (INITCAP(?), ?)";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, kebaktian.getJenisKebaktian());
            statement.setDate(2, kebaktian.getTanggal());
            statement.executeUpdate();
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // EDIT
    public static void update(Connection con, Kebaktian kebaktian) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_kebaktian SET jenis_kebaktian = INITCAP(?), tanggal = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, kebaktian.getJenisKebaktian());
            statement.setDate(2, kebaktian.getTanggal());
            statement.setInt(3, kebaktian.getIdKebaktian());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error editing kebaktian: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // DELETE
    public static void delete(Connection con, Kebaktian kebaktian) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_kebaktian SET status_aktif = 0 WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, kebaktian.getIdKebaktian());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting kebaktian: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    public static Map<String, Object[]> getAllArrayObject(Connection con, Kebaktian kbk) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "WITH DetailedCounts AS (\n" +
                "    SELECT \n" +
                "        kbk.jenis_kebaktian, \n" +
                "        kbk.tanggal,\n" +
                "        CONCAT(k.nama_kelas, ' ', COALESCE(kpt.kelas_paralel || ' ', '')) AS kelas,\n" +
                "        SUM(CASE WHEN a.jenis_kelamin = 'male' THEN 1 ELSE 0 END) AS LakiLaki,\n" +
                "        SUM(CASE WHEN a.jenis_kelamin = 'female' THEN 1 ELSE 0 END) AS Perempuan,\n" +
                "        COUNT(*) AS Total\n" +
                "    FROM tbl_kebaktian kbk\n" +
                "    JOIN tbl_kehadiran_anak ka ON ka.id_kebaktian = kbk.id\n" +
                "    JOIN tbl_histori_kelas_anak hka ON hka.id = ka.id_histori_kelas_anak\n" +
                "    JOIN tbl_anak a ON a.id = hka.id_anak\n" +
                "    JOIN tbl_kelas_per_tahun kpt ON kpt.id = hka.id_kelas_per_tahun\n" +
                "    JOIN tbl_kelas k ON k.id = kpt.id_kelas\n" +
                "    WHERE \n" +
                "        kbk.id = ?\n" +
                "    GROUP BY \n" +
                "        kbk.jenis_kebaktian, k.nama_kelas, kpt.kelas_paralel, kbk.tanggal\n" +
                "),\n" +
                "TotalCounts AS (\n" +
                "    SELECT\n" +
                "        '',\n" +
                "        NULL::DATE as tanggal,\n" +
                "        'Total' AS kelas,\n" +
                "        SUM(LakiLaki) AS LakiLaki,\n" +
                "        SUM(Perempuan) AS Perempuan,\n" +
                "        SUM(Total) AS Total\n" +
                "    FROM DetailedCounts\n" +
                ")\n" +
                "SELECT *\n" +
                "FROM (\n" +
                "    SELECT * FROM DetailedCounts\n" +
                "    UNION ALL\n" +
                "    SELECT * FROM TotalCounts\n" +
                ") AS combined\n" +
                "ORDER BY \n" +
                "    CASE WHEN kelas = 'Total' THEN 1 ELSE 0 END,\n" +
                "    kelas DESC;\n";
        Map<String, Object[]> listKebaktian = new TreeMap<String, Object[]>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, kbk.getIdKebaktian());
            rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                Object[] object = new Object[6];
                object[0] = rs.getString("jenis_kebaktian");
                object[1] = rs.getDate("tanggal");
                object[2] = rs.getString("kelas");
                object[3] = rs.getInt("LakiLaki");
                object[4] = rs.getInt("Perempuan");
                object[5] = rs.getInt("Total");

                listKebaktian.put(String.valueOf(i), object);
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(rs, ps);
        }
        return listKebaktian;
    }
}
