package com.example.pbo_sekolahminggu.dao;

import com.example.pbo_sekolahminggu.beans.OrangTua;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrangTuaDao {
    public static ArrayList<OrangTua> getAll(Connection con) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "select * from tbl_orangTua where status_aktif = 1";
        ArrayList<OrangTua> listorangTua = new ArrayList<>();
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                OrangTua orangTua = new OrangTua();
                orangTua.setID_ORANG_TUA(rs.getInt("id"));
                orangTua.setNamaOrangTua(rs.getString("nama"));
                orangTua.setAlamatOrangTua(rs.getString("alamat"));
                orangTua.setNoTelpOrangTua(rs.getString("no_telp"));
                listorangTua.add(orangTua);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, rs);
        }
        return listorangTua;
    }

    // SAVE
    public static void save(Connection con, OrangTua orangTua) {
        PreparedStatement statement = null;
        String query = "INSERT INTO tbl_orangTua(nama, alamat, no_telp) VALUES (?, ?, ?)";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, orangTua.getNamaOrangTua());
            statement.setString(2, orangTua.getAlamatOrangTua());
            statement.setString(3, orangTua.getNoTelpOrangTua());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving orangTua: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // EDIT
    public static void edit(Connection con, OrangTua orangTua) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_orangTua SET nama = ?, alamat = ?, no_telp = ? WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setString(1, orangTua.getNamaOrangTua());
            statement.setString(2, orangTua.getAlamatOrangTua());
            statement.setString(3, orangTua.getNoTelpOrangTua());
            statement.setInt(4, orangTua.getID_ORANG_TUA());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error editing orangTua: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }

    // DELETE
    public static void delete(Connection con, OrangTua orangTua) {
        PreparedStatement statement = null;
        String query = "UPDATE tbl_orangTua SET status_aktif = 0 WHERE id = ?";

        try {
            statement = con.prepareStatement(query);
            statement.setInt(1, orangTua.getID_ORANG_TUA());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting orangTua: " + e.getMessage());
        } finally {
            ConnectionManager.close(statement);
        }
    }
}
