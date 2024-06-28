package com.example.pbo_sekolahminggu.controllers;

import com.example.pbo_sekolahminggu.beans.*;
import com.example.pbo_sekolahminggu.dao.HistoriKelasAnakDao;
import com.example.pbo_sekolahminggu.dao.KehadiranAnakDao;
import com.example.pbo_sekolahminggu.dao.KelasPerTahunDao;
import com.example.pbo_sekolahminggu.dao.TahunAjaranDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class HistoriKelasAnakController implements Initializable {

    @FXML
    private Button ediHistoriKelas;
    @FXML
    private AnchorPane historiKelasAncPane;
    @FXML
    private TextField historiKelasSearchField;
    @FXML
    private TableView<HistoriKelasAnak> historiKelasTbl;
    @FXML
    private ChoiceBox<KelasPerTahun> kelasHistoriKelasCb;
    @FXML
    private Button showHistoriKelas;
    @FXML
    private ChoiceBox<TahunAjaran> tahunAjaranHistoriKelasCb;
    @FXML
    private TableColumn<HistoriKelasAnak, String> IDHistori, Nama, NIS, Kelas, TahunAjaran;
    ObservableList<HistoriKelasAnak> dataKehadiranAnak ;
    ObservableList<TahunAjaran> dataTahunAjaran = FXCollections.observableArrayList();
    ObservableList<KelasPerTahun> dataKelas = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        populateKelasTable();

        tahunAjaranHistoriKelasCb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TahunAjaran>() {
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

    @FXML
    public void showFilter(){  //buat filter data pas teken show
        try {
            //selected nama kelas
            KelasPerTahun selectedKelas = (KelasPerTahun) kelasHistoriKelasCb.getSelectionModel().getSelectedItem();
            //selected tahun ajaran
//            TahunAjaran selectedTahunAjaran = (TahunAjaran) tahunAjaranHistoriKelasCb.getSelectionModel().getSelectedItem();
            // Get the ArrayList of Guru objects from the database
            ArrayList<HistoriKelasAnak> listHistoryKelasAnak = HistoriKelasAnakDao.get(ConnectionManager.getConnection(), selectedKelas.getID_KELAS_PER_TAHUN());

            if (listHistoryKelasAnak.isEmpty()) {
                alertWarning("Belum ada anak yang terdaftar di kelas ini!");
            }
            // Set cell value factory for each TableColumn
            IDHistori.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getID_HISTORI_KELAS_ANAK())));
            Nama.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNama_anak()));
            NIS.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNis()));
            Kelas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            TahunAjaran.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTahun_ajaran()));

            // Add columns to the TableView
            historiKelasTbl.getColumns().clear(); // Clear existing columns
            historiKelasTbl.getColumns().addAll(IDHistori, Nama, NIS, Kelas, TahunAjaran);

            // Set the data to the TableView
            historiKelasTbl.getItems().clear(); // Clear existing data
            historiKelasTbl.getItems().addAll(listHistoryKelasAnak);

            // Add listener for selection change
            historiKelasTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
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
            dataTahunAjaran.addAll(TahunAjaranDao.getAll(ConnectionManager.getConnection()));

            // Set items and converter for ChoiceBox
            tahunAjaranHistoriKelasCb.setItems(dataTahunAjaran);

            // Set up StringConverter
            tahunAjaranHistoriKelasCb.setConverter(new StringConverter<TahunAjaran>() {
                @Override
                public String toString(TahunAjaran object) {
                    return object != null ? object.getTahunAjaran() : "";
                }

                @Override
                public TahunAjaran fromString(String string) {
                    return dataTahunAjaran.stream()
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
        TahunAjaran tahunSelected = tahunAjaranHistoriKelasCb.getSelectionModel().getSelectedItem();
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
            kelasHistoriKelasCb.setItems(dataKelas);

            // Set StringConverter if needed
            kelasHistoriKelasCb.setConverter(new StringConverter<KelasPerTahun>() {
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
                kelasHistoriKelasCb.getSelectionModel().selectFirst();
            }

            System.out.println("Filtered classes loaded successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching filtered classes: " + e.getMessage(), e);
        } finally {
            ConnectionManager.close(con);
        }
    }

    public void populateKelasTable() {
        try {
            // Get the ArrayList of Guru objects from the database
            ArrayList<HistoriKelasAnak> listHistoryMengajar = HistoriKelasAnakDao.getAll(ConnectionManager.getConnection());

            //initialize
            IDHistori = new TableColumn<>("ID Histori");
            Nama = new TableColumn<>("Nama");
            NIS = new TableColumn<>("NIS");
            Kelas = new TableColumn<>("Kelas");
            TahunAjaran = new TableColumn<>("Tahun Ajaran");


            // Set cell value factory for each TableColumn
            IDHistori.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getID_HISTORI_KELAS_ANAK())));
            Nama.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNama_anak()));
            NIS.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNis()));
            Kelas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            TahunAjaran.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTahun_ajaran()));

            // Add columns to the TableView
            historiKelasTbl.getColumns().clear(); // Clear existing columns
            historiKelasTbl.getColumns().addAll(IDHistori, Nama, NIS, Kelas, TahunAjaran);

            // Set the data to the TableView
            historiKelasTbl.getItems().clear(); // Clear existing data
            historiKelasTbl.getItems().addAll(listHistoryMengajar);

            // Add listener for selection change
            historiKelasTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Handle the event when a row is clicked
                    System.out.println("Row clicked: " + newSelection);

//                    //selected kelasPerTahun
//                    KelasPerTahun selectedKelasPerTahun = (KelasPerTahun) kelasHistoriMengajarCb.getSelectionModel().getSelectedItem();
//                    //selected tahun ajaran
//                    TahunAjaran selectedTahunAjaran = (TahunAjaran) tahunAjaranHistoriMengajarCb.getSelectionModel().getSelectedItem();
                    HistoriKelasAnak selectedHistoriAnak = historiKelasTbl.getSelectionModel().getSelectedItem();
                    KelasPerTahun newKelas = kelasHistoriKelasCb.getItems().stream()
                            .filter(kelas -> kelas.getID_KELAS_PER_TAHUN() == selectedHistoriAnak.getID_HISTORI_KELAS_ANAK())
                            .findFirst()
                            .orElse(null);
                    kelasHistoriKelasCb.getSelectionModel().select(newKelas);
                    TahunAjaran newTahunAjaran = tahunAjaranHistoriKelasCb.getItems().stream()
                            .filter(tahunAjaran -> tahunAjaran.getID_TAHUN_AJARAN() == selectedHistoriAnak.getID_HISTORI_KELAS_ANAK())
                            .findFirst()
                            .orElse(null);
                    tahunAjaranHistoriKelasCb.getSelectionModel().select(newTahunAjaran);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //fxml function
    @FXML
    public void editAssign() {
        Connection con = null;
        try {
            con = ConnectionManager.getConnection();

            //pass the selected class to the DAO
            KelasPerTahun selectedKelas = kelasHistoriKelasCb.getSelectionModel().getSelectedItem();
            HistoriKelasAnakDao.setSelectedClass(selectedKelas);

            loadMenuAssignKelasAnak();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(con);
        }
    }



    //function to change the window
    private void loadMenuAssignKelasAnak() {
        loadFXML("/com/example/pbo_sekolahminggu/assignHistoriKelasAnak.fxml");
    }

    private void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            // Now switch the scene or pane
            historiKelasAncPane.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately (e.g., show error message)
        }
    }

    private void alertWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

