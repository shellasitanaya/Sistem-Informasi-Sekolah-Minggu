package com.example.pbo_sekolahminggu.controllers;

import com.example.pbo_sekolahminggu.beans.TahunAjaran;
import com.example.pbo_sekolahminggu.dao.TahunAjaranDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    // table
    @FXML
    private TableView<TahunAjaran> tahunAjaranTbl;
    private ObservableList<TahunAjaran> data;

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

        // Initialize table columns
        TableColumn<TahunAjaran, String> tahunCol = new TableColumn<>("Tahun Ajaran");
        tahunCol.setMinWidth(150);
        tahunCol.setCellValueFactory(new PropertyValueFactory<>("tahunAjaran"));

        tahunAjaranTbl.getColumns().addAll(tahunCol);

        refreshData();
    }

    private void refreshData() {
        Connection connection = null;
        try {
            connection = ConnectionManager.getConnection();
            data = FXCollections.observableArrayList(TahunAjaranDao.getAll(connection));
            tahunAjaranTbl.setItems(data);
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
        String tahun = tahunAjaranField.getText();

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
        String tahun = tahunAjaranField.getText();

        if (tahun.isEmpty()) {
            showErrorMessage("Harap isi tahun ajaran.");
            return;
        }

        TahunAjaran tahunAjaran = new TahunAjaran();
        tahunAjaran.setTahunAjaran(tahun);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            TahunAjaranDao.update(con, tahunAjaran);

            listTahunAjaran = FXCollections.observableArrayList(TahunAjaranDao.getAll(con));
            tahunAjaranTbl.setItems(listTahunAjaran);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Tahun Ajaran berhasil diupdate!");
            alert.show();
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

    @FXML
    public void clear() {
        tahunAjaranField.setText("");
    }
}
