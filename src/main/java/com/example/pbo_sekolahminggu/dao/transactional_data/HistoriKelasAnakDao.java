package com.example.pbo_sekolahminggu.dao.transactional_data;


import com.example.pbo_sekolahminggu.beans.master_data.Anak;
import com.example.pbo_sekolahminggu.beans.transactional_data.HistoriKelasAnak;
import com.example.pbo_sekolahminggu.beans.transactional_data.KelasPerTahun;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
                "WHERE anak.status_aktif = 1\n";
        ArrayList<HistoriKelasAnak> listHistoriKelasAnak = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                HistoriKelasAnak histAnak = new HistoriKelasAnak();
                histAnak.setID_HISTORI_KELAS_ANAK(rs.getInt("id"));
                histAnak.setNama_anak(rs.getString("nama"));
                histAnak.setNis(rs.getString("nis"));
                histAnak.setKelas(rs.getString("kelas"));
                histAnak.setTahun_ajaran(rs.getString("tahun_ajaran"));
                histAnak.setId_anak(rs.getInt("id_anak"));
                histAnak.setId_kelas_per_tahun(rs.getInt("id_kelas_per_tahun"));
                listHistoriKelasAnak.add(histAnak);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listHistoriKelasAnak;
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
                histAnak.setID_HISTORI_KELAS_ANAK(rs.getInt("id"));
                histAnak.setNama_anak(rs.getString("nama"));
                histAnak.setNis(rs.getString("nis"));
                histAnak.setKelas(rs.getString("kelas"));
                histAnak.setTahun_ajaran(rs.getString("tahun_ajaran"));
                histAnak.setId_anak(rs.getInt("id_anak"));
                histAnak.setId_kelas_per_tahun(rs.getInt("id_kelas_per_tahun"));
                listhistoriAnak.add(histAnak);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listhistoriAnak;
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
            ps.setInt(1, selectedClass.getID_KELAS_PER_TAHUN());

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

    public static ArrayList<Anak> getAllAnakTidakKelas(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT * FROM tbl_anak\n" +
                "WHERE status_aktif = 1";
        ArrayList<Anak> listAnak = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);

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

    public static void insertToClass(Connection con, Anak selectedStudent) {
        PreparedStatement ps = null;
        String query = "INSERT INTO tbl_histori_kelas_anak (id_anak, id_kelas_per_tahun) VALUES (?, ?)";
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, selectedStudent.getID_ANAK());
            ps.setInt(2, selectedClass.getID_KELAS_PER_TAHUN());
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
            ps.setInt(1, selectedStudent.getID_ANAK());
            ps.setInt(2, selectedClass.getID_KELAS_PER_TAHUN());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps);
        }
    }

    //Function to check if the anak already exists in the class
    public static boolean exists(Connection conn, int idAnak, int idKelasPerTahun) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tbl_histori_kelas_anak WHERE id_anak = ? AND id_kelas_per_tahun = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idAnak);
            pstmt.setInt(2, idKelasPerTahun);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

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