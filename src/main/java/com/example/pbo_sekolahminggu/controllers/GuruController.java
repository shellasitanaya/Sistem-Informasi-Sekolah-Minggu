package com.example.pbo_sekolahminggu.controllers;

import com.example.pbo_sekolahminggu.beans.Guru;
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

public class GuruController implements Initializable {
    // text field
    @FXML
    private TextField txtNama;
    @FXML
    private TextField txtNIP;
    @FXML
    private TextField txtTelp;
    @FXML
    private TextField txtAlamat;

    // table
    @FXML
    private TableView<Guru> tblData;
    private ObservableList<Guru> data;

    // button
    @FXML
    private Button btnClear;
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;


    ObservableList<Guru> listGuru = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // supaya data dan UI sinkron (auto update)
        listGuru = FXCollections.observableArrayList();
        // untuk pilih row
        tblData.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // kosongkan kolom dari tabel
        tblData.getColumns().clear();

        // inisialisasi kolom
        // Membuat kolom untuk ID_GURU
        TableColumn<Guru, Integer> idCol = new TableColumn<>("ID Guru");
        idCol.setMinWidth(50);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Membuat kolom untuk NamaGuru
        TableColumn<Guru, String> namaCol = new TableColumn<>("Nama Guru");
        namaCol.setMinWidth(150);
        namaCol.setCellValueFactory(new PropertyValueFactory<>("nama"));

        // Membuat kolom untuk NIP
        TableColumn<Guru, String> nipCol = new TableColumn<>("NIP");
        nipCol.setMinWidth(100);
        nipCol.setCellValueFactory(new PropertyValueFactory<>("nip"));

        // Membuat kolom untuk NoTelp
        TableColumn<Guru, String> noTelpCol = new TableColumn<>("Nomor Telpon");
        noTelpCol.setMinWidth(100);
        noTelpCol.setCellValueFactory(new PropertyValueFactory<>("no_telp"));

        // Membuat kolom untuk Alamat
        TableColumn<Guru, String> alamatCol = new TableColumn<>("Alamat");
        alamatCol.setMinWidth(150);
        alamatCol.setCellValueFactory(new PropertyValueFactory<>("alamat"));

        // Menambahkan kolom ke TableView
        tblData.getColumns().addAll(idCol, namaCol, nipCol, noTelpCol, alamatCol);

        refreshData();

    }

    // mengambil ulang data dari database
    private void refreshData() {
        Connection connection = null;
        try {
            connection = ConnectionManager.getConnection();
            data = FXCollections.observableArrayList(GuruDao.getAll(connection));
            tblData.setItems(data);
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

    // CRUD ----------------------------------------------------
    // CREATE
    @FXML
    public void create() {
        String nama= txtNama.getText();
        String nip = txtNIP.getText();
        String noTelp = txtTelp.getText();
        String alamat = txtAlamat.getText();

        // cek input
        if (nama.isEmpty() || nip.isEmpty() || noTelp.isEmpty()|| alamat.isEmpty()) {
            showErrorMessage("Harap isi semua kolom.");
            return;
        }

        // buat objek dengan inputan datanya
        Guru guru = new Guru();
        guru.setNamaGuru(nama);
        guru.setNIP(nip);
        guru.setNoTelp(noTelp);
        guru.setAlamat(alamat);

        // connect ke database
        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            GuruDao.create(con, guru); // create

            /*
            mendapatkan dan memperbarui semua data guru dari database
            buat atau inisialisasi ObservableList baru dengan data dari database
             */
            listGuru = FXCollections.observableArrayList(GuruDao.getAll(con));
            // tampilin update-an data di tableview
            tblData.setItems(listGuru);

            /*
            opsi lain observable list:
            mempertahankan referensi ObservableList yang sudah ada tetapi ingin
            mengganti isi dari ObservableList tersebut dengan data baru dari database
            */
            // -- listGuru.setAll(GuruDao.getAll(con));

            // menampilkan pesan sukses
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Guru berhasil ditambahkan!");
            alert.show();
        } catch (SQLException e) {
            // menampilkan pesan error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat menambahkan data guru: " + e.getMessage());
            alert.show();
        } finally {
            ConnectionManager.close(con);
        }
    }

    // UPDATE
    @FXML
    public void update() {
        // mendapatkan nilai dari input fields
        String nama= txtNama.getText();
        String nip = txtNIP.getText();
        String noTelp = txtTelp.getText();
        String alamat = txtAlamat.getText();

        // cek input
        if (nama.isEmpty() || nip.isEmpty() || noTelp.isEmpty() || alamat.isEmpty()) {
            showErrorMessage("Harap isi semua kolom.");
            return;
        }

        // buat objek dengan inputan datanya
        Guru guru = new Guru();
        guru.setNamaGuru(nama);
        guru.setNIP(nip);
        guru.setNoTelp(noTelp);
        guru.setAlamat(alamat);

        // connect ke database
        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            GuruDao.update(con, guru); // update

            listGuru = FXCollections.observableArrayList(GuruDao.getAll(con));
            tblData.setItems(listGuru);

            // Menampilkan pesan sukses
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Guru berhasil diupdate!");
            alert.show();
        } catch (SQLException e) {
            // Menampilkan pesan error jika terjadi kesalahan
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat mengupdate data guru: " + e.getMessage());
            alert.show();
        } finally {
            ConnectionManager.close(con);
        }
    }

    // DELETE
    @FXML
    public void delete() {
        if (tblData.getSelectionModel().getSelectedItems().size() != 0) {
            Guru selected = tblData.getSelectionModel().getSelectedItem();
            Connection connection = null;
            try {
                connection = ConnectionManager.getConnection();
                GuruDao.delete(connection, selected);
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

    // CLEAR -> hapus semua data dari tabel
    @FXML
    public void clear() {
        listGuru.clear(); // menghapus semua item dari ObservableList
        tblData.setItems(listGuru); // memperbarui tampilan tabel
    }
}