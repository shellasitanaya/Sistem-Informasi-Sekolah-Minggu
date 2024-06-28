package com.example.pbo_sekolahminggu.controllers;

import com.example.pbo_sekolahminggu.beans.HistoriMengajar;
import com.example.pbo_sekolahminggu.beans.Kelas;
import com.example.pbo_sekolahminggu.beans.KelasPerTahun;
import com.example.pbo_sekolahminggu.beans.TahunAjaran;
import com.example.pbo_sekolahminggu.dao.HistoriMengajarDao;
import com.example.pbo_sekolahminggu.dao.KelasDao;
import com.example.pbo_sekolahminggu.dao.KelasPerTahunDao;
import com.example.pbo_sekolahminggu.dao.TahunAjaranDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class KelasPerTahunController implements Initializable {

    @FXML
    TableView<KelasPerTahun> kelasPerTahunTbl;
    @FXML
    TableColumn<KelasPerTahun, String> idKelasPerTahun, namaKelasPerTahun, namaPararelKelasPerTahun, tahunAjaranKelasPerTahun, namaRuangKelasPerTahun;
    @FXML
    ComboBox<Kelas> namaKelasPerTahunCb;
    @FXML
    ComboBox<TahunAjaran> tahunAjaranKelasPerTahunCb;
    @FXML
    TextField namaPararelKelasPerTahunField, namaRuangKelasPerTahunField;

    ObservableList<Kelas> namaKelasList = FXCollections.observableArrayList();
    ObservableList<TahunAjaran> tahunAjaranList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            fillKelasCb();
            fillTahunAjaranCb();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        populateKelasTable();
    }

    public void addKelasPerTahun(){
        KelasPerTahun kelasPerTahun = new KelasPerTahun();
        //selected nama kelas
        Kelas selectedKelas = (Kelas) namaKelasPerTahunCb.getSelectionModel().getSelectedItem();
        //selected tahun ajaran
        TahunAjaran selectedTahunAjaran = (TahunAjaran) tahunAjaranKelasPerTahunCb.getSelectionModel().getSelectedItem();
        kelasPerTahun.setID_KELAS(selectedKelas.getID_KELAS());
        kelasPerTahun.setNamaKelas(selectedKelas.getNamaKelas());
        kelasPerTahun.setKelasParalel(namaPararelKelasPerTahunField.getText());
        kelasPerTahun.setRuangKelas(namaRuangKelasPerTahunField.getText());
        kelasPerTahun.setID_TAHUN_AJARAN(selectedTahunAjaran.getID_TAHUN_AJARAN());
        kelasPerTahun.setTahunAjaran(selectedTahunAjaran.getTahunAjaran());


        // Establish database connection
        try (Connection con = ConnectionManager.getConnection()) {

            // Call save method
            KelasPerTahunDao.save(con, kelasPerTahun);

            //get id
            kelasPerTahun.setID_KELAS_PER_TAHUN(KelasPerTahunDao.getIdByProperties(con, kelasPerTahun));

            kelasPerTahunTbl.getItems().add(kelasPerTahun);
//            skinCareDao.resetSequence(con);
        } catch (Exception e) {
            e.printStackTrace(); // Handle or log the exception
        }
    }

    public void delete(){
        // Get the selected item (i.e., the row that was clicked)
        KelasPerTahun selectedKelas = kelasPerTahunTbl.getSelectionModel().getSelectedItem();

        if (selectedKelas != null) {
            try (Connection con = ConnectionManager.getConnection()) {

                // Call delete method
                KelasPerTahunDao.delete(con, selectedKelas);

                // Remove the selected item from the TableView
                kelasPerTahunTbl.getItems().remove(selectedKelas);

//                skinCareDao.resetSequence(con);
            } catch (Exception e) {
                e.printStackTrace(); // Handle or log the exception
            }
        } else {
            // No row is selected
            System.out.println("No row selected.");
        }
    }

    public void edit() {
        // Get the selected item (i.e., the row that was clicked)
        KelasPerTahun selectedKelasPerTahun  = kelasPerTahunTbl.getSelectionModel().getSelectedItem();

        //selected nama kelas
        Kelas selectedKelas = (Kelas) namaKelasPerTahunCb.getSelectionModel().getSelectedItem();
        //selected tahun ajaran
        TahunAjaran selectedTahunAjaran = (TahunAjaran) tahunAjaranKelasPerTahunCb.getSelectionModel().getSelectedItem();

        if (selectedKelas != null) {
            // Establish database connection
            try (Connection con = ConnectionManager.getConnection()) {
                // Update the selected item with the edited data
                selectedKelasPerTahun.setID_KELAS(selectedKelas.getID_KELAS());
                selectedKelasPerTahun.setNamaKelas(selectedKelas.getNamaKelas());
                selectedKelasPerTahun.setRuangKelas(namaRuangKelasPerTahunField.getText());
                selectedKelasPerTahun.setKelasParalel(namaPararelKelasPerTahunField.getText());
                selectedKelasPerTahun.setID_TAHUN_AJARAN(selectedTahunAjaran.getID_TAHUN_AJARAN());
                selectedKelasPerTahun.setTahunAjaran(selectedTahunAjaran.getTahunAjaran());


                // Call update method
                KelasPerTahunDao.edit(con, selectedKelasPerTahun);

                // Find the index of the selected item in the TableView
                int index = kelasPerTahunTbl.getItems().indexOf(selectedKelasPerTahun);
                if (index != -1) {
                    // Update the item in the TableView's items list
                    kelasPerTahunTbl.getItems().set(index, selectedKelasPerTahun);
//                    skinCareDao.resetSequence(con);
                } else {
                    System.out.println("Selected item not found in the TableView.");
                }
            } catch (Exception e) {
                e.printStackTrace(); // Handle or log the exception
            }
        } else {
            // No row is selected
            System.out.println("No row selected.");
        }
    }


    public void fillKelasCb() throws SQLException {
        try {
            // Populate tahunAjaranList from database
            namaKelasList.addAll(KelasDao.getAll(ConnectionManager.getConnection()));

            // Set items and converter for ChoiceBox
            namaKelasPerTahunCb.setItems(namaKelasList);

            // Set up StringConverter
            namaKelasPerTahunCb.setConverter(new StringConverter<Kelas>() {
                @Override
                public String toString(Kelas object) {
                    return object != null ? object.getNamaKelas() : "";
                }

                @Override
                public Kelas fromString(String string) {
                    return namaKelasList.stream()
                            .filter(ta -> ta.getNamaKelas().equals(string))
                            .findFirst()
                            .orElse(null); // Return null if not found (though you should ideally handle this case better)
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException("Error populating tahun ajaran list", e);
        }
    }

    public void fillTahunAjaranCb() throws SQLException {
        try {
            // Populate tahunAjaranList from database
            tahunAjaranList.addAll(TahunAjaranDao.getAll(ConnectionManager.getConnection()));

            // Set items and converter for ChoiceBox
            tahunAjaranKelasPerTahunCb.setItems(tahunAjaranList);

            // Set up StringConverter
            tahunAjaranKelasPerTahunCb.setConverter(new StringConverter<TahunAjaran>() {
                @Override
                public String toString(TahunAjaran object) {
                    return object != null ? object.getTahunAjaran() : "";
                }

                @Override
                public TahunAjaran fromString(String string) {
                    return tahunAjaranList.stream()
                            .filter(ta -> ta.getTahunAjaran().equals(string))
                            .findFirst()
                            .orElse(null); // Return null if not found (though you should ideally handle this case better)
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException("Error populating tahun ajaran list", e);
        }
    }

    public void populateKelasTable() {
        try {
            // Get the ArrayList of Guru objects from the database
            ArrayList<KelasPerTahun> listHistoryMengajar = KelasPerTahunDao.getAll(ConnectionManager.getConnection());

            // Set cell value factory for each TableColumn
            idKelasPerTahun.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getID_KELAS_PER_TAHUN())));
            namaKelasPerTahun.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNamaKelas()));
            namaPararelKelasPerTahun.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelasParalel()));
            tahunAjaranKelasPerTahun.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTahunAjaran()));
            namaRuangKelasPerTahun.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRuangKelas()));

            // Add columns to the TableView
            kelasPerTahunTbl.getColumns().clear(); // Clear existing columns
            kelasPerTahunTbl.getColumns().addAll(idKelasPerTahun, namaKelasPerTahun, namaPararelKelasPerTahun, tahunAjaranKelasPerTahun, namaRuangKelasPerTahun);

            // Set the data to the TableView
            kelasPerTahunTbl.getItems().clear(); // Clear existing data
            kelasPerTahunTbl.getItems().addAll(listHistoryMengajar);

            // Add listener for selection change
            kelasPerTahunTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Handle the event when a row is clicked
                    System.out.println("Row clicked: " + newSelection);
                    KelasPerTahun selectedKelasPerTahun  = kelasPerTahunTbl.getSelectionModel().getSelectedItem();
                    namaPararelKelasPerTahunField.setText(selectedKelasPerTahun.getKelasParalel());
                    namaRuangKelasPerTahunField.setText(selectedKelasPerTahun.getRuangKelas());
                    Kelas newKelas = namaKelasPerTahunCb.getItems().stream()
                            .filter(kelas -> kelas.getID_KELAS() == selectedKelasPerTahun.getID_KELAS())
                            .findFirst()
                            .orElse(null);
                    namaKelasPerTahunCb.getSelectionModel().select(newKelas);
                    TahunAjaran newTahunAjaran = tahunAjaranKelasPerTahunCb.getItems().stream()
                            .filter(tahunAjaran -> tahunAjaran.getID_TAHUN_AJARAN() == selectedKelasPerTahun.getID_TAHUN_AJARAN())
                            .findFirst()
                            .orElse(null);
                    tahunAjaranKelasPerTahunCb.getSelectionModel().select(newTahunAjaran);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clear(){
        namaKelasPerTahunCb.getSelectionModel().clearSelection();
        tahunAjaranKelasPerTahunCb.getSelectionModel().clearSelection();
        namaPararelKelasPerTahunField.clear();
        namaRuangKelasPerTahunField.clear();
    }

}