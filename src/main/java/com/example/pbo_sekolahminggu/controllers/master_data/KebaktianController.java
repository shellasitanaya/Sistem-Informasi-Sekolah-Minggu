package com.example.pbo_sekolahminggu.controllers.master_data;

import com.example.pbo_sekolahminggu.beans.master_data.Kebaktian;
import com.example.pbo_sekolahminggu.dao.master_data.KebaktianDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class KebaktianController implements Initializable {
    // text field
    @FXML
    private TextField jenisKebaktianField;
    @FXML
    private TextField kebaktianSearchField;
    @FXML
    private DatePicker tanggalKebaktianPicker;

    // table
    @FXML
    private TableView<Kebaktian> kebaktianTbl;
    private ObservableList<Kebaktian> data;
    private Kebaktian selectedKebaktian = null;

    // button
    @FXML
    private Button clearKebaktianBtn;
    @FXML
    private Button updateKebaktianBtn;
    @FXML
    private Button deleteKebaktianBtn;
    @FXML
    private Button createKebaktianBtn;

    ObservableList<Kebaktian> listKebaktian = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listKebaktian = FXCollections.observableArrayList();
        kebaktianTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        kebaktianTbl.getColumns().clear();

        // Inisialisasi table columns
        TableColumn<Kebaktian, Integer> idCol = new TableColumn<>("ID Kebaktian");
        idCol.setMinWidth(100);
        idCol.setCellValueFactory(new PropertyValueFactory<>("ID_KEBAKTIAN"));

        TableColumn<Kebaktian, String> jenisCol = new TableColumn<>("Jenis Kebaktian");
        jenisCol.setMinWidth(150);
        jenisCol.setCellValueFactory(new PropertyValueFactory<>("jenisKebaktian"));

        TableColumn<Kebaktian, Date> tanggalCol = new TableColumn<>("Tanggal");
        tanggalCol.setMinWidth(150);
        tanggalCol.setCellValueFactory(new PropertyValueFactory<>("tanggal"));

        kebaktianTbl.getColumns().addAll(idCol, jenisCol, tanggalCol);

        refreshData();

        kebaktianTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedKebaktian = newSelection;
                jenisKebaktianField.setText(selectedKebaktian.getJenisKebaktian());
                tanggalKebaktianPicker.setValue(selectedKebaktian.getTanggal().toLocalDate());
            }
        });
    }

    private void refreshData() {
        Connection connection = null;
        try {
            connection = ConnectionManager.getConnection();
            data = FXCollections.observableArrayList(KebaktianDao.getAll(connection));
            listKebaktian.setAll(data);
            kebaktianTbl.setItems(listKebaktian);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(connection);
        }
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void create() {
        String jenis = jenisKebaktianField.getText();
        Date tanggal = Date.valueOf(tanggalKebaktianPicker.getValue());

        if (jenis.isEmpty() || tanggal == null) {
            showErrorMessage("Harap isi semua kolom.");
            return;
        }

        Kebaktian kebaktian = new Kebaktian();
        kebaktian.setJenisKebaktian(jenis);
        kebaktian.setTanggal(tanggal);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            KebaktianDao.create(con, kebaktian);

            listKebaktian = FXCollections.observableArrayList(KebaktianDao.getAll(con));
            kebaktianTbl.setItems(listKebaktian);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Kebaktian berhasil ditambahkan!");
            alert.show();

            clear();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat menambahkan data kebaktian: " + e.getMessage());
            alert.show();
        } finally {
            ConnectionManager.close(con);
        }
    }

    @FXML
    public void update() {
        Kebaktian selected = kebaktianTbl.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorMessage("Pilih kolom yang ingin diupdate.");
            return;
        }

        String jenis = jenisKebaktianField.getText();
        Date tanggal = Date.valueOf(tanggalKebaktianPicker.getValue());

        if (jenis.isEmpty() || tanggal == null) {
            showErrorMessage("Harap isi semua kolom.");
            return;
        }

        selected.setJenisKebaktian(jenis);
        selected.setTanggal(tanggal);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            KebaktianDao.update(con, selected);

            updateKebaktianInList(selected);

            kebaktianTbl.refresh();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Kebaktian berhasil diupdate!");
            alert.show();

            clear();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat mengupdate data kebaktian: " + e.getMessage());
            alert.show();

        } finally {
            ConnectionManager.close(con);
        }
    }

    private void updateKebaktianInList(Kebaktian updatedKebaktian) {
        int index = listKebaktian.indexOf(selectedKebaktian);
        if (index != -1) {
            listKebaktian.set(index, updatedKebaktian);
        }
    }

    @FXML
    public void delete() {
        if (kebaktianTbl.getSelectionModel().getSelectedItems().size() != 0) {
            Kebaktian selected = kebaktianTbl.getSelectionModel().getSelectedItem();
            Connection connection = null;
            try {
                connection = ConnectionManager.getConnection();
                KebaktianDao.delete(connection, selected);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Data berhasil dihapus !");
                alert.show();

                refreshData();
                clear();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                ConnectionManager.close(connection);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Tidak ada data yang dipilih !");
            alert.show();
        }
    }

    @FXML
    public void clear() {
        jenisKebaktianField.clear();
        tanggalKebaktianPicker.setValue(null);
        selectedKebaktian = null;
    }

    public String formatDateToString(LocalDate date) {
        if (date == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    @FXML
    public void Search() {
        FilteredList<Kebaktian> filter = new FilteredList<>(listKebaktian, e -> true);

        kebaktianSearchField.textProperty().addListener((Observable, oldValue, newValue) -> {
            filter.setPredicate(predicateKebaktianData -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (predicateKebaktianData.getJenisKebaktian().toLowerCase().contains(searchKey)) {
                    return true;
                }
                String tanggalString = predicateKebaktianData.getTanggal().toString();
                if (tanggalString.toLowerCase().contains(searchKey)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Kebaktian> sortedData = new SortedList<>(filter);
        sortedData.comparatorProperty().bind(kebaktianTbl.comparatorProperty());
        kebaktianTbl.setItems(sortedData);
    }
}
