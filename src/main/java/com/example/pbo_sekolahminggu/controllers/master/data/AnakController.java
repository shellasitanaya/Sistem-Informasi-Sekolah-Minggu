package com.example.pbo_sekolahminggu.controllers.master.data;

import com.example.pbo_sekolahminggu.beans.master.data.Anak;
import com.example.pbo_sekolahminggu.dao.master.data.AnakDao;
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

public class AnakController implements Initializable {
    // text field
    @FXML
    private TextField namaAnakField;
    @FXML
    private TextField nisAnakField;
    @FXML
    private TextField namaOrangTuaField;
    @FXML
    private TextField nomorTeleponField;
    @FXML
    private TextField alamatOrangTuaField;
    @FXML
    private TextField anakSearchField;



    // radio button jenis kelamin
    @FXML
    private RadioButton lakiAnakBtn;
    @FXML
    private RadioButton perempuanAnakBtn;
    private ToggleGroup radioButtonGroup;

    // table
    @FXML
    private TableView<Anak> anakTbl;
    private ObservableList<Anak> data;
    private Anak selectedAnak = null;

    // button
    @FXML
    private Button clearAnakBtn;
    @FXML
    private Button updateAnakBtn;
    @FXML
    private Button deleteAnakBtn;
    @FXML
    private Button createAnakBtn;

    ObservableList<Anak> listAnak = FXCollections.observableArrayList();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // radio button cuma boleh pilih 1
        radioButtonGroup = new ToggleGroup();
        lakiAnakBtn.setToggleGroup(radioButtonGroup);
        perempuanAnakBtn.setToggleGroup(radioButtonGroup);

        listAnak = FXCollections.observableArrayList();
        anakTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        anakTbl.getColumns().clear();

        // Initialize table columns
        TableColumn<Anak, Integer> idCol = new TableColumn<>("ID Anak");
        idCol.setMinWidth(55);
        idCol.setCellValueFactory(new PropertyValueFactory<>("ID_ANAK"));

        TableColumn<Anak, String> namaCol = new TableColumn<>("Nama Anak");
        namaCol.setMinWidth(145);
        namaCol.setCellValueFactory(new PropertyValueFactory<>("Nama"));

        TableColumn<Anak, String> nisCol = new TableColumn<>("NIS");
        nisCol.setMinWidth(115);
        nisCol.setCellValueFactory(new PropertyValueFactory<>("NIS"));

        TableColumn<Anak, String> jenisKelaminCol = new TableColumn<>("Jenis Kelamin");
        jenisKelaminCol.setMinWidth(85);
        jenisKelaminCol.setCellValueFactory(new PropertyValueFactory<>("JenisKelamin"));

        TableColumn<Anak, String> namaOrangTuaCol = new TableColumn<>("Nama Orang Tua");
        namaOrangTuaCol.setMinWidth(145);
        namaOrangTuaCol.setCellValueFactory(new PropertyValueFactory<>("namaOrangTua"));

        TableColumn<Anak, String> noTelpOrangTuaCol = new TableColumn<>("No Telp Orang Tua");
        noTelpOrangTuaCol.setMinWidth(120);
        noTelpOrangTuaCol.setCellValueFactory(new PropertyValueFactory<>("noTelpOrangTua"));

        TableColumn<Anak, String> alamatOrangTuaCol = new TableColumn<>("Alamat Orang Tua");
        alamatOrangTuaCol.setMinWidth(152);
        alamatOrangTuaCol.setCellValueFactory(new PropertyValueFactory<>("alamatOrangTua"));

        anakTbl.getColumns().addAll(idCol, namaCol, nisCol, jenisKelaminCol, namaOrangTuaCol, noTelpOrangTuaCol, alamatOrangTuaCol);

        refreshData();

        anakTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedAnak = newSelection;
                namaAnakField.setText(selectedAnak.getNama());
                nisAnakField.setText(selectedAnak.getNis());
                alamatOrangTuaField.setText(selectedAnak.getAlamatOrangTua());
                namaOrangTuaField.setText(selectedAnak.getNamaOrangTua());
                nomorTeleponField.setText(selectedAnak.getNoTelpOrangTua());
                if ("male".equalsIgnoreCase(selectedAnak.getJenisKelamin())) {
                    lakiAnakBtn.setSelected(true);
                } else if ("female".equalsIgnoreCase(selectedAnak.getJenisKelamin())) {
                    perempuanAnakBtn.setSelected(true);
                }
            }
        });


        // nampilin selected items



    }

    // mengambil ulang data dari database
    private void refreshData() {
        Connection connection = null;
        try {
            connection = ConnectionManager.getConnection();
            data = FXCollections.observableArrayList(AnakDao.getAll(connection));
            listAnak.setAll(data);
            anakTbl.setItems(listAnak);
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
        String nama = namaAnakField.getText();
        String nis = nisAnakField.getText();
        String alamat = alamatOrangTuaField.getText();
        String nama_orang_tua = namaOrangTuaField.getText();
        String no_telp = nomorTeleponField.getText();

        RadioButton selectedGender = (RadioButton) radioButtonGroup.getSelectedToggle();
        String jenisKelamin = selectedGender != null ? (selectedGender.getText().equalsIgnoreCase("Male") ? "male" : "female") : "";
        if (nama.isEmpty() || nis.isEmpty() || alamat.isEmpty() || nama_orang_tua.isEmpty() || no_telp.isEmpty()
                || (!lakiAnakBtn.isSelected() && !perempuanAnakBtn.isSelected())) {
            showErrorMessage("Harap isi semua kolom.");
            return;
        }

        Anak anak = new Anak();
        anak.setNama(nama);
        anak.setNis(nis);
        anak.setJenisKelamin(jenisKelamin);
        anak.setNoTelpOrangTua(no_telp);
        anak.setNamaOrangTua(nama_orang_tua);
        anak.setAlamatOrangTua(alamat);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            AnakDao.create(con, anak);

            listAnak = FXCollections.observableArrayList(AnakDao.getAll(con));
            anakTbl.setItems(listAnak);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Anak berhasil ditambahkan!");
            alert.show();

            clear();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat menambahkan data anak: " + e.getMessage());
            alert.show();
        } finally {
            ConnectionManager.close(con);
        }
    }

    @FXML
    public void update() {
        Anak selectedAnak = anakTbl.getSelectionModel().getSelectedItem();
        if (selectedAnak == null) {
            showErrorMessage("Pilih data anak yang ingin diupdate.");
            return;
        }

        String nama = namaAnakField.getText();
        String nis = nisAnakField.getText();
        String jenisKelamin = lakiAnakBtn.isSelected() ? "male" : "female";
        String alamat = alamatOrangTuaField.getText();
        String nama_ortu = namaOrangTuaField.getText();
        String no_telp = nomorTeleponField.getText();

        if (nama.isEmpty() || nis.isEmpty() || alamat.isEmpty() || nama_ortu.isEmpty() || no_telp.isEmpty()
                || (!lakiAnakBtn.isSelected() && !perempuanAnakBtn.isSelected())) {
            showErrorMessage("Harap isi semua kolom.");
            return;
        }

        // update selected anak object
        selectedAnak.setNama(nama);
        selectedAnak.setNis(nis);
        selectedAnak.setJenisKelamin(jenisKelamin);
        selectedAnak.setAlamatOrangTua(alamat);
        selectedAnak.setNoTelpOrangTua(no_telp);
        selectedAnak.setNamaOrangTua(nama_ortu);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            AnakDao.update(con, selectedAnak);

            // update observable list
            updateAnakInList(selectedAnak);

            // Refresh table view
            anakTbl.refresh();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Anak berhasil diupdate!");
            alert.show();

            clear();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat mengupdate data anak: " + e.getMessage());
            alert.show();
        } finally {
            ConnectionManager.close(con);
        }
    }

    private void updateAnakInList(Anak updatedAnak) {
        int index = listAnak.indexOf(updatedAnak); // cari index selected di listAnak
        if (index != -1) { // cek kalo selected ada di list
            listAnak.set(index, updatedAnak); // update list
        }
    }

    @FXML
    public void delete() {
        if (anakTbl.getSelectionModel().getSelectedItems().size() != 0) {
            Anak selected = anakTbl.getSelectionModel().getSelectedItem();
            Connection connection = null;
            try {
                connection = ConnectionManager.getConnection();
                AnakDao.delete(connection, selected);
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
        namaAnakField.clear();
        nisAnakField.clear();
        namaOrangTuaField.clear();
        alamatOrangTuaField.clear();
        nomorTeleponField.clear();
        lakiAnakBtn.setSelected(false);
        perempuanAnakBtn.setSelected(false);
    }

    public void Search() {
        FilteredList<Anak> filter = new FilteredList<>(listAnak, e -> true);

        anakSearchField.textProperty().addListener((Observable, oldValue, newValue) -> {
            filter.setPredicate(predicateAnakData -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (predicateAnakData.getNama().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateAnakData.getNis().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateAnakData.getJenisKelamin().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateAnakData.getNamaOrangTua().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateAnakData.getNoTelpOrangTua().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateAnakData.getAlamatOrangTua().toLowerCase().contains(searchKey)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<Anak> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(anakTbl.comparatorProperty());
        anakTbl.setItems(sortList);
    }


}