package com.example.pbo_sekolahminggu.controllers.transactional_data;

import com.example.pbo_sekolahminggu.beans.transactional_data.HistoriKelasAnak;
import com.example.pbo_sekolahminggu.beans.transactional_data.HistoriMengajar;
import com.example.pbo_sekolahminggu.beans.transactional_data.KelasPerTahun;
import com.example.pbo_sekolahminggu.dao.transactional_data.HistoriKelasAnakDao;
import com.example.pbo_sekolahminggu.dao.transactional_data.HistoriMengajarDao;
import com.example.pbo_sekolahminggu.dao.master_data.TahunAjaranDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import com.example.pbo_sekolahminggu.beans.master_data.TahunAjaran;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import com.example.pbo_sekolahminggu.dao.transactional_data.KelasPerTahunDao;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HistoriMengajarController implements Initializable {
    @FXML
    private AnchorPane historiMengajarAncPane;
    @FXML
    TableView<HistoriMengajar> historiMengajarTbl;
    @FXML
    TableColumn<HistoriMengajar, String> IDHistori, Nama, NIP, Kelas, TahunAjaran;
    @FXML
    ChoiceBox<TahunAjaran> tahunAjaranHistoriMengajarCb;
    @FXML
    ChoiceBox<KelasPerTahun> kelasHistoriMengajarCb;

    ObservableList<TahunAjaran> tahunAjaranList = FXCollections.observableArrayList();
    ObservableList<KelasPerTahun> dataKelas = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateKelasTable();
        try {
            fillTahunAjaranCb();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void showFilter() {
        try {
            //selected nama kelas
            KelasPerTahun selectedKelas = (KelasPerTahun) kelasHistoriMengajarCb.getSelectionModel().getSelectedItem();
            //selected tahun ajaran
//            TahunAjaran selectedTahunAjaran = (TahunAjaran) tahunAjaranHistoriKelasCb.getSelectionModel().getSelectedItem();
            // Get the ArrayList of Guru objects from the database
            ArrayList<HistoriMengajar> listHistoryKelasGuru = HistoriMengajarDao.get(ConnectionManager.getConnection(), selectedKelas.getID_KELAS_PER_TAHUN());

            if (listHistoryKelasGuru.isEmpty()) {
                alertWarning("Belum ada anak yang terdaftar di kelas ini!");
            }
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
            historiMengajarTbl.getItems().addAll(listHistoryKelasGuru);

            // Add listener for selection change
            historiMengajarTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Handle the event when a row is clicked
                    System.out.println("Row clicked: " + newSelection);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public void fillTahunAjaranCb () throws SQLException {
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


    public void filterDataKelas () {
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
        kelasHistoriMengajarCb.show();
    }


    public void populateKelasTable () {
        System.out.println("ha");
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
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void editAssign() {
        Connection con = null;
        try {
            con = ConnectionManager.getConnection();

            //pass the selected class to the DAO
            KelasPerTahun selectedKelas = kelasHistoriMengajarCb.getSelectionModel().getSelectedItem();
            HistoriMengajarDao.setSelectedClass(selectedKelas);

            loadMenuAssignKelasGuru();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(con);
        }
    }


    //function to change the window
    private void loadMenuAssignKelasGuru () {
        loadFXML("/com/example/pbo_sekolahminggu/views/transactional_data/assignHistoriMengajar.fxml");
    }

    private void loadFXML (String fxmlFile){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            // Now switch the scene or pane
            historiMengajarAncPane.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately (e.g., show error message)
        }
    }

    private void alertWarning (String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}