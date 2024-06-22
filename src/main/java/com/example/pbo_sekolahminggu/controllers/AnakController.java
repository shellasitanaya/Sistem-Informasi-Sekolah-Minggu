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
    private TextField namaAnakField;
    @FXML
    private TextField nisAnakField;


    // radio button jenis kelamin
    @FXML
    private RadioButton lakiAnakBtn;

    @FXML
    private RadioButton perempuanAnakBtn;
    private ToggleGroup radioButtonGroup;

    // table
    @FXML
    private TableView<Anak> anakTbl;
    private ObservableList<Anak> data;
    private Anak selectedAnak = null;

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
        radioButtonGroup = new ToggleGroup();
        lakiAnakBtn.setToggleGroup(radioButtonGroup);
        perempuanAnakBtn.setToggleGroup(radioButtonGroup);

        listAnak = FXCollections.observableArrayList();
        anakTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        anakTbl.getColumns().clear();

        // Initialize table columns
        TableColumn<Anak, Integer> idCol = new TableColumn<>("ID Anak");
        idCol.setMinWidth(100);
        idCol.setCellValueFactory(new PropertyValueFactory<>("ID_ANAK"));

        TableColumn<Anak, String> namaCol = new TableColumn<>("Nama Anak");
        namaCol.setMinWidth(150);
        namaCol.setCellValueFactory(new PropertyValueFactory<>("Nama"));

        TableColumn<Anak, String> nisCol = new TableColumn<>("NIS");
        nisCol.setMinWidth(100);
        nisCol.setCellValueFactory(new PropertyValueFactory<>("NIS"));

        TableColumn<Anak, String> jenisKelaminCol = new TableColumn<>("Jenis Kelamin");
        jenisKelaminCol.setMinWidth(100);
        jenisKelaminCol.setCellValueFactory(new PropertyValueFactory<>("JenisKelamin"));

        anakTbl.getColumns().addAll(idCol, namaCol, nisCol, jenisKelaminCol);

        refreshData();

        // nampilin selected items
        anakTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedAnak = newSelection;
                nisAnakField.setText(selectedAnak.getNIS());
                namaAnakField.setText(selectedAnak.getNama());
                if ("Laki-laki".equalsIgnoreCase(selectedAnak.getJenisKelamin())) {
                    lakiAnakBtn.setSelected(true);
                } else if ("Perempuan".equalsIgnoreCase(selectedAnak.getJenisKelamin())) {
                    perempuanAnakBtn.setSelected(true);
                }
            }
        });


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

        RadioButton selectedGender = (RadioButton) radioButtonGroup.getSelectedToggle();
        String jenisKelamin = selectedGender != null ? selectedGender.getText() : "";

        if (nama.isEmpty() || nis.isEmpty()  || (!lakiAnakBtn.isSelected() && !perempuanAnakBtn.isSelected())) {
            showErrorMessage("Harap isi semua kolom.");
            return;
        }

        Anak anak = new Anak();
        anak.setNama(nama);
        anak.setNIS(nis);
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
        Anak selectedAnak = anakTbl.getSelectionModel().getSelectedItem();
        if (selectedAnak == null) {
            showErrorMessage("Pilih data anak yang ingin diupdate.");
            return;
        }


        String nama = namaAnakField.getText();
        String nis = nisAnakField.getText();
        String jenisKelamin = lakiAnakBtn.isSelected() ? "Laki-laki" : "Perempuan";

        if (nama.isEmpty() || nis.isEmpty() || (!lakiAnakBtn.isSelected() && !perempuanAnakBtn.isSelected())) {
            showErrorMessage("Harap isi semua kolom.");
            return;
        }

        // update selected anak object
        selectedAnak.setNama(nama);
        selectedAnak.setNIS(nis);
        selectedAnak.setJenisKelamin(jenisKelamin);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            AnakDao.update(con, selectedAnak);

            // update observable list
            updateAnakInList(selectedAnak);

            // Refresh table view
            anakTbl.refresh();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Anak berhasil diupdate!");
            alert.show();

            clear();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat mengupdate data anak: " + e.getMessage());
            alert.show();
        } finally {
            ConnectionManager.close(con);
        }
    }

    private void updateAnakInList(Anak updatedAnak) {
        int index = listAnak.indexOf(updatedAnak); // cari index selected di listAnak
        if (index != -1) { // cek kalo selected ada di list
            listAnak.set(index, updatedAnak); // update list
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
        namaAnakField.clear();
        nisAnakField.clear();
        lakiAnakBtn.setSelected(false);
        perempuanAnakBtn.setSelected(false);
    }

}