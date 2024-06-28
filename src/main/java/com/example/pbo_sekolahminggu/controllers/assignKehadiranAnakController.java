package com.example.pbo_sekolahminggu.controllers;

import com.example.pbo_sekolahminggu.beans.master_data.Anak;
import com.example.pbo_sekolahminggu.dao.transactional_data.KehadiranAnakDao;
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

public class assignKehadiranAnakController implements Initializable {

    @FXML
    private Button addKehadiranAnakBtn;

    @FXML
    private AnchorPane assignKehadiranAnakAncPane;

    @FXML
    private TableView<Anak> belumHadirKehadiranAnakTbl;

    @FXML
    private Button cancelKehadiranAnakBtn;

    @FXML
    private TableView<Anak> hadirKehadiranAnakTbl;

    @FXML
    private Button removeKehadiranAnakBtn;

    @FXML
    private Button saveKehadiranAnakBtn;

    private ObservableList<Anak> dataAnakHadir;
    private ObservableList<Anak> dataAnakTidakHadir;
    private static Connection con = null;
    private Connection conHere = null;

    private static boolean populate = false;
    private static final Logger logger = Logger.getLogger(assignKehadiranAnakController.class.getName());

    public static Connection getCon() {
        return con;
    }

    public static void setCon(Connection con) {
        assignKehadiranAnakController.con = con;
    }

    public static boolean isPopulate() {
        return populate;
    }

    public static void setPopulate(boolean populate) {
        assignKehadiranAnakController.populate = populate;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Ensure con is not null before using it
        if (con == null) {
            logger.severe("Database connection is null!");
            return;
        }

        dataAnakHadir = FXCollections.observableArrayList();
        dataAnakTidakHadir = FXCollections.observableArrayList();
        hadirKehadiranAnakTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        belumHadirKehadiranAnakTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        hadirKehadiranAnakTbl.getColumns().clear();
        //set the columns
        TableColumn<Anak, String> namaColHadir = new TableColumn<>("Nama");
        namaColHadir.setMinWidth(100);
        namaColHadir.setCellValueFactory(new PropertyValueFactory<>("Nama"));

        TableColumn<Anak, String> nisHadirCol = new TableColumn<>("NIS");
        nisHadirCol.setMinWidth(60);
        nisHadirCol.setCellValueFactory(new PropertyValueFactory<>("NIS"));

        hadirKehadiranAnakTbl.getColumns().addAll(namaColHadir, nisHadirCol);

        belumHadirKehadiranAnakTbl.getColumns().clear();
        TableColumn<Anak, String> namaColTidak = new TableColumn<>("Nama");
        namaColTidak.setMinWidth(100);
        namaColTidak.setCellValueFactory(new PropertyValueFactory<>("Nama"));

        TableColumn<Anak, String> nisTidakCol = new TableColumn<>("NIS");
        nisTidakCol.setMinWidth(60);
        nisTidakCol.setCellValueFactory(new PropertyValueFactory<>("NIS"));

        belumHadirKehadiranAnakTbl.getColumns().addAll(namaColTidak, nisTidakCol);

        if (populate) {

            KehadiranAnakDao.populateTblKehadiranAnak(con);
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
        loadMenuKehadiranAnak();
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
        loadMenuKehadiranAnak();
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
        Anak selectedAnak = belumHadirKehadiranAnakTbl.getSelectionModel().getSelectedItem();
        if (selectedAnak != null) {
            System.out.println(selectedAnak.getID_ANAK());
            KehadiranAnakDao.updateHadir(conHere, selectedAnak);
            //refresh table view
            refreshTable(conHere);
        } else {
            alertWarning("Silahkan pilih anak yang akan dijadikan hadir");
        }
    }

    @FXML
    private void removeToTidakHadir() {
        Anak selectedAnak = hadirKehadiranAnakTbl.getSelectionModel().getSelectedItem();
        if (selectedAnak != null) {
            System.out.println(selectedAnak.getID_ANAK());
            KehadiranAnakDao.updateTidakHadir(conHere, selectedAnak);
            //refresh table view
            refreshTable(conHere);
        } else {
            alertWarning("Silahkan pilih anak yang akan dijadikan tidak hadir");
        }
    }

    private void refreshTable(Connection con) {
        //refresh data anak HADIR
        dataAnakHadir = FXCollections.observableArrayList(KehadiranAnakDao.getAllAnakHadir(con));
        hadirKehadiranAnakTbl.setItems(dataAnakHadir);
        System.out.println(dataAnakHadir);

        //refresh data anak TIDAK HADIR
        dataAnakTidakHadir = FXCollections.observableArrayList(KehadiranAnakDao.getAllAnakTidakHadir(con));
        belumHadirKehadiranAnakTbl.setItems(dataAnakTidakHadir);
        System.out.println("tidak hadir:");
        System.out.println(dataAnakTidakHadir);
    }

    private void alertWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // INI UNTUK PINDAH WINDOW
    @FXML
    private void loadMenuKehadiranAnak() {
        loadFXML("/com/example/pbo_sekolahminggu/kehadiranAnak.fxml");
    }

    private void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            assignKehadiranAnakAncPane.getChildren().setAll(root);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error loading FXML file", e);
        }
    }
}
