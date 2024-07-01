package com.example.pbo_sekolahminggu.controllers.master.data;

import com.example.pbo_sekolahminggu.beans.master.data.Kelas;
import com.example.pbo_sekolahminggu.dao.master.data.KelasDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class KelasController implements Initializable {
    @FXML
    AnchorPane kelasAncPane;
    @FXML
    TextField kelasSearchField, namaKelasField;
    @FXML
    Button findKelasBtn, exportKelasBtn, clearKelasBtn, updateKelasBtn, deleteKelasBtn, createKelasBtn;
    @FXML
    TableView<Kelas> kelasTbl;
    @FXML
    TableColumn idKelas, namaKelas;
    ObservableList<Kelas> dataKelas;
    int updateID = 0;
    private Kelas selectedKelas;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dataKelas = FXCollections.observableArrayList();
        kelasTbl.getSelectionModel().setSelectionMode(
                SelectionMode.SINGLE
        );

        kelasTbl.getColumns().clear();
        //column no
        TableColumn idCol = new TableColumn<>("No");
        idCol.setMinWidth(55);
        idCol.setCellValueFactory(new PropertyValueFactory<Kelas, Integer>("ID_KELAS"));  //yg ini harus sama dgn attribute di beans

        //column nama pengajar
        TableColumn namaKelasCol = new TableColumn("Nama Kelas");
        namaKelasCol.setMinWidth(240);
        namaKelasCol.setCellValueFactory(
                new PropertyValueFactory<Kelas, String>("NamaKelas"));


        kelasTbl.getColumns().addAll(idCol, namaKelasCol);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            refreshTable(con);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            ConnectionManager.close(con);
        }
    }

    @FXML
    public void create() {
        if (!checkInputAman()) { //masih ada input kosong
            alertWarning("Data masih ada yang kosong!");
        } else {
            Connection con = null;
            try {
                con = ConnectionManager.getConnection();
                Kelas kelas = new Kelas();
                kelas.setNamaKelas(namaKelasField.getText());
                KelasDao.save(con, kelas);
                refreshTable(con);
                dialogBox("Data kelas berhasil ditambah!");
                clear(); //clear all the textfield
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                ConnectionManager.close(con);
            }
        }
    }

    @FXML
    public void delete() {
        if (selectedKelas != null) {
            Connection con = null;
            try {
                con = ConnectionManager.getConnection();
                KelasDao.delete(con, selectedKelas);
                refreshTable(con);
                dialogBox("Data kelas berhasil dihapus!");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                ConnectionManager.close(con);
            }
            clear();//clear all the textfield
            selectedKelas = null;
        } else {
            alertWarning("Silahkan pilih kelas yang mau dihapus.");
        }
    }
    @FXML
    public void update() {
        if (selectedKelas != null) {
            Kelas kelas = selectedKelas;
            Connection con = null;
            try {
                kelas.setNamaKelas(namaKelasField.getText());
                con = ConnectionManager.getConnection();
                KelasDao.update(con, kelas);
                refreshTable(con);
                dialogBox("Data kelas berhasil diperbaharui!");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                ConnectionManager.close(con);
            }
            clear();//clear all the textfield
            selectedKelas = null;
        } else {
            alertWarning("Silahkan pilih kelas yang mau diperbaharui.");
        }
    }

    @FXML
    public void clear() {
        namaKelasField.clear();
    }

//    Function to check if the textfield is empty
    private boolean checkInputAman() {
        return namaKelasField.getText() != null && !namaKelasField.getText().trim().isEmpty();
    }

    @FXML
    private void handleRowClick() {
        selectedKelas = kelasTbl.getSelectionModel().getSelectedItem();
        namaKelasField.setText(selectedKelas.getNamaKelas());
    }

    //refresh view tabel biar terlihat perubahan
    private void refreshTable(Connection con) {
        dataKelas = FXCollections.observableArrayList(KelasDao.getAll(con));
        kelasTbl.setItems(dataKelas);
    }

//    ini untuk dialog button
    private void dialogBox(String message) {
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        Dialog<String> dialog = new Dialog<>();
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().add(okButtonType);
        dialog.showAndWait();
    }

    //function kalo misalnya ada textfield yang kosong, atau kelas yang mau didelete/diedit blm dipilih
    private void alertWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}