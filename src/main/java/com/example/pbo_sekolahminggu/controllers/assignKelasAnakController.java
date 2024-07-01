package com.example.pbo_sekolahminggu.controllers;

import com.example.pbo_sekolahminggu.beans.master.data.Anak;
import com.example.pbo_sekolahminggu.dao.transactional.data.HistoriKelasAnakDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class assignKelasAnakController implements Initializable {
    @FXML
    private Button addHistoriKelasBtn;
    @FXML
    private TableView<Anak> anakBerdasarkanTahunHistoriKelasTbl;
    @FXML
    private TableView<Anak> anakDalamKelasHistoriKelasTbl;
    @FXML
    private AnchorPane assignHistoriKelasAncPane;
    @FXML
    private Button cancelHistoriKelasBtn;
    @FXML
    private Button removeHistoriKelasBtn;
    @FXML
    private Button saveHistoriKelasBtn;

    private ObservableList<Anak> dataAnakMasuk;
    private ObservableList<Anak> dataAnakTidakMasuk;
    private Connection con;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dataAnakMasuk = FXCollections.observableArrayList();
        dataAnakTidakMasuk = FXCollections.observableArrayList();
        anakDalamKelasHistoriKelasTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        anakBerdasarkanTahunHistoriKelasTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        anakDalamKelasHistoriKelasTbl.getColumns().clear();
        //set the columns
        TableColumn<Anak, String> namaColTerdaftar = new TableColumn<>("Nama");
        namaColTerdaftar.setMinWidth(100);
        namaColTerdaftar.setCellValueFactory(new PropertyValueFactory<>("Nama"));

        TableColumn<Anak, String> nisTerdaftarCol = new TableColumn<>("NIS");
        nisTerdaftarCol.setMinWidth(60);
        nisTerdaftarCol.setCellValueFactory(new PropertyValueFactory<>("NIS"));

        anakDalamKelasHistoriKelasTbl.getColumns().addAll(namaColTerdaftar, nisTerdaftarCol);

        anakBerdasarkanTahunHistoriKelasTbl.getColumns().clear();
        TableColumn<Anak, String> namaColTidak = new TableColumn<>("Nama");
        namaColTidak.setMinWidth(100);
        namaColTidak.setCellValueFactory(new PropertyValueFactory<>("Nama"));

        TableColumn<Anak, String> nisTidakCol = new TableColumn<>("NIS");
        nisTidakCol.setMinWidth(60);
        nisTidakCol.setCellValueFactory(new PropertyValueFactory<>("NIS"));

        anakBerdasarkanTahunHistoriKelasTbl.getColumns().addAll(namaColTidak, nisTidakCol);

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
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnections();
        }
        //balik ke menu awal
        loadMenuHKA();
    }

    @FXML
    public void save() {
        try {
            if (con != null) {
                con.commit();
            }
            System.out.println("Changes saved.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            closeConnections();
        }
        //balik ke menu awal
        loadMenuHKA();
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
        Anak selectedAnak = anakBerdasarkanTahunHistoriKelasTbl.getSelectionModel().getSelectedItem();
        if (selectedAnak != null) {
            System.out.println(selectedAnak.getID_ANAK());
            HistoriKelasAnakDao.insertToClass(con, selectedAnak);
            refreshTable(con);
        } else {
            alertWarning("Silahkan pilih anak yang akan ditambahkan");
        }
    }

    @FXML
    private void removeFromClass() {
        Anak selectedAnak = anakDalamKelasHistoriKelasTbl.getSelectionModel().getSelectedItem();
        if (selectedAnak != null) {
            System.out.println(selectedAnak.getID_ANAK());
            HistoriKelasAnakDao.removeFromClass(con, selectedAnak);
            //refresh table view
            refreshTable(con);
        } else {
            alertWarning("Silahkan pilih anak yang akan dijadikan akan dihapus");
        }
    }

    private void refreshTable(Connection con) {
        //refresh data anak yang terdaftar dalam kelas
        dataAnakMasuk = FXCollections.observableArrayList(HistoriKelasAnakDao.getAllAnakKelas(con));
        anakDalamKelasHistoriKelasTbl.setItems(dataAnakMasuk);

        //refresh data anak TIDAK terdaftar dalam kelas
        dataAnakTidakMasuk = FXCollections.observableArrayList(HistoriKelasAnakDao.getAllAnakTidakKelas(con));
        anakBerdasarkanTahunHistoriKelasTbl.setItems(dataAnakTidakMasuk);
    }

    private void alertWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadMenuHKA() {
        loadFXML("/com/example/pbo_sekolahminggu/views/transactional.data/historiKelasAnak.fxml");
    }

    private void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            // Now switch the scene or pane
            assignHistoriKelasAncPane.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately (e.g., show error message)
        }
    }
}

