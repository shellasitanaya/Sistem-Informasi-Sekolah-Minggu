package com.example.pbo_sekolahminggu;


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
    private static Connection con = null;
    @FXML
    private AnchorPane assignKehadiranGuruAncPane;
    private Connection conHere = null;
    private ObservableList<Guru> dataGuruHadir;
    private ObservableList<Guru> dataGuruTidakHadir;
    private static boolean populate = false;
    private static final Logger logger = Logger.getLogger(assignKehadiranGuruController.class.getName());
    public Connection getCon() {
        return con;
    }

    public static void setCon(Connection conn) {
        assignKehadiranGuruController.con = conn;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Ensure con is not null before using it
        if (con == null) {
            logger.severe("Database connection is null!");
            return;
        }

        System.out.println("DOR");

        dataGuruHadir = FXCollections.observableArrayList();
        dataGuruTidakHadir = FXCollections.observableArrayList();
        hadirKehadiranGuruTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        belumHadirKehadiranGuruTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        hadirKehadiranGuruTbl.getColumns().clear();
        //set the columns
        TableColumn<Guru, String> namaColHadir = new TableColumn<>("Nama");
        namaColHadir.setMinWidth(100);
        namaColHadir.setCellValueFactory(new PropertyValueFactory<>("NamaGuru"));

        TableColumn<Guru, String> nisHadirCol = new TableColumn<>("NIP");
        nisHadirCol.setMinWidth(60);
        nisHadirCol.setCellValueFactory(new PropertyValueFactory<>("NIP"));

        hadirKehadiranGuruTbl.getColumns().addAll(namaColHadir, nisHadirCol);

        belumHadirKehadiranGuruTbl.getColumns().clear();
        TableColumn<Guru, String> namaColTidak = new TableColumn<>("Nama");
        namaColTidak.setMinWidth(100);
        namaColTidak.setCellValueFactory(new PropertyValueFactory<>("NamaGuru"));

        TableColumn<Guru, String> nisTidakCol = new TableColumn<>("NIP");
        nisTidakCol.setMinWidth(60);
        nisTidakCol.setCellValueFactory(new PropertyValueFactory<>("NIP"));

        belumHadirKehadiranGuruTbl.getColumns().addAll(namaColTidak, nisTidakCol);

        if (populate) {

            KehadiranGuruDao.populateTblKehadiranGuru(con);
        }
        try {
            conHere = ConnectionManager.getConnection();
            conHere.setAutoCommit(false);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error setting auto-commit to false", e);
        }

        refreshTable(con);

        populate = false;

    }


    @FXML
    public void cancel() {
        try {
            if (con != null) {
                con.rollback();
                logger.info("Main connection rollback executed.");
            }
            if (conHere != null) {
                conHere.rollback();
                logger.info("Secondary connection rollback executed.");
            }
            System.out.println("Changes cancelled");
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
            if (con != null) {
                con.commit();
                logger.info("Main connection commit executed.");
            }
            if (conHere != null) {
                conHere.commit();
                logger.info("Secondary connection commit executed.");
            }
            System.out.println("Changes saved.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error during commit", e);
        } finally {
            closeConnections();
        }
        loadMenuKehadiranGuru();
    }

    private void closeConnections() {
        try {
            if (con != null && !con.isClosed()) {
                con.setAutoCommit(true);
                con.close();
                logger.info("Main connection closed.");
            }
            if (conHere != null && !conHere.isClosed()) {
                conHere.setAutoCommit(true);
                conHere.close();
                logger.info("Secondary connection closed.");
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
            System.out.println(selectedAnak.getID_GURU());
            KehadiranGuruDao.updateHadir(conHere, selectedAnak);
            //refresh table view
            refreshTable(conHere);
        } else {
            alertWarning("Silahkan pilih anak yang akan dijadikan hadir");
        }
    }

    @FXML
    private void removeToTidakHadir() {
        Guru selectedGuru = hadirKehadiranGuruTbl.getSelectionModel().getSelectedItem();
        if (selectedGuru != null) {
            System.out.println(selectedGuru.getID_GURU());
            KehadiranGuruDao.updateTidakHadir(conHere, selectedGuru);
            //refresh table view
            refreshTable(conHere);
        } else {
            alertWarning("Silahkan pilih anak yang akan dijadikan tidak hadir");
        }
    }

    private void alertWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void loadMenuKehadiranGuru() {
        loadFXML("/com/example/pbo_sekolahminggu/kehadiranGuru.fxml");
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
        System.out.println(dataGuruHadir);

        //refresh data anak TIDAK HADIR
        dataGuruTidakHadir = FXCollections.observableArrayList(KehadiranGuruDao.getAllGuruTidakHadir(con));
        belumHadirKehadiranGuruTbl.setItems(dataGuruTidakHadir);
        System.out.println("tidak hadir:");
        System.out.println(dataGuruTidakHadir);
    }
}