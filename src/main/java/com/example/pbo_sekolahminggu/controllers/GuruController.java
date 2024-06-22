package com.example.pbo_sekolahminggu.controllers;

import com.example.pbo_sekolahminggu.beans.Guru;
import com.example.pbo_sekolahminggu.dao.GuruDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class GuruController implements Initializable {
    @FXML
    private TextField namaGuruField;
    @FXML
    private TextField nipGuruField;
    @FXML
    private TextField noTelpGuruField;
    @FXML
    private TextField alamatGuruField;
    @FXML
    private TextField guruSearchField;

    // table
    @FXML
    private TableView<Guru> guruTbl;
    private ObservableList<Guru> data;
    private Guru selectedGuru = null;

    // button
    @FXML
    private Button clearGuruBtn;
    @FXML
    private Button updateGuruBtn;
    @FXML
    private Button deleteGuruBtn;
    @FXML
    private Button createGuruBtn;


    ObservableList<Guru> listGuru;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // supaya data dan UI sinkron (auto update)
        listGuru = FXCollections.observableArrayList();
        // untuk pilih row
        guruTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // kosongkan kolom dari tabel
        guruTbl.getColumns().clear();

        // inisialisasi kolom
        // Membuat kolom untuk ID_GURU
        TableColumn<Guru, Integer> idCol = new TableColumn<>("ID Guru");
        idCol.setMinWidth(100);
        idCol.setCellValueFactory(new PropertyValueFactory<>("ID_GURU"));

        // Membuat kolom untuk NamaGuru
        TableColumn<Guru, String> namaCol = new TableColumn<>("Nama Guru");
        namaCol.setMinWidth(150);
        namaCol.setCellValueFactory(new PropertyValueFactory<>("NamaGuru"));

        // Membuat kolom untuk NIP
        TableColumn<Guru, String> nipCol = new TableColumn<>("NIP");
        nipCol.setMinWidth(150);
        nipCol.setCellValueFactory(new PropertyValueFactory<>("NIP"));

        // Membuat kolom untuk NoTelp
        TableColumn<Guru, String> noTelpCol = new TableColumn<>("Nomor Telpon");
        noTelpCol.setMinWidth(150);
        noTelpCol.setCellValueFactory(new PropertyValueFactory<>("NoTelp"));

        // Membuat kolom untuk Alamat
        TableColumn<Guru, String> alamatCol = new TableColumn<>("Alamat");
        alamatCol.setMinWidth(150);
        alamatCol.setCellValueFactory(new PropertyValueFactory<>("Alamat"));

        // Menambahkan kolom ke TableView
        guruTbl.getColumns().addAll(idCol, namaCol, nipCol, noTelpCol, alamatCol);

        //Inisialisasi data
        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            listGuru = FXCollections.
                    observableList(GuruDao.getAll(con));
            for (Guru guru: listGuru) {
                System.out.println(guru);
            }
            guruTbl.setItems(listGuru);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.closeConnection(con);
        }

        // nampilin selected items
        guruTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedGuru = newSelection;
                namaGuruField.setText(selectedGuru.getNamaGuru());
                nipGuruField.setText(selectedGuru.getNIP());
                noTelpGuruField.setText(selectedGuru.getNoTelp());
                alamatGuruField.setText(selectedGuru.getAlamat());
            }
        });

    }

    // CLEAR -> text fieldnya saja saja)
    @FXML
    public void clear() {
        namaGuruField.setText("");
        nipGuruField.setText("");
        noTelpGuruField.setText("");
        alamatGuruField.setText("");

    }

    public void Search() {

        FilteredList<Guru> filter = new FilteredList<>(listGuru, e -> true);

        guruSearchField.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate(predicateEmployeeData -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (predicateEmployeeData.getNamaGuru().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateEmployeeData.getNIP().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateEmployeeData.getNoTelp().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateEmployeeData.getAlamat().toLowerCase().contains(searchKey)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<Guru> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(guruTbl.comparatorProperty());
        guruTbl.setItems(sortList);
    }

}