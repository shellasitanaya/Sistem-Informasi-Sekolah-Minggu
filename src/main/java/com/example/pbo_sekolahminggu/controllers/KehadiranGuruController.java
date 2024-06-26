package com.example.pbo_sekolahminggu.controllers;

import com.example.pbo_sekolahminggu.beans.KehadiranGuru;
import com.example.pbo_sekolahminggu.beans.KelasPerTahun;
import com.example.pbo_sekolahminggu.dao.KehadiranGuruDao;
import com.example.pbo_sekolahminggu.dao.KelasPerTahunDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class KehadiranGuruController implements Initializable {
    @FXML
    TableView<KehadiranGuru> kehadiranGuruTbl;
    @FXML
    TableColumn<KehadiranGuru, String> idKehadiranCol, namaCol, nipCol, kelasCol, kebaktianCol, tanggalCol, presensiCol;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateKelasTable();
    }

    public void populateKelasTable() {
        System.out.println("ha");
        try {
            // Get the ArrayList of Guru objects from the database
            ArrayList<KehadiranGuru> listKehadiranGuru = KehadiranGuruDao.getAll(ConnectionManager.getConnection());

            // Set cell value factory for each TableColumn
            idKehadiranCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getID_KEHADIRAN_GURU())));
            namaCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNama()));
            nipCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNip()));
            kelasCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            kebaktianCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getJenisKebaktian()));
            tanggalCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTanggal())));
            presensiCol.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().isPresensi() ? "Hadir" : "Tidak Hadir")));


            // Add columns to the TableView
            kehadiranGuruTbl.getColumns().clear(); // Clear existing columns
            kehadiranGuruTbl.getColumns().addAll(idKehadiranCol, namaCol, nipCol, kelasCol, kebaktianCol, tanggalCol, presensiCol);

            // Set the data to the TableView
            kehadiranGuruTbl.getItems().clear(); // Clear existing data
            kehadiranGuruTbl.getItems().addAll(listKehadiranGuru);

            // Add listener for selection change
            kehadiranGuruTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Handle the event when a row is clicked
                    System.out.println("Row clicked: " + newSelection);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}