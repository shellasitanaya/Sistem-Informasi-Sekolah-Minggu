package com.example.pbo_sekolahminggu.controllers;


import com.example.pbo_sekolahminggu.beans.master.data.Guru;
import com.example.pbo_sekolahminggu.dao.transactional.data.KehadiranGuruDao;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class assignKehadiranGuruController implements Initializable {

    @FXML
    Button addKehadiranGuruBtn, removeKehadiranGuruBtn, saveKehadiranGuruBtn, cancelKehadiranGuruBtn;
    @FXML
    TableView<Guru> hadirKehadiranGuruTbl;
    @FXML
    TableView<Guru> belumHadirKehadiranGuruTbl;
    @FXML
    private AnchorPane assignKehadiranGuruAncPane;
    private Connection conHere = null;
    private ObservableList<Guru> dataGuruHadir;
    private ObservableList<Guru> dataGuruTidakHadir;
    private static final Logger logger = Logger.getLogger(assignKehadiranGuruController.class.getName());
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dataGuruHadir = FXCollections.observableArrayList();
        dataGuruTidakHadir = FXCollections.observableArrayList();
        hadirKehadiranGuruTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        belumHadirKehadiranGuruTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        hadirKehadiranGuruTbl.getColumns().clear();
        //set the columns
        TableColumn<Guru, String> namaColHadir = new TableColumn<>("Nama");
        namaColHadir.setMinWidth(125);
        namaColHadir.setCellValueFactory(new PropertyValueFactory<>("NamaGuru"));

        TableColumn<Guru, String> nisHadirCol = new TableColumn<>("NIP");
        nisHadirCol.setMinWidth(95);
        nisHadirCol.setCellValueFactory(new PropertyValueFactory<>("NIP"));

        hadirKehadiranGuruTbl.getColumns().addAll(namaColHadir, nisHadirCol);

        belumHadirKehadiranGuruTbl.getColumns().clear();
        TableColumn<Guru, String> namaColTidak = new TableColumn<>("Nama");
        namaColTidak.setMinWidth(125);
        namaColTidak.setCellValueFactory(new PropertyValueFactory<>("NamaGuru"));

        TableColumn<Guru, String> nisTidakCol = new TableColumn<>("NIP");
        nisTidakCol.setMinWidth(95);
        nisTidakCol.setCellValueFactory(new PropertyValueFactory<>("NIP"));

        belumHadirKehadiranGuruTbl.getColumns().addAll(namaColTidak, nisTidakCol);

        try {
            conHere = ConnectionManager.getConnection();
            conHere.setAutoCommit(false);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error setting auto-commit to false", e);
        }

        refreshTable(conHere);
    }

    @FXML
    public void cancel() {
        try {
            if (conHere != null) {
                conHere.rollback();
                logger.info("Secondary connection rollback executed.");
            }
            System.out.println("Changes cancelled");
            dialogBox("Perubahan data dibatalkan.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error during rollback", e);
        } finally {
            closeConnections();
        }
        //balik ke menu awal
        loadMenuKehadiranGuru();
    }

    @FXML
    public void save() {
        try {
            if (conHere != null) {
                conHere.commit();
                logger.info("Connection commit executed.");
            }
            System.out.println("Changes saved.");
            dialogBox("Data berhasil diubah!");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error during commit", e);
        } finally {
            closeConnections();
        }
        loadMenuKehadiranGuru();
    }

    private void closeConnections() {
        try {
            if (conHere != null && !conHere.isClosed()) {
                conHere.setAutoCommit(true);
                conHere.close();
                logger.info("Connection closed.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error closing connections", e);
        }
    }
    // UNTUK UPDATE KEHADIRAN
    @FXML
    private void addToHadir() {
        Guru selectedAnak = belumHadirKehadiranGuruTbl.getSelectionModel().getSelectedItem();
        if (selectedAnak != null) {
            System.out.println(selectedAnak.getIdGuru());
            KehadiranGuruDao.updateHadir(conHere, selectedAnak);
            //refresh table view
            refreshTable(conHere);
        } else {
            alertWarning("Silahkan pilih pengajar kelas untuk hadir");
        }
    }

    @FXML
    private void removeToTidakHadir() {
        Guru selectedGuru = hadirKehadiranGuruTbl.getSelectionModel().getSelectedItem();
        if (selectedGuru != null) {
            System.out.println(selectedGuru.getIdGuru());
            KehadiranGuruDao.updateTidakHadir(conHere, selectedGuru);
            //refresh table view
            refreshTable(conHere);
        } else {
            alertWarning("Silahkan pilih pengajar kelas untuk tidak hadir");
        }
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

    private void loadMenuKehadiranGuru() {
        loadFXML("/com/example/pbo_sekolahminggu/views/transactional.data/kehadiranGuru.fxml");
    }

    private void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            assignKehadiranGuruAncPane.getChildren().setAll(root);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading FXML file", e);
        }
    }


    private void refreshTable(Connection con) {
        //refresh data anak HADIR
        dataGuruHadir = FXCollections.observableArrayList(KehadiranGuruDao.getAllGuruHadir(con));
        hadirKehadiranGuruTbl.setItems(dataGuruHadir);
        //refresh data anak TIDAK HADIR
        dataGuruTidakHadir = FXCollections.observableArrayList(KehadiranGuruDao.getAllGuruTidakHadir(con));
        belumHadirKehadiranGuruTbl.setItems(dataGuruTidakHadir);
    }
}