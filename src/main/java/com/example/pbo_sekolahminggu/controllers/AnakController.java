package com.example.pbo_sekolahminggu.controllers;

import com.example.pbo_sekolahminggu.beans.Anak;
import com.example.pbo_sekolahminggu.beans.Guru;
import com.example.pbo_sekolahminggu.dao.AnakDao;
import com.example.pbo_sekolahminggu.dao.GuruDao;
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

public class AnakController implements Initializable {

    // text field
    @FXML
    private TextField nomorTeleponAnakField;
    @FXML
    private TextField namaOrangTuaAnakField;
    @FXML
    private TextField namaAnakField;
    @FXML
    private TextField nisAnakField;
    @FXML
    private TextField alamatAnakField;

    // radio button jenis kelamin
    @FXML
    private RadioButton lakiAnakBtn;

    @FXML
    private RadioButton perempuanAnakBtn;

    // table
    @FXML
    private TableView<Anak> anakTbl;
    private ObservableList<Anak> data;

    // button
    @FXML
    private Button clearAnakBtn;
    @FXML
    private Button updateAnakBtn;
    @FXML
    private Button deleteAnakBtn;
    @FXML
    private Button createAnakBtn;

    ObservableList<Anak> listAnak = FXCollections.observableArrayList();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // radio button cuma boleh pilih 1
        ToggleGroup radioButtonGroup = new ToggleGroup();
        lakiAnakBtn.setToggleGroup(radioButtonGroup);
        perempuanAnakBtn.setToggleGroup(radioButtonGroup);

        listAnak = FXCollections.observableArrayList();
        anakTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        anakTbl.getColumns().clear();

        // Initialize table columns
        TableColumn<Anak, String> namaCol = new TableColumn<>("Nama Anak");
        namaCol.setMinWidth(150);
        namaCol.setCellValueFactory(new PropertyValueFactory<>("nama"));

        TableColumn<Anak, String> nisCol = new TableColumn<>("NIS");
        nisCol.setMinWidth(100);
        nisCol.setCellValueFactory(new PropertyValueFactory<>("nis"));

        TableColumn<Anak, String> alamatCol = new TableColumn<>("Alamat");
        alamatCol.setMinWidth(150);
        alamatCol.setCellValueFactory(new PropertyValueFactory<>("alamat"));

        TableColumn<Anak, String> jenisKelaminCol = new TableColumn<>("Jenis Kelamin");
        jenisKelaminCol.setMinWidth(100);
        jenisKelaminCol.setCellValueFactory(new PropertyValueFactory<>("jenisKelamin"));

        anakTbl.getColumns().addAll(namaCol, nisCol, alamatCol, jenisKelaminCol);

        refreshData();
    }

    // mengambil ulang data dari database
    private void refreshData() {
        Connection connection = null;
        try {
            connection = ConnectionManager.getConnection();
            data = FXCollections.observableArrayList(AnakDao.getAll(connection));
            anakTbl.setItems(data);
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
        String nama = namaAnakField.getText();
        String nis = nisAnakField.getText();
        String alamat = alamatAnakField.getText();
        String jenisKelamin = lakiAnakBtn.isSelected() ? "Laki-laki" : "Perempuan";

        if (nama.isEmpty() || nis.isEmpty() || alamat.isEmpty() || (!lakiAnakBtn.isSelected() && !perempuanAnakBtn.isSelected())) {
            showErrorMessage("Harap isi semua kolom.");
            return;
        }

        Anak anak = new Anak();
        anak.setNama(nama);
        anak.setNIS(nis);
        anak.setAlamat(alamat);
        anak.setJenisKelamin(jenisKelamin);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            AnakDao.create(con, anak);

            listAnak = FXCollections.observableArrayList(AnakDao.getAll(con));
            anakTbl.setItems(listAnak);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Anak berhasil ditambahkan!");
            alert.show();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat menambahkan data anak: " + e.getMessage());
            alert.show();
        } finally {
            ConnectionManager.close(con);
        }
    }

    @FXML
    public void update() {
        String nama = namaAnakField.getText();
        String nis = nisAnakField.getText();
        String alamat = alamatAnakField.getText();
        String jenisKelamin = lakiAnakBtn.isSelected() ? "Laki-laki" : "Perempuan";

        if (nama.isEmpty() || nis.isEmpty() || alamat.isEmpty() || (!lakiAnakBtn.isSelected() && !perempuanAnakBtn.isSelected())) {
            showErrorMessage("Harap isi semua kolom.");
            return;
        }

        Anak anak = new Anak();
        anak.setNama(nama);
        anak.setNIS(nis);
        anak.setAlamat(alamat);
        anak.setJenisKelamin(jenisKelamin);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            AnakDao.update(con, anak);

            listAnak = FXCollections.observableArrayList(AnakDao.getAll(con));
            anakTbl.setItems(listAnak);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Anak berhasil diupdate!");
            alert.show();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat mengupdate data anak: " + e.getMessage());
            alert.show();
        } finally {
            ConnectionManager.close(con);
        }
    }

    @FXML
    public void delete() {
        if (anakTbl.getSelectionModel().getSelectedItems().size() != 0) {
            Anak selected = anakTbl.getSelectionModel().getSelectedItem();
            Connection connection = null;
            try {
                connection = ConnectionManager.getConnection();
                AnakDao.delete(connection, selected);
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
        namaAnakField.setText("");
        nisAnakField.setText("");
        alamatAnakField.setText("");
        lakiAnakBtn.setSelected(false);
        perempuanAnakBtn.setSelected(false);
    }

}