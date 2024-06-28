package com.example.pbo_sekolahminggu.controllers;

import com.example.pbo_sekolahminggu.beans.HistoriMengajar;
import com.example.pbo_sekolahminggu.beans.Kelas;
import com.example.pbo_sekolahminggu.beans.KelasPerTahun;
import com.example.pbo_sekolahminggu.beans.TahunAjaran;
import com.example.pbo_sekolahminggu.dao.HistoriMengajarDao;
import com.example.pbo_sekolahminggu.dao.KelasPerTahunDao;
import com.example.pbo_sekolahminggu.dao.TahunAjaranDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class KelasPerTahunController implements Initializable {

    @FXML
    TableView<KelasPerTahun> kelasPerTahunTbl;
    @FXML
    TableColumn<KelasPerTahun, String> idKelasPerTahun, namaKelasPerTahun, namaPararelKelasPerTahun, tahunAjaranKelasPerTahun, namaRuangKelasPerTahun;
//    @FXML
//    ChoiceBox<Kelas> namaKelasPerTahunCb;
//    ChoiceBox<KelasP>
    ObservableList<TahunAjaran> namaKelasList = FXCollections.observableArrayList();
    ObservableList<TahunAjaran> kelasPararelList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateKelasTable();
    }

//    public void fillKelasCb() throws SQLException {
//        try {
//            // Populate tahunAjaranList from database
//            namaKelasList.addAll(TahunAjaranDao.getAll(ConnectionManager.getConnection()));
//
//            // Set items and converter for ChoiceBox
//            tahunAjaranHistoriMengajarCb.setItems(tahunAjaranList);
//
//            // Set up StringConverter
//            tahunAjaranHistoriMengajarCb.setConverter(new StringConverter<TahunAjaran>() {
//                @Override
//                public String toString(TahunAjaran object) {
//                    return object != null ? object.getTahunAjaran() : "";
//                }
//
//                @Override
//                public TahunAjaran fromString(String string) {
//                    return tahunAjaranList.stream()
//                            .filter(ta -> ta.getTahunAjaran().equals(string))
//                            .findFirst()
//                            .orElse(null); // Return null if not found (though you should ideally handle this case better)
//                }
//            });
//        } catch (SQLException e) {
//            throw new RuntimeException("Error populating tahun ajaran list", e);
//        }
//    }

    public void populateKelasTable() {
        System.out.println("ha");
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
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}