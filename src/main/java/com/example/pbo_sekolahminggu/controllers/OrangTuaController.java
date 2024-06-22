package com.example.pbo_sekolahminggu.controllers;

import com.example.pbo_sekolahminggu.beans.OrangTua;
import com.example.pbo_sekolahminggu.dao.OrangTuaDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class OrangTuaController implements Initializable {
    // text field
    @FXML
    private TextField namaOrangTuaField;
    @FXML
    private TextField alamatOrangTuaField;
    @FXML
    private TextField noTelpOrangTuaField;
    @FXML
    private TextField orangTuaSearchField;


    // table
    @FXML
    private TableView<OrangTua> orangTuaTbl;
    private ObservableList<OrangTua> data;
    private OrangTua selectedOrangTua = null;

    // button
    @FXML
    private Button clearOrangTuaBtn;
    @FXML
    private Button updateOrangTuaBtn;
    @FXML
    private Button deleteOrangTuaBtn;
    @FXML
    private Button createOrangTuaBtn;

    ObservableList<OrangTua> listOrangTua = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listOrangTua = FXCollections.observableArrayList();
        orangTuaTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        orangTuaTbl.getColumns().clear();

        // Initialize table columns
        TableColumn<OrangTua, Integer> idCol = new TableColumn<>("ID Orang Tua");
        idCol.setMinWidth(100);
        idCol.setCellValueFactory(new PropertyValueFactory<>("ID_ORANG_TUA"));

        TableColumn<OrangTua, String> namaCol = new TableColumn<>("Nama Orang Tua");
        namaCol.setMinWidth(150);
        namaCol.setCellValueFactory(new PropertyValueFactory<>("namaOrangTua"));

        TableColumn<OrangTua, String> alamatCol = new TableColumn<>("Alamat");
        alamatCol.setMinWidth(150);
        alamatCol.setCellValueFactory(new PropertyValueFactory<>("alamatOrangTua"));

        TableColumn<OrangTua, String> noTelpCol = new TableColumn<>("Nomor Telepon");
        noTelpCol.setMinWidth(150);
        noTelpCol.setCellValueFactory(new PropertyValueFactory<>("noTelpOrangTua"));

        orangTuaTbl.getColumns().addAll(idCol, namaCol, alamatCol, noTelpCol);

        refreshData();

        orangTuaTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedOrangTua = newSelection;
                namaOrangTuaField.setText(selectedOrangTua.getNamaOrangTua());
                alamatOrangTuaField.setText(selectedOrangTua.getAlamatOrangTua());
                noTelpOrangTuaField.setText(selectedOrangTua.getNoTelpOrangTua());
            }
        });
    }

    private void refreshData() {
        Connection connection = null;
        try {
            connection = ConnectionManager.getConnection();
            data = FXCollections.observableArrayList(OrangTuaDao.getAll(connection));
            orangTuaTbl.setItems(data);
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
        String nama = namaOrangTuaField.getText();
        String alamat = alamatOrangTuaField.getText();
        String noTelp = noTelpOrangTuaField.getText();

        if (nama.isEmpty() || alamat.isEmpty() || noTelp.isEmpty()) {
            showErrorMessage("Harap isi semua kolom.");
            return;
        }

        OrangTua orangTua = new OrangTua();
        orangTua.setNamaOrangTua(nama);
        orangTua.setAlamatOrangTua(alamat);
        orangTua.setNoTelpOrangTua(noTelp);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            OrangTuaDao.create(con, orangTua);

            listOrangTua = FXCollections.observableArrayList(OrangTuaDao.getAll(con));
            orangTuaTbl.setItems(listOrangTua);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Orang Tua berhasil ditambahkan!");
            alert.show();

            clear();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat menambahkan data orang tua: " + e.getMessage());
            alert.show();
        } finally {
            ConnectionManager.close(con);
        }
    }

    @FXML
    public void update() {
        OrangTua selected = orangTuaTbl.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showErrorMessage("Pilih kolom yang ingin diupdate.");
            return;
        }

        String nama = namaOrangTuaField.getText();
        String alamat = alamatOrangTuaField.getText();
        String noTelp = noTelpOrangTuaField.getText();

        if (nama.isEmpty() || alamat.isEmpty() || noTelp.isEmpty()) {
            showErrorMessage("Harap isi semua kolom.");
            return;
        }

        selected.setNamaOrangTua(nama);
        selected.setAlamatOrangTua(alamat);
        selected.setNoTelpOrangTua(noTelp);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            OrangTuaDao.update(con, selected);
            updateOrangTuaInList(selected);
            orangTuaTbl.refresh();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Orang Tua berhasil diupdate!");
            alert.show();

            clear();

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat mengupdate data orang tua: " + e.getMessage());
            alert.show();
        } finally {
            ConnectionManager.close(con);
        }
    }

    private void updateOrangTuaInList(OrangTua updatedOrangTua) {
        int index = listOrangTua.indexOf(selectedOrangTua);
        if (index != -1) {
            listOrangTua.set(index, updatedOrangTua);
        }
    }

    @FXML
    public void delete() {
        if (orangTuaTbl.getSelectionModel().getSelectedItems().size() != 0) {
            OrangTua selected = orangTuaTbl.getSelectionModel().getSelectedItem();
            Connection connection = null;
            try {
                connection = ConnectionManager.getConnection();
                OrangTuaDao.delete(connection, selected);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Data berhasil dihapus !");
                alert.show();

                refreshData();
                clear();
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
        namaOrangTuaField.clear();
        alamatOrangTuaField.clear();
        noTelpOrangTuaField.clear();
        selectedOrangTua = null;
    }
}