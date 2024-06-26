package com.example.pbo_sekolahminggu.controllers;

import com.example.pbo_sekolahminggu.beans.*;
import com.example.pbo_sekolahminggu.dao.KehadiranAnakDao;
import com.example.pbo_sekolahminggu.dao.KelasDao;
import com.example.pbo_sekolahminggu.dao.TahunAjaranDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class KehadiranAnakController implements Initializable {
    @FXML
    private Button editKehadiranAnak;
    @FXML
    private Button findKehadiranAnak;
    @FXML
    private AnchorPane kehadiranAnakAncPane;
    @FXML
    private TextField kehadiranAnakSearchField;
    @FXML
    private TableView<KehadiranAnak> kehadiranAnakTbl;
    @FXML
    private Button showData;
    @FXML
    private ChoiceBox<TahunAjaran> tahunAjaranCmbBox;
    @FXML
    private ChoiceBox<KelasPerTahun> kelasCmbBox;
    @FXML
    private ChoiceBox<Kebaktian> kebaktianCmbBox;

    ObservableList<KehadiranAnak> dataKehadiranAnak ;
    ObservableList<TahunAjaran> dataTahunAjaran;
    ObservableList<KelasPerTahun> dataKelas;
    ObservableList<Kebaktian> dataKebaktian;
    private KehadiranAnak selectedKA;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dataKehadiranAnak = FXCollections.observableArrayList();
        dataTahunAjaran = FXCollections.observableArrayList();
        dataKelas = FXCollections.observableArrayList();
        dataKebaktian = FXCollections.observableArrayList();
        kehadiranAnakTbl.getSelectionModel().setSelectionMode(
                SelectionMode.SINGLE
        );

        kehadiranAnakTbl.getColumns().clear();
        //column no
        TableColumn idCol = new TableColumn<>("No");
        idCol.setMinWidth(3);
        idCol.setCellValueFactory(new PropertyValueFactory<KehadiranAnak, Integer>("ID_KEHADIRAN_ANAK"));  //yg ini harus sama dgn attribute di beans

        //column nama anak
        TableColumn namaAnakCol = new TableColumn("Nama");
        namaAnakCol.setMinWidth(130);
        namaAnakCol.setCellValueFactory(
                new PropertyValueFactory<KehadiranAnak, String>("nama_anak"));

        TableColumn nisCOL = new TableColumn("NIS");
        nisCOL.setMinWidth(130);
        nisCOL.setCellValueFactory(
                new PropertyValueFactory<KehadiranAnak, String>("NIS"));

        TableColumn namaKelasCol = new TableColumn("Kelas");
        namaKelasCol.setMinWidth(130);
        namaKelasCol.setCellValueFactory(
                new PropertyValueFactory<KehadiranAnak, String>("kelas"));

        TableColumn taCol = new TableColumn("Tahun Ajaran");
        taCol.setMinWidth(130);
        taCol.setCellValueFactory(
                new PropertyValueFactory<KehadiranAnak, String>("tahun_ajaran"));

        TableColumn kebaktianCol = new TableColumn("Kebaktian");
        kebaktianCol.setMinWidth(130);
        kebaktianCol.setCellValueFactory(
                new PropertyValueFactory<KehadiranAnak, String>("kebaktian"));

        TableColumn tanggalCol = new TableColumn("Tanggal");
        tanggalCol.setMinWidth(130);
        tanggalCol.setCellValueFactory(
                new PropertyValueFactory<KehadiranAnak, String>("tgl_kebaktian"));

        TableColumn presensiCol = new TableColumn("Presensi");
        presensiCol.setMinWidth(130);
        presensiCol.setCellValueFactory(
                new PropertyValueFactory<KehadiranAnak, String>("Presensi"));

        kehadiranAnakTbl.getColumns().addAll(idCol, namaAnakCol, nisCOL, namaKelasCol, taCol, kebaktianCol, tanggalCol, presensiCol);


        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            dataTahunAjaran.addAll(TahunAjaranDao.getAll(con));
            //populate the tahun ajaran choice box
            tahunAjaranCmbBox.setItems(dataTahunAjaran);
            tahunAjaranCmbBox.setConverter(new StringConverter<TahunAjaran>() {
                @Override
                public String toString(TahunAjaran object) {
                    return object.getTahunAjaran();
                }
                @Override
                public TahunAjaran fromString(String string) {
                    return ((ObservableList<TahunAjaran>)tahunAjaranCmbBox.getItems()).stream()
                            .filter(selectedKelas -> string.equals(selectedKelas.getTahunAjaran()))
                            .findAny()
                            .orElse(null);
                }
            });
            tahunAjaranCmbBox.getSelectionModel().select(0);

            //populate kelas Choice box
            kelasCmbBox.setItems(dataKelas);
//            kelasCmbBox.setConverter(new StringConverter<KelasPerTahun>() {
//                @Override
//                public String toString(KelasPerTahun object) {
//                    return object.;
//                }
//
//                @Override
//                public TahunAjaran fromString(String string) {
//                    return ((ObservableList<TahunAjaran>)tahunAjaranCmbBox.getItems()).stream()
//                            .filter(selectedKelas -> string.equals(selectedKelas.getTahunAjaran()))
//                            .findAny()
//                            .orElse(null);
//                }
//            });
            tahunAjaranCmbBox.getSelectionModel().select(0);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            ConnectionManager.close(con);
        }
    }

    @FXML
    public void show() {
        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            KehadiranAnak ka = new KehadiranAnak();
            System.out.println(tahunAjaranCmbBox.getSelectionModel().getSelectedItem().getID_TAHUN_AJARAN());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            ConnectionManager.close(con);
        }
    }

    @FXML
    public void edit() {

    }

    //refresh view tabel biar terlihat perubahan
//    private void refreshTable(Connection con, ) {
//        dataKehadiranAnak = FXCollections.observableArrayList(KehadiranAnakDao.getAll(con));
//        kelasTbl.setItems(dataKelas);
//    }

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
