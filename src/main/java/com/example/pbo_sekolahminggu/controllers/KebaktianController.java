package com.example.pbo_sekolahminggu.controllers;

import com.example.pbo_sekolahminggu.beans.Kebaktian;
import com.example.pbo_sekolahminggu.dao.KebaktianDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class KebaktianController implements Initializable {
    // text field
    @FXML
    private TextField jenisKebaktianField;
    @FXML
    private DatePicker tanggalKebaktianPicker;

    // table
    @FXML
    private TableView<Kebaktian> kebaktianTbl;
    private ObservableList<Kebaktian> data;

    // button
    @FXML
    private Button clearKebaktianBtn;
    @FXML
    private Button updateKebaktianBtn;
    @FXML
    private Button deleteKebaktianBtn;
    @FXML
    private Button createKebaktianBtn;

    ObservableList<Kebaktian> listKebaktian = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listKebaktian = FXCollections.observableArrayList();
        kebaktianTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        kebaktianTbl.getColumns().clear();

        // Initialize table columns
        TableColumn<Kebaktian, String> jenisCol = new TableColumn<>("Jenis Kebaktian");
        jenisCol.setMinWidth(150);
        jenisCol.setCellValueFactory(new PropertyValueFactory<>("jenisKebaktian"));

        TableColumn<Kebaktian, Date> tanggalCol = new TableColumn<>("Tanggal");
        tanggalCol.setMinWidth(150);
        tanggalCol.setCellValueFactory(new PropertyValueFactory<>("tanggal"));

        kebaktianTbl.getColumns().addAll(jenisCol, tanggalCol);

        refreshData();
    }

    private void refreshData() {
        Connection connection = null;
        try {
            connection = ConnectionManager.getConnection();
            data = FXCollections.observableArrayList(KebaktianDao.getAll(connection));
            kebaktianTbl.setItems(data);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(connection);
        }
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void create() {
        String jenis = jenisKebaktianField.getText();
        Date tanggal = Date.valueOf(tanggalKebaktianPicker.getValue());

        if (jenis.isEmpty() || tanggal == null) {
            showErrorMessage("Harap isi semua kolom.");
            return;
        }

        Kebaktian kebaktian = new Kebaktian();
        kebaktian.setJenisKebaktian(jenis);
        kebaktian.setTanggal(tanggal);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            KebaktianDao.create(con, kebaktian);

            listKebaktian = FXCollections.observableArrayList(KebaktianDao.getAll(con));
            kebaktianTbl.setItems(listKebaktian);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Kebaktian berhasil ditambahkan!");
            alert.show();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat menambahkan data kebaktian: " + e.getMessage());
            alert.show();
        } finally {
            ConnectionManager.close(con);
        }
    }

    @FXML
    public void update() {
        String jenis = jenisKebaktianField.getText();
        Date tanggal = Date.valueOf(tanggalKebaktianPicker.getValue());

        if (jenis.isEmpty() || tanggal == null) {
            showErrorMessage("Harap isi semua kolom.");
            return;
        }

        Kebaktian kebaktian = new Kebaktian();
        kebaktian.setJenisKebaktian(jenis);
        kebaktian.setTanggal(tanggal);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            KebaktianDao.create(con, kebaktian);

            listKebaktian = FXCollections.observableArrayList(KebaktianDao.getAll(con));
            kebaktianTbl.setItems(listKebaktian);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Kebaktian berhasil diupdate!");
            alert.show();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat mengupdate data kebaktian: " + e.getMessage());
            alert.show();
        } finally {
            ConnectionManager.close(con);
        }
    }

    @FXML
    public void delete() {
        if (kebaktianTbl.getSelectionModel().getSelectedItems().size() != 0) {
            Kebaktian selected = kebaktianTbl.getSelectionModel().getSelectedItem();
            Connection connection = null;
            try {
                connection = ConnectionManager.getConnection();
                KebaktianDao.delete(connection, selected);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Data berhasil dihapus !");
                alert.show();

                refreshData();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                ConnectionManager.close(connection);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Tidak ada data yang dipilih !");
            alert.show();
        }
    }

    @FXML
    public void clear() {
        jenisKebaktianField.setText("");
        tanggalKebaktianPicker.setValue(null);
    }
}
