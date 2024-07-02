package com.example.pbo_sekolahminggu.dao.transactional.data;


import com.example.pbo_sekolahminggu.beans.master.data.Anak;
import com.example.pbo_sekolahminggu.beans.transactional.data.HistoriKelasAnak;
import com.example.pbo_sekolahminggu.beans.transactional.data.KelasPerTahun;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class HistoriKelasAnakDao {
    //to get the id from selected class
    private static KelasPerTahun selectedClass;
    //getter setter
    public static KelasPerTahun getSelectedClass() {
        return selectedClass;
    }
    public static void setSelectedClass(KelasPerTahun selectedClass) {
        HistoriKelasAnakDao.selectedClass = selectedClass;
    }

    //fetch data
    public static ArrayList<HistoriKelasAnak> getAll(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        // select id, kelas, jumlah murid
        String query = "SELECT histAnak.id, anak.nama, anak.nis, CONCAT(k.nama_kelas, ' ', COALESCE(kpt.kelas_paralel || ' ', '')) AS kelas, t.tahun_ajaran, histAnak.id_anak, histAnak.id_kelas_per_tahun, kpt.id_tahun_ajaran\n" +
                "FROM tbl_histori_kelas_anak histAnak\n" +
                "JOIN tbl_kelas_per_tahun kpt ON histAnak.id_kelas_per_tahun = kpt.id\n" +
                "JOIN tbl_kelas k ON k.id = kpt.id_kelas\n" +
                "JOIN tbl_tahun_ajaran t ON t.id = kpt.id_tahun_ajaran\n" +
                "JOIN tbl_anak anak ON anak.id = histAnak.id_anak\n" +
                "WHERE anak.status_aktif = 1 ORDER BY histAnak.id\n";
        ArrayList<HistoriKelasAnak> listHistoriKelasAnak = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                HistoriKelasAnak histAnak = new HistoriKelasAnak();
                histAnak.setIdHistoriKelasAnak(rs.getInt("id"));
                histAnak.setNamaAnak(rs.getString("nama"));
                histAnak.setNis(rs.getString("nis"));
                histAnak.setKelas(rs.getString("kelas"));
                histAnak.setTahunAjaran(rs.getString("tahun_ajaran"));
                histAnak.setIdAnak(rs.getInt("id_anak"));
                histAnak.setIdKelasPerTahun(rs.getInt("id_kelas_per_tahun"));
                listHistoriKelasAnak.add(histAnak);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listHistoriKelasAnak;
    }

    // EXPORT
    public static Map<String, Object[]> getAllArrayObject(Connection con, KelasPerTahun kpt) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query =
                "SELECT a.id AS anak_id, a.nama AS nama_anak, COUNT(ka.id) AS total_kehadiran\n" +
                        "FROM tbl_histori_kelas_anak hka\n" +
                        "JOIN tbl_kelas_per_tahun kpt ON kpt.id = hka.id_kelas_per_tahun\n" +
                        "JOIN tbl_anak a ON a.id = hka.id_anak\n" +
                        "JOIN tbl_kelas k ON k.id = kpt.id_kelas\n" +
                        "JOIN tbl_tahun_ajaran ta ON ta.id = kpt.id_tahun_ajaran\n" +
                        "JOIN tbl_kehadiran_anak ka ON hka.id = ka.id_histori_kelas_anak\n" +
                        "WHERE kpt.id = ?\n" +
                        "GROUP BY a.id, a.nama, k.nama_kelas, kpt.kelas_paralel, ta.tahun_ajaran\n" +
                        "ORDER BY anak_id;\n";

        Map<String, Object[]> listKehadiran = new TreeMap<>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, kpt.getIdKelasPerTahun());
            rs = ps.executeQuery();
            int i = 1;
            while (rs.next()) {
                Object[] object = new Object[3];
                object[0] = rs.getInt("anak_id");
                object[1] = rs.getString("nama_anak");
                object[2] = rs.getInt("total_kehadiran");
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

    // INI UNTUK DI WINDOW ASSIGN, ADA 2 QUERY (MASUK DI KELAS ITU, DAN TIDAK MASUK DI KELAS ITU)
    public static ArrayList<Anak> getAllAnakKelas(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT a.* FROM tbl_anak a\n" +
                "JOIN tbl_histori_kelas_anak hka on a.id = hka.id_anak\n" +
                "WHERE hka.id_kelas_per_tahun = ?";
        ArrayList<Anak> listAnak = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, selectedClass.getIdKelasPerTahun());

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

    public static ArrayList<Anak> getAllAnakTidakKelas(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT * FROM tbl_anak\n" +
                "WHERE id NOT IN (SELECT a.id FROM tbl_anak a\n" +
                "\t\t\t\tJOIN tbl_histori_kelas_anak hka on a.id = hka.id_anak\n" +
                "\t\t\t\tWHERE hka.id_kelas_per_tahun = ?) AND status_aktif = 1";
        ArrayList<Anak> listAnak = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, selectedClass.getIdKelasPerTahun());
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

    public static void insertToClass(Connection con, Anak selectedStudent) {
        PreparedStatement ps = null;
        String query = "INSERT INTO tbl_histori_kelas_anak (id_anak, id_kelas_per_tahun) VALUES (?, ?)";
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, selectedStudent.getIdAnak());
            ps.setInt(2, selectedClass.getIdKelasPerTahun());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps);
        }
    }


    public static void removeFromClass(Connection con, Anak selectedStudent) {
        PreparedStatement ps = null;
        String query = "DELETE FROM tbl_histori_kelas_anak " +
                "WHERE id_anak = ? AND id_kelas_per_tahun = ?";
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, selectedStudent.getIdAnak());
            ps.setInt(2, selectedClass.getIdKelasPerTahun());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static ArrayList<HistoriKelasAnak> get(Connection con, int kelasPerTahunId) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT histAnak.id, anak.nama, anak.nis, CONCAT(k.nama_kelas, ' ', COALESCE(kpt.kelas_paralel || ' ', '')) AS kelas, t.tahun_ajaran, histAnak.id_anak, histAnak.id_kelas_per_tahun, kpt.id_tahun_ajaran\n" +
                "FROM tbl_histori_kelas_anak histAnak\n" +
                "JOIN tbl_kelas_per_tahun kpt ON histAnak.id_kelas_per_tahun = kpt.id\n" +
                "JOIN tbl_kelas k ON k.id = kpt.id_kelas\n" +
                "JOIN tbl_tahun_ajaran t ON t.id = kpt.id_tahun_ajaran\n" +
                "JOIN tbl_anak anak ON anak.id = histAnak.id_anak\n" +
                "WHERE kpt.id = ? and anak.status_aktif = 1";
        ArrayList<HistoriKelasAnak> listhistoriAnak = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, kelasPerTahunId);
            rs = ps.executeQuery();
            while (rs.next()) {
                HistoriKelasAnak histAnak = new HistoriKelasAnak();
                histAnak.setIdHistoriKelasAnak(rs.getInt("id"));
                histAnak.setNamaAnak(rs.getString("nama"));
                histAnak.setNis(rs.getString("nis"));
                histAnak.setKelas(rs.getString("kelas"));
                histAnak.setTahunAjaran(rs.getString("tahun_ajaran"));
                histAnak.setIdAnak(rs.getInt("id_anak"));
                histAnak.setIdKelasPerTahun(rs.getInt("id_kelas_per_tahun"));
                listhistoriAnak.add(histAnak);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listhistoriAnak;
    }

    //Function to check if the anak already exists in the class
//    public static boolean exists(Connection conn, int idAnak, int idKelasPerTahun) throws SQLException {
//        String sql = "SELECT COUNT(*) FROM tbl_histori_kelas_anak WHERE id_anak = ? AND id_kelas_per_tahun = ?";
//
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setInt(1, idAnak);
//            pstmt.setInt(2, idKelasPerTahun);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt(1) > 0;
//                }
//            }


//
//    //Function to check if the anak already exists in the class
//    public static boolean exists(Connection conn, int idAnak, int idKelasPerTahun) throws SQLException {
//        String sql = "SELECT COUNT(*) FROM tbl_histori_kelas_anak WHERE id_anak = ? AND id_kelas_per_tahun = ?";
//
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setInt(1, idAnak);
//            pstmt.setInt(2, idKelasPerTahun);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getInt(1) > 0;
//                }
//            }
//        }
//        return false;
//    }


//    // SAVE
//    public static void save(Connection con, HistoriKelasAnak HistoriKelasAnak) {
//        PreparedStatement statement = null;
//        String query = "INSERT INTO tbl_histori_kelas_anak (kelas, jumlah_murid) VALUES (?, ?, ?)";
//
//        try {
//            statement = con.prepareStatement(query);
////            statement.setString(1, HistoriKelasAnak. ); // kelas
////            statement.setString(2, HistoriKelasAnak. ); // jumlah_murid
//            statement.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException("Error saving HistoriKelasAnak: " + e.getMessage());
//        } finally {
//            ConnectionManager.close(statement);
//        }
//    }

//    // DELETE
//    public static void delete(Connection con, HistoriKelasAnak historiKelasAnak) {
//        PreparedStatement statement = null;
//        String query = "UPDATE tbl_histori_kelas_anak SET status_aktif = 0 WHERE id = ?";
//
//        try {
//            statement = con.prepareStatement(query);
//            statement.setInt(1, historiKelasAnak.getID_HISTORI_KELAS_ANAK());
//            statement.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException("Error deleting HistoriKelasAnak: " + e.getMessage());
//        } finally {
//            ConnectionManager.close(statement);
//        }
//    }

        }



