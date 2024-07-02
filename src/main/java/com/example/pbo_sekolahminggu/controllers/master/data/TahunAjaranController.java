package com.example.pbo_sekolahminggu.controllers.master.data;

import com.example.pbo_sekolahminggu.beans.master.data.TahunAjaran;
import com.example.pbo_sekolahminggu.dao.master.data.TahunAjaranDao;
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
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TahunAjaranController implements Initializable {
    // text field
    @FXML
    private TextField tahunAjaranField;
    @FXML
    private TextField tahunAjaranSearchField;

    // table
    @FXML
    private TableView<TahunAjaran> tahunAjaranTbl;
    private ObservableList<TahunAjaran> data;
    private TahunAjaran selectedTahunAjaran = null;

    // button
    @FXML
    private Button clearTahunAjaranBtn;
    @FXML
    private Button updateTahunAjaranBtn;
    @FXML
    private Button deleteTahunAjaranBtn;
    @FXML
    private Button createTahunAjaranBtn;

    ObservableList<TahunAjaran> listTahunAjaran = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listTahunAjaran = FXCollections.observableArrayList();
        tahunAjaranTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tahunAjaranTbl.getColumns().clear();

        // inisialisasi table columns
        TableColumn<TahunAjaran, Integer> idCol = new TableColumn<>("ID Tahun Ajaran");
        idCol.setMinWidth(100);
        idCol.setCellValueFactory(new PropertyValueFactory<>("ID_TAHUN_AJARAN"));

        TableColumn<TahunAjaran, String> tahunCol = new TableColumn<>("Tahun Ajaran");
        tahunCol.setMinWidth(235);
        tahunCol.setCellValueFactory(new PropertyValueFactory<>("tahunAjaran"));

        tahunAjaranTbl.getColumns().addAll(idCol, tahunCol);

        refreshData();

        tahunAjaranTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedTahunAjaran = newSelection;
                tahunAjaranField.setText(selectedTahunAjaran.getTahunAjaran());
            }
        });
    }

    private void refreshData() {
        Connection connection = null;
        try {
            connection = ConnectionManager.getConnection();
            data = FXCollections.observableArrayList(TahunAjaranDao.getAll(connection));
            listTahunAjaran.setAll(data);
            tahunAjaranTbl.setItems(listTahunAjaran);
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

    //CRUD
    @FXML
    public void create() {
        String tahun = tahunAjaranField.getText().trim();

        if (tahun.isEmpty()) {
            showErrorMessage("Harap isi tahun ajaran.");
            return;
        }

        TahunAjaran tahunAjaran = new TahunAjaran();
        tahunAjaran.setTahunAjaran(tahun);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            TahunAjaranDao.create(con, tahunAjaran);

            listTahunAjaran = FXCollections.observableArrayList(TahunAjaranDao.getAll(con));
            tahunAjaranTbl.setItems(listTahunAjaran);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Tahun Ajaran berhasil ditambahkan!");
            alert.show();
            clear();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat menambahkan data tahun ajaran: " + e.getMessage());
            alert.show();
        } finally {
            ConnectionManager.close(con);
        }
    }

    @FXML
    public void update() {
        TahunAjaran selected = tahunAjaranTbl.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showErrorMessage("Pilih tahun ajaran yang ingin diupdate.");
            return;
        }

        String tahunAjaran = tahunAjaranField.getText().trim();

        if (tahunAjaran.isEmpty()) {
            showErrorMessage("Harap isi tahun ajaran.");
            return;
        }
        selected.setTahunAjaran(tahunAjaran);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            TahunAjaranDao.update(con, selected);

            updateTahunAjaranInList(selected);

            tahunAjaranTbl.refresh();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Tahun Ajaran berhasil diupdate!");
            alert.show();

            clear();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat mengupdate data tahun ajaran: " + e.getMessage());
            alert.show();
        } finally {
            ConnectionManager.close(con);
        }
    }

    @FXML
    public void delete() {
        if (tahunAjaranTbl.getSelectionModel().getSelectedItems().size() != 0) {
            TahunAjaran selected = tahunAjaranTbl.getSelectionModel().getSelectedItem();
            Connection connection = null;
            try {
                connection = ConnectionManager.getConnection();
                TahunAjaranDao.delete(connection, selected);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Data berhasil dihapus !");
                alert.show();

                refreshData();
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

    private void updateTahunAjaranInList(TahunAjaran updatedTahunAjaran) {
        int index = listTahunAjaran.indexOf(selectedTahunAjaran);
        if (index != -1) {
            listTahunAjaran.set(index, updatedTahunAjaran);
        }
    }

    @FXML
    public void clear() {
        tahunAjaranField.clear();
        selectedTahunAjaran = null;
    }

    @FXML
    public void Search() {
        FilteredList<TahunAjaran> filteredData = new FilteredList<>(listTahunAjaran, e -> true);

        tahunAjaranSearchField.textProperty().addListener((Observable, oldValue, newValue) -> {
            filteredData.setPredicate(tahunAjaran -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (tahunAjaran.getTahunAjaran().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<TahunAjaran> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tahunAjaranTbl.comparatorProperty());
        tahunAjaranTbl.setItems(sortedData);
    }
}
