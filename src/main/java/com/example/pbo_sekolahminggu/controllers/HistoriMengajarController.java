package com.example.pbo_sekolahminggu.controllers;

import com.example.pbo_sekolahminggu.beans.HistoriMengajar;
import com.example.pbo_sekolahminggu.beans.Kelas;
import com.example.pbo_sekolahminggu.beans.KelasPerTahun;
import com.example.pbo_sekolahminggu.dao.HistoriMengajarDao;
import com.example.pbo_sekolahminggu.dao.TahunAjaranDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import com.example.pbo_sekolahminggu.beans.TahunAjaran;
import javafx.util.StringConverter;
import com.example.pbo_sekolahminggu.dao.KelasPerTahunDao;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HistoriMengajarController implements Initializable {

    @FXML
    TableView<HistoriMengajar> historiMengajarTbl;
    @FXML
    TableColumn<HistoriMengajar, String> IDHistori, Nama, NIP, Kelas, TahunAjaran;
    @FXML
    ComboBox<TahunAjaran> tahunAjaranHistoriMengajarCb;
    @FXML
    ComboBox<KelasPerTahun> kelasHistoriMengajarCb;

    ObservableList<TahunAjaran> tahunAjaranList = FXCollections.observableArrayList();
    ObservableList<KelasPerTahun> dataKelas = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateKelasTable();

        tahunAjaranHistoriMengajarCb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TahunAjaran>() {
            @Override
            public void changed(ObservableValue<? extends TahunAjaran> observable, TahunAjaran oldValue, TahunAjaran newValue) {
                if (newValue != null) {
                    System.out.println("Selection changed to: " + newValue.toString());

                    filterDataKelas();
                }
            }
        });

        try {
            fillTahunAjaranCb();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void showFilter(){
        try {
            //selected nama kelas
            KelasPerTahun selectedKelas = (KelasPerTahun) kelasHistoriMengajarCb.getSelectionModel().getSelectedItem();
            //selected tahun ajaran
            TahunAjaran selectedTahunAjaran = (TahunAjaran) tahunAjaranHistoriMengajarCb.getSelectionModel().getSelectedItem();
            // Get the ArrayList of Guru objects from the database
            ArrayList<HistoriMengajar> listHistoryMengajar = HistoriMengajarDao.get(ConnectionManager.getConnection(), selectedKelas.getID_KELAS_PER_TAHUN());

            // Set cell value factory for each TableColumn
            IDHistori.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getID_HISTORI_MENGAJAR())));
            Nama.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNamaGuru()));
            NIP.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNip()));
            Kelas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            TahunAjaran.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTahunAjaran()));

            // Add columns to the TableView
            historiMengajarTbl.getColumns().clear(); // Clear existing columns
            historiMengajarTbl.getColumns().addAll(IDHistori, Nama, NIP, Kelas, TahunAjaran);

            // Set the data to the TableView
            historiMengajarTbl.getItems().clear(); // Clear existing data
            historiMengajarTbl.getItems().addAll(listHistoryMengajar);

            // Add listener for selection change
            historiMengajarTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Handle the event when a row is clicked
                    System.out.println("Row clicked: " + newSelection);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void fillTahunAjaranCb() throws SQLException {
        try {
            // Populate tahunAjaranList from database
            tahunAjaranList.addAll(TahunAjaranDao.getAll(ConnectionManager.getConnection()));

            // Set items and converter for ChoiceBox
            tahunAjaranHistoriMengajarCb.setItems(tahunAjaranList);

            // Set up StringConverter
            tahunAjaranHistoriMengajarCb.setConverter(new StringConverter<TahunAjaran>() {
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


    public void filterDataKelas() {
        TahunAjaran tahunSelected = tahunAjaranHistoriMengajarCb.getSelectionModel().getSelectedItem();
        if (tahunSelected == null) {
            System.out.println("No TahunAjaran selected!");
            return;
        }

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();

            ArrayList<KelasPerTahun> filteredClasses = KelasPerTahunDao.getFilteredClasses(con, tahunSelected);

            // Convert to ObservableList and update dataKelas
            ObservableList<KelasPerTahun> observableClasses = FXCollections.observableArrayList(filteredClasses);
            dataKelas.setAll(observableClasses);

            // Update ChoiceBox items
            kelasHistoriMengajarCb.setItems(dataKelas);

            // Set StringConverter if needed
            kelasHistoriMengajarCb.setConverter(new StringConverter<KelasPerTahun>() {
                @Override
                public String toString(KelasPerTahun object) {
                    String paralel = (object.getKelasParalel() == null) ? "" : " " + object.getKelasParalel();
                    return object.getNamaKelas() + paralel;
                }

                @Override
                public KelasPerTahun fromString(String string) {
                    return dataKelas.stream()
                            .filter(kelas -> string.equals(kelas.getNamaKelas()))
                            .findFirst()
                            .orElse(null);
                }
            });

            // Optionally, select an item in the ChoiceBox
            if (!dataKelas.isEmpty()) {
                kelasHistoriMengajarCb.getSelectionModel().selectFirst();
            }

            System.out.println("Filtered classes loaded successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching filtered classes: " + e.getMessage(), e);
        } finally {
            ConnectionManager.close(con);
        }
//        kelasHistoriMengajarCb.show();
    }


    public void populateKelasTable() {
        try {
            // Get the ArrayList of Guru objects from the database
            ArrayList<HistoriMengajar> listHistoryMengajar = HistoriMengajarDao.getAll(ConnectionManager.getConnection());

            // Set cell value factory for each TableColumn
            IDHistori.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getID_HISTORI_MENGAJAR())));
            Nama.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNamaGuru()));
            NIP.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNip()));
            Kelas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            TahunAjaran.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTahunAjaran()));

            // Add columns to the TableView
            historiMengajarTbl.getColumns().clear(); // Clear existing columns
            historiMengajarTbl.getColumns().addAll(IDHistori, Nama, NIP, Kelas, TahunAjaran);

            // Set the data to the TableView
            historiMengajarTbl.getItems().clear(); // Clear existing data
            historiMengajarTbl.getItems().addAll(listHistoryMengajar);

            // Add listener for selection change
            historiMengajarTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Handle the event when a row is clicked
                    System.out.println("Row clicked: " + newSelection);

//                    //selected kelasPerTahun
//                    KelasPerTahun selectedKelasPerTahun = (KelasPerTahun) kelasHistoriMengajarCb.getSelectionModel().getSelectedItem();
//                    //selected tahun ajaran
//                    TahunAjaran selectedTahunAjaran = (TahunAjaran) tahunAjaranHistoriMengajarCb.getSelectionModel().getSelectedItem();
                    HistoriMengajar selectedHistoriMengajar = historiMengajarTbl.getSelectionModel().getSelectedItem();
                    KelasPerTahun newKelas = kelasHistoriMengajarCb.getItems().stream()
                            .filter(kelas -> kelas.getID_KELAS_PER_TAHUN() == selectedHistoriMengajar.getID_KELAS_PER_TAHUN())
                            .findFirst()
                            .orElse(null);
                    kelasHistoriMengajarCb.getSelectionModel().select(newKelas);
                    TahunAjaran newTahunAjaran = tahunAjaranHistoriMengajarCb.getItems().stream()
                            .filter(tahunAjaran -> tahunAjaran.getID_TAHUN_AJARAN() == selectedHistoriMengajar.getID_TAHUN_AJARAN())
                            .findFirst()
                            .orElse(null);
                    tahunAjaranHistoriMengajarCb.getSelectionModel().select(newTahunAjaran);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}