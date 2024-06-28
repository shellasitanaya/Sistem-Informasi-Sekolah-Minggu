package com.example.pbo_sekolahminggu.controllers;

import com.example.pbo_sekolahminggu.beans.*;
import com.example.pbo_sekolahminggu.dao.*;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class KehadiranGuruController implements Initializable {
    @FXML
    TableView<KehadiranGuru> kehadiranGuruTbl;
    @FXML
    TableColumn<KehadiranGuru, String> idKehadiranCol, namaCol, nipCol, kelasCol, kebaktianCol, tanggalCol, presensiCol;
    @FXML
    ComboBox<TahunAjaran> tahunAjaranKehadiranGuruCb;
    @FXML
    ComboBox<KelasPerTahun> kelasKehadiranGuruCb;
    @FXML
    ComboBox<Kebaktian> kebaktianKehadiranGuruCb;
    @FXML
    private AnchorPane kehadiranGuruAncPane;

    ObservableList<KehadiranGuru> dataKehadiranGuru ;


    ObservableList<KelasPerTahun> dataKelas = FXCollections.observableArrayList();
    ObservableList<TahunAjaran> tahunAjaranList = FXCollections.observableArrayList();
    ObservableList<Kebaktian> dataKebaktian = FXCollections.observableArrayList();

    private Connection conn;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        populateKelasTable();
        tahunAjaranKehadiranGuruCb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TahunAjaran>() {
            @Override
            public void changed(ObservableValue<? extends TahunAjaran> observable, TahunAjaran oldValue, TahunAjaran newValue) {
                if (newValue != null) {
                    System.out.println("Selection changed to: " + newValue.toString());

                    filterDataKelas();
                    filterDataKebaktian();
                }
            }
        });

        try {
            fillTahunAjaranCb();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
//        tahunAjaranHistoriMengajarCb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TahunAjaran>() {
//            @Override
//            public void changed(ObservableValue<? extends TahunAjaran> observable, TahunAjaran oldValue, TahunAjaran newValue) {
//                if (newValue != null) {
//                    System.out.println("Selection changed to: " + newValue.toString());
//
//                    filterDataKelas();
//                }
//            }
//        });
    }


    public void filterDataKebaktian() {
        TahunAjaran tahunSelected = tahunAjaranKehadiranGuruCb.getSelectionModel().getSelectedItem();
        if (tahunSelected == null) {
            alertWarning("Silahkan pilih tahun ajaran terlebih dahulu.");
            return;
        }

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            //filtered data of kebaktian
            ArrayList<Kebaktian> filteredKebaktian = KebaktianDao.getFilteredKebaktian(con, tahunSelected);

            // Convert to ObservableList and update data Kebaktian
            ObservableList<Kebaktian> observableKebaktian= FXCollections.observableArrayList(filteredKebaktian);
            dataKebaktian.setAll(observableKebaktian);

            // Update ChoiceBox items
            kebaktianKehadiranGuruCb.setItems(dataKebaktian);

            // Set StringConverter if needed
            kebaktianKehadiranGuruCb.setConverter(new StringConverter<Kebaktian>() {
                @Override
                public String toString(Kebaktian object) {
                    return object.getJenisKebaktian() + " (" + object.getTanggal() + ")";
                }

                @Override
                public Kebaktian fromString(String string) {
                    return ((ObservableList<Kebaktian>)kebaktianKehadiranGuruCb.getItems()).stream()
                            .filter(selectedKebaktian -> string.equals(selectedKebaktian.getJenisKebaktian()))
                            .findAny()
                            .orElse(null);
                }
            });
            // Optionally, select an item in the ChoiceBox
            if (!dataKebaktian.isEmpty()) {
                kelasKehadiranGuruCb.getSelectionModel().selectFirst();
            }

            System.out.println("Filtered kebaktian loaded successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching filtered classes: " + e.getMessage(), e);
        } finally {
            ConnectionManager.close(con);
        }
//        kebaktianKehadiranGuruCb.show();
    }

    public void fillTahunAjaranCb() throws SQLException {
        try {
            // Populate tahunAjaranList from database
            tahunAjaranList.addAll(TahunAjaranDao.getAll(ConnectionManager.getConnection()));

            // Set items and converter for ChoiceBox
            tahunAjaranKehadiranGuruCb.setItems(tahunAjaranList);

            // Set up StringConverter
            tahunAjaranKehadiranGuruCb.setConverter(new StringConverter<TahunAjaran>() {
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
                KehadiranGuru selectedKehadiranGuru = kehadiranGuruTbl.getSelectionModel().getSelectedItem();
                KelasPerTahun newKelas = kelasKehadiranGuruCb.getItems().stream()
                        .filter(kelas -> kelas.getID_KELAS_PER_TAHUN() == selectedKehadiranGuru.getID_KELAS_PER_TAHUN())
                        .findFirst()
                        .orElse(null);
                kelasKehadiranGuruCb.getSelectionModel().select(newKelas);
                TahunAjaran newTahunAjaran = tahunAjaranKehadiranGuruCb.getItems().stream()
                        .filter(tahunAjaran -> tahunAjaran.getID_TAHUN_AJARAN() == selectedKehadiranGuru.getID_TAHUN_AJARAN())
                        .findFirst()
                        .orElse(null);
                tahunAjaranKehadiranGuruCb.getSelectionModel().select(newTahunAjaran);
                Kebaktian newKebaktian = kebaktianKehadiranGuruCb.getItems().stream()
                        .filter(kebaktian -> kebaktian.getID_KEBAKTIAN() == selectedKehadiranGuru.getID_KEBAKTIAN())
                        .findFirst()
                        .orElse(null);
                kebaktianKehadiranGuruCb.getSelectionModel().select(newKebaktian);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void filterDataKelas() {
        TahunAjaran tahunSelected = tahunAjaranKehadiranGuruCb.getSelectionModel().getSelectedItem();
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
            kelasKehadiranGuruCb.setItems(dataKelas);

            // Set StringConverter if needed
            kelasKehadiranGuruCb.setConverter(new StringConverter<KelasPerTahun>() {
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
                kelasKehadiranGuruCb.getSelectionModel().selectFirst();
            }

            System.out.println("Filtered classes loaded successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching filtered classes: " + e.getMessage(), e);
        } finally {
            ConnectionManager.close(con);
        }
//        kelasHistoriMengajarCb.show();
    }

    public void edit() {
        try {
            conn = ConnectionManager.getConnection();

            KelasPerTahun selectedKelas = kelasKehadiranGuruCb.getSelectionModel().getSelectedItem();
            Kebaktian selectedKebaktian = kebaktianKehadiranGuruCb.getSelectionModel().getSelectedItem();
            KehadiranGuruDao.setSelectedKelas(selectedKelas);
            KehadiranGuruDao.setSelectedKebaktian(selectedKebaktian);

            //get the data kehadiran to check if it's empty or not
            dataKehadiranGuru = FXCollections.observableArrayList(KehadiranGuruDao.getSpecial(conn, selectedKelas, selectedKebaktian));
            // Disable auto-commit
            conn.setAutoCommit(false);

            if (dataKehadiranGuru.isEmpty()) {
                KehadiranGuruDao.populateTblKehadiranGuru(this.conn);
            }
            assignKehadiranGuruController.setCon(this.conn);  //this is to pass the connection
            loadMenuAssignKehadiranGuru();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    ConnectionManager.close(conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadMenuAssignKehadiranGuru() {
        loadFXML("/com/example/pbo_sekolahminggu/assignKehadiranGuru.fxml");
    }

    private void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            // Now switch the scene or pane
            kehadiranGuruAncPane.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately (e.g., show error message)
        }
    }

    public void showFilter(){
        try {
            //selected nama kelas
            KelasPerTahun selectedKelas = (KelasPerTahun) kelasKehadiranGuruCb.getSelectionModel().getSelectedItem();
            //selected tahun ajaran
            TahunAjaran selectedTahunAjaran = (TahunAjaran) tahunAjaranKehadiranGuruCb.getSelectionModel().getSelectedItem();
            //selected kebaktian
            Kebaktian selectedKebaktian = (Kebaktian) kebaktianKehadiranGuruCb.getSelectionModel(). getSelectedItem();



            // Get the ArrayList of Guru objects from the database
            ArrayList<KehadiranGuru> listKehadiranGuru = KehadiranGuruDao.get(ConnectionManager.getConnection(), selectedKebaktian.getID_KEBAKTIAN());

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

    private void alertWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}