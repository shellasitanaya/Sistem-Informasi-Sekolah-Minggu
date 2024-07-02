package com.example.pbo_sekolahminggu.controllers;

import com.example.pbo_sekolahminggu.beans.master.data.Guru;
import com.example.pbo_sekolahminggu.dao.transactional.data.HistoriMengajarDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class assignHistoriMengajarController implements Initializable {
    @FXML
    private Button addHistoriMengajarBtn;
    @FXML
    private TableView<Guru> addedGuruTbl;
    @FXML
    private AnchorPane assignHistoriMengajarAncPane;
    @FXML
    private Button cancelHistoriMengajarBtn;
    @FXML
    private Button removeHistoriMengajarBtn;
    @FXML
    private TableView<Guru> removedGuruTbl;
    @FXML
    private Button saveHistoriMengajarBtn;
    private ObservableList<Guru> dataGuruMasuk;
    private ObservableList<Guru> dataGuruTidakMasuk;
    private Connection con;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dataGuruMasuk = FXCollections.observableArrayList();
        dataGuruTidakMasuk = FXCollections.observableArrayList();
        addedGuruTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        removedGuruTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        addedGuruTbl.getColumns().clear();
        //set the columns
        TableColumn<Guru, String> namaColTerdaftar = new TableColumn<>("Nama");
        namaColTerdaftar.setMinWidth(125);
        namaColTerdaftar.setCellValueFactory(new PropertyValueFactory<>("namaGuru"));

        TableColumn<Guru, String> nipTerdaftarCol = new TableColumn<>("NIP");
        nipTerdaftarCol.setMinWidth(95);
        nipTerdaftarCol.setCellValueFactory(new PropertyValueFactory<>("nip"));

        addedGuruTbl.getColumns().addAll(namaColTerdaftar, nipTerdaftarCol);

        removedGuruTbl.getColumns().clear();
        TableColumn<Guru, String> namaColTidak = new TableColumn<>("Nama");
        namaColTidak.setMinWidth(125);
        namaColTidak.setCellValueFactory(new PropertyValueFactory<>("namaGuru"));

        TableColumn<Guru, String> nipTidakCol = new TableColumn<>("NIP");
        nipTidakCol.setMinWidth(95);
        nipTidakCol.setCellValueFactory(new PropertyValueFactory<>("nip"));

        removedGuruTbl.getColumns().addAll(namaColTidak, nipTidakCol);

        try {
            con = ConnectionManager.getConnection();
            con.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        refreshTable(con);
    }

    @FXML
    public void cancel() {
        try {
            if (con != null) {
                con.rollback();
            }
            System.out.println("Changes cancelled");
            dialogBox("Perubahan data dibatalkan.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnections();
        }
        //balik ke menu awal
        loadMenuHM();
    }

    @FXML
    public void save() {
        try {
            if (con != null) {
                con.commit();
            }
            System.out.println("Changes saved");
            dialogBox("Data berhasil diubah!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnections();
        }
        //balik ke menu awal
        loadMenuHM();
    }

    private void closeConnections() {
        try {
            if (con != null && !con.isClosed()) {
                con.setAutoCommit(true);
                con.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // UNTUK UPDATE KEHADIRAN
    @FXML
    private void addToClass() {
        Guru selectedGuru = removedGuruTbl.getSelectionModel().getSelectedItem();
        if (selectedGuru != null) {
            HistoriMengajarDao.insertToClass(con, selectedGuru);
            refreshTable(con);
        } else {
            alertWarning("Silahkan pilih guru yang akan ditambahkan");
        }
    }

    @FXML
    private void removeFromClass() {
        Guru selectedGuru = addedGuruTbl.getSelectionModel().getSelectedItem();
        if (selectedGuru != null) {
            System.out.println(selectedGuru.getIdGuru());
            HistoriMengajarDao.removeFromClass(con, selectedGuru);
            //refresh table view
            refreshTable(con);
        } else {
            alertWarning("Silahkan pilih guru yang akan dihapus");
        }
    }

    private void refreshTable(Connection con) {
        //refresh data anak yang terdaftar dalam kelas
        dataGuruMasuk = FXCollections.observableArrayList(HistoriMengajarDao.getAllGuruKelas(con));
        addedGuruTbl.setItems(dataGuruMasuk);

        //refresh data guru TIDAK terdaftar
        dataGuruTidakMasuk = FXCollections.observableArrayList(HistoriMengajarDao.getAllGuruTidakKelas(con));
        removedGuruTbl.setItems(dataGuruTidakMasuk);
    }

    private void alertWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void dialogBox(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadMenuHM() {
        loadFXML("/com/example/pbo_sekolahminggu/views/transactional.data/historiMengajar.fxml");
    }

    private void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            // Now switch the scene or pane
            assignHistoriMengajarAncPane.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately (e.g., show error message)
        }
    }
}
