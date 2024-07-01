package com.example.pbo_sekolahminggu.dao.transactional.data;

import com.example.pbo_sekolahminggu.beans.master.data.Guru;
import com.example.pbo_sekolahminggu.beans.transactional.data.HistoriMengajar;
import com.example.pbo_sekolahminggu.beans.transactional.data.KelasPerTahun;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class HistoriMengajarDao {
    // tampilin nama_guru dan kelasnya
    private static KelasPerTahun selectedClass;
    //getter setter
    public static KelasPerTahun getSelectedClass() {
        return selectedClass;
    }
    public static void setSelectedClass(KelasPerTahun selectedClass) {
        HistoriMengajarDao.selectedClass = selectedClass;
    }
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
                "WHERE thm.status_aktif = 1\n" +
                "ORDER BY thm.id";
        ArrayList<HistoriMengajar> listhistoriMengajar = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                HistoriMengajar historiMengajar = new HistoriMengajar();
                historiMengajar.setIdHistoriMengajar(rs.getInt("id"));
                historiMengajar.setNamaGuru(rs.getString("nama"));
                historiMengajar.setNip(rs.getString("nip"));
                historiMengajar.setKelas(rs.getString("kelas"));
                historiMengajar.setTahunAjaran(rs.getString("tahun_ajaran"));
                historiMengajar.setIdGuru(rs.getInt("id_guru"));
                historiMengajar.setIdKelasPerTahun(rs.getInt("id_kelas_per_tahun"));
                listhistoriMengajar.add(historiMengajar);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listhistoriMengajar;
    }

    public static ArrayList<HistoriMengajar> get(Connection con, int kelasPerTahunId) {
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
                "WHERE thm.status_aktif = 1 AND kpt.id=?\n" +
                "ORDER BY thm.id";
        ArrayList<HistoriMengajar> listhistoriMengajar = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, kelasPerTahunId);
            rs = ps.executeQuery();
            while (rs.next()) {
                HistoriMengajar historiMengajar = new HistoriMengajar();
                historiMengajar.setIdHistoriMengajar(rs.getInt("id"));
                historiMengajar.setNamaGuru(rs.getString("nama"));
                historiMengajar.setNip(rs.getString("nip"));
                historiMengajar.setKelas(rs.getString("kelas"));
                historiMengajar.setIdTahunAjaran(rs.getInt("id_tahun_ajaran"));
                historiMengajar.setTahunAjaran(rs.getString("tahun_ajaran"));
                historiMengajar.setIdGuru(rs.getInt("id_guru"));
                historiMengajar.setIdKelasPerTahun(rs.getInt("id_kelas_per_tahun"));
                listhistoriMengajar.add(historiMengajar);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listhistoriMengajar;
    }

    public static ArrayList<Guru> getAllGuruKelas(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT a.* FROM tbl_guru a\n" +
                "                JOIN tbl_histori_mengajar hm on a.id = hm.id_guru\n" +
                "                WHERE hm.id_kelas_per_tahun = ? AND a.status_aktif = 1";
        ArrayList<Guru> listGuru = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, selectedClass.getIdKelasPerTahun());

            rs = ps.executeQuery();
            while (rs.next()) {
                Guru guru = new Guru();
                guru.setIdGuru(rs.getInt("id"));
                guru.setNamaGuru(rs.getString("nama"));
                guru.setNip(rs.getString("nip"));
                listGuru.add(guru);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listGuru;
    }

    public static ArrayList<Guru> getAllGuruTidakKelas(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT * FROM tbl_guru WHERE status_aktif = 1";
        ArrayList<Guru> listGuru = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                Guru guru = new Guru();
                guru.setIdGuru(rs.getInt("id"));
                guru.setNamaGuru(rs.getString("nama"));
                guru.setNip(rs.getString("nip"));
                listGuru.add(guru);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listGuru;
    }

    public static void insertToClass(Connection con, Guru selectedTeacher) {
        PreparedStatement ps = null;
        String query = "INSERT INTO tbl_histori_mengajar (id_guru, id_kelas_per_tahun) VALUES (?, ?)";
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, selectedTeacher.getIdGuru());
            ps.setInt(2, selectedClass.getIdKelasPerTahun());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps);
        }
    }

    public static void removeFromClass(Connection con, Guru selectedTeacher) {
        PreparedStatement ps = null;
        String query = "DELETE FROM tbl_histori_mengajar " +
                "WHERE id_guru = ? AND id_kelas_per_tahun = ?";
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, selectedTeacher.getIdGuru());
            ps.setInt(2, selectedClass.getIdKelasPerTahun());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps);
        }
    }
    // SAVE
    public static void save(Connection con, HistoriMengajar historiMengajar) {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_histori_mengajar (id_kelas_per_tahun, id_guru) VALUES (?, ?)";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, historiMengajar.getIdKelasPerTahun() ); // kelas
            statement.setInt(2, historiMengajar.getIdGuru()); // guru
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
            statement.setInt(1, historiMengajar.getIdKelasPerTahun());
            statement.setInt(2, historiMengajar.getIdGuru());
            statement.setInt(3, historiMengajar.getIdHistoriMengajar());
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
            statement.setInt(1, historiMengajar.getIdHistoriMengajar());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting historiMengajar: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    public static Map<String, Object[]> getAllArrayObject(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT \n" +
                "    g.id AS id_guru,\n" +
                "    g.nama AS nama_guru,\n" +
                "    COUNT(hm.id) AS jumlah_mengajar\n" +
                "FROM \n" +
                "    tbl_guru g\n" +
                "    JOIN tbl_histori_mengajar hm ON g.id = hm.id_guru\n" +
                "GROUP BY \n" +
                "    g.id, g.nama\n" +
                "ORDER BY \n" +
                "    jumlah_mengajar DESC\n" +
                "LIMIT 3;\n";
        Map<String, Object[]> listMengajar = new TreeMap<String, Object[]>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            int i = 1;
            while(rs.next()) {
                Object[] object = new Object[3];
                object[0] = rs.getInt("id_guru");
                object[1] = rs.getString("nama_guru");
                object[2] = rs.getInt("jumlah_mengajar");

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




}
