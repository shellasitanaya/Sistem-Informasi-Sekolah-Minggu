package com.example.pbo_sekolahminggu.dao.master_data;

import com.example.pbo_sekolahminggu.beans.master_data.Kelas;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class KelasDao {
    public static ArrayList<Kelas> getAll(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select * from tbl_kelas where status_aktif = 1";
        ArrayList<Kelas> listkelas = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                Kelas kelas = new Kelas();
                kelas.setID_KELAS(rs.getInt("id"));
                kelas.setNamaKelas(rs.getString("nama"));
                listkelas.add(kelas);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listkelas;
    }

    // SAVE
    public static void save(Connection con, Kelas kelas) {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_kelas(nama) VALUES (?)";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, kelas.getNamaKelas());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving kelas: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // EDIT
    public static void edit(Connection con, Kelas kelas) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_kelas SET nama = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, kelas.getNamaKelas());
            statement.setInt(2, kelas.getID_KELAS());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error editing kelas: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // DELETE
    public static void delete(Connection con, Kelas kelas) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_kelas SET status_aktif = 0 WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, kelas.getID_KELAS());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting kelas: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

}
