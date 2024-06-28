package com.example.pbo_sekolahminggu.controllers.transactional_data;

import com.example.pbo_sekolahminggu.beans.*;
import com.example.pbo_sekolahminggu.dao.*;
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
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class KehadiranAnakController implements Initializable {
    @FXML
    private Button editKehadiranAnakBtn;
    @FXML
    private Button showKehadiranAnakBtn;
    @FXML
    private AnchorPane kehadiranAnakAncPane;
    @FXML
    private TextField kehadiranAnakSearchField;
    @FXML
    private TableView<KehadiranAnak> kehadiranAnakTbl;
    @FXML
    private ChoiceBox<TahunAjaran> tahunAjaranKehadiranAnakCb;
    @FXML
    private ChoiceBox<KelasPerTahun> kelasKehadiranAnakCb;
    @FXML
    private ChoiceBox<Kebaktian> kebaktianKehadiranAnakCb;

    ObservableList<KehadiranAnak> dataKehadiranAnak ;
    ObservableList<TahunAjaran> dataTahunAjaran;
    ObservableList<KelasPerTahun> dataKelas;
    ObservableList<Kebaktian> dataKebaktian;
    private Connection conn;

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

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

//        tahunAjaranKehadiranAnakCb.setValue(null);
//        kelasKehadiranAnakCb.setValue(null);
//        kebaktianKehadiranAnakCb.setValue(null);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            dataTahunAjaran.addAll(TahunAjaranDao.getAll(con));
            //populate the tahun ajaran choice box
            tahunAjaranKehadiranAnakCb.setItems(dataTahunAjaran);
            tahunAjaranKehadiranAnakCb.setConverter(new StringConverter<TahunAjaran>() {
                @Override
                public String toString(TahunAjaran object) {
                    return object.getTahunAjaran();
                }

                @Override
                public TahunAjaran fromString(String string) {
                    return ((ObservableList<TahunAjaran>) tahunAjaranKehadiranAnakCb.getItems()).stream()
                            .filter(selectedKelas -> string.equals(selectedKelas.getTahunAjaran()))
                            .findAny()
                            .orElse(null);
                }
            });
            tahunAjaranKehadiranAnakCb.getSelectionModel().select(0);

            TahunAjaran tahunSelected = tahunAjaranKehadiranAnakCb.getSelectionModel().getSelectedItem();

            //data kelas
            dataKelas.addAll(KelasPerTahunDao.getFilteredClasses(con, tahunSelected));
            kelasKehadiranAnakCb.setItems(dataKelas);

            kelasKehadiranAnakCb.setConverter(new StringConverter<KelasPerTahun>() {
                @Override
                public String toString(KelasPerTahun object) {
                    String paralel;
                    if (object.getKelasParalel() == null)  {
                        paralel = "";
                    } else paralel = " " + object.getKelasParalel();
                    return object.getNamaKelas() + paralel;
                }

                @Override
                public KelasPerTahun fromString(String string) {
                    return ((ObservableList<KelasPerTahun>)kelasKehadiranAnakCb.getItems()).stream()
                            .filter(selectedKelas -> string.equals(selectedKelas.getNamaKelas()))
                            .findAny()
                            .orElse(null);
                }
            });
            kelasKehadiranAnakCb.getSelectionModel().select(0);

            //data dropdown kebaktian
            dataKebaktian.addAll(KebaktianDao.getFilteredKebaktian(con, tahunSelected));
            kebaktianKehadiranAnakCb.setItems(dataKebaktian);
            kebaktianKehadiranAnakCb.setConverter(new StringConverter<Kebaktian>() {
                @Override
                public String toString(Kebaktian object) {
                    return object.getJenisKebaktian() + " (" + object.getTanggal() + ")";
                }

                @Override
                public Kebaktian fromString(String string) {
                    return ((ObservableList<Kebaktian>)kebaktianKehadiranAnakCb.getItems()).stream()
                            .filter(selectedKebaktian -> string.equals(selectedKebaktian.getJenisKebaktian()))
                            .findAny()
                            .orElse(null);
                }
            });
            kebaktianKehadiranAnakCb.getSelectionModel().select(0);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            ConnectionManager.close(con);
        }
//        tahunAjaranKehadiranAnakCb.setValue(null);
    }

    @FXML
    public void show() {
        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            refreshTable(con);
            if (refreshTable(con)) {
                alertWarning("Belum ada data kehadiran!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            ConnectionManager.close(con);
        }
    }

    @FXML
    public void edit() {
//        Connection con = null;
        try {
            conn = ConnectionManager.getConnection();

            KelasPerTahun selectedKelas = kelasKehadiranAnakCb.getSelectionModel().getSelectedItem();
            Kebaktian selectedKebaktian = kebaktianKehadiranAnakCb.getSelectionModel().getSelectedItem();
            KehadiranAnakDao.setSelectedKelas(selectedKelas);
            KehadiranAnakDao.setSelectedKebaktian(selectedKebaktian);

            //get the data kehadiran to check if it's empty or not
            dataKehadiranAnak = FXCollections.observableArrayList(KehadiranAnakDao.getAll(conn, selectedKelas, selectedKebaktian));
            // Disable auto-commit
            conn.setAutoCommit(false);

            if (dataKehadiranAnak.isEmpty()) {
                KehadiranAnakDao.populateTblKehadiranAnak(this.conn);
            }
            assignKehadiranAnakController.setCon(this.conn);  //this is to pass the connection
            loadMenuAssignKehadiranAnak();
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

    @FXML
    public void filterDataKelas() {
        TahunAjaran tahunSelected = tahunAjaranKehadiranAnakCb.getSelectionModel().getSelectedItem();
        if (tahunSelected == null) {
            alertWarning("Silahkan pilih tahun ajaran terlebih dahulu.");
            return;
        }

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            //filtered data of kelas per tahun
            ArrayList<KelasPerTahun> filteredClasses = KelasPerTahunDao.getFilteredClasses(con, tahunSelected);

            // Convert to ObservableList and update dataKelas
            ObservableList<KelasPerTahun> observableClasses = FXCollections.observableArrayList(filteredClasses);

            // Update ChoiceBox items
            kelasKehadiranAnakCb.setItems(observableClasses);

            // Set StringConverter if needed
            kelasKehadiranAnakCb.setConverter(new StringConverter<KelasPerTahun>() {
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
                kelasKehadiranAnakCb.getSelectionModel().selectFirst();
            }

            System.out.println("Filtered classes loaded successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching filtered classes: " + e.getMessage(), e);
        } finally {
            ConnectionManager.close(con);
        }
        kelasKehadiranAnakCb.show();
    }

    @FXML
    public void filterDataKebaktian() {
        TahunAjaran tahunSelected = tahunAjaranKehadiranAnakCb.getSelectionModel().getSelectedItem();
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

            // Update ChoiceBox items
            kebaktianKehadiranAnakCb.setItems(observableKebaktian);

            // Set StringConverter if needed
            kebaktianKehadiranAnakCb.setConverter(new StringConverter<Kebaktian>() {
                @Override
                public String toString(Kebaktian object) {
                    return object.getJenisKebaktian() + " (" + object.getTanggal() + ")";
                }

                @Override
                public Kebaktian fromString(String string) {
                    return ((ObservableList<Kebaktian>)kebaktianKehadiranAnakCb.getItems()).stream()
                            .filter(selectedKebaktian -> string.equals(selectedKebaktian.getJenisKebaktian()))
                            .findAny()
                            .orElse(null);
                }
            });
            // Optionally, select an item in the ChoiceBox
            if (!dataKebaktian.isEmpty()) {
                kelasKehadiranAnakCb.getSelectionModel().selectFirst();
            }

            System.out.println("Filtered kebaktian loaded successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching filtered classes: " + e.getMessage(), e);
        } finally {
            ConnectionManager.close(con);
        }
        kebaktianKehadiranAnakCb.show();
    }

    //refresh view tabel biar terlihat perubahan
    private boolean refreshTable(Connection con) {
        boolean empty = true;
        KelasPerTahun selectedKelas = kelasKehadiranAnakCb.getSelectionModel().getSelectedItem();
        Kebaktian selectedKbk = kebaktianKehadiranAnakCb.getSelectionModel().getSelectedItem();
        //get the table data
        dataKehadiranAnak = FXCollections.observableArrayList(KehadiranAnakDao.getAll(con, selectedKelas, selectedKbk));
        if (!dataKehadiranAnak.isEmpty()) {
            kehadiranAnakTbl.setItems(dataKehadiranAnak);
            empty = false;
        }
        return empty;
    }

    @FXML
    private void loadMenuAssignKehadiranAnak() {
        loadFXML("/com/example/pbo_sekolahminggu/assignKehadiranAnak.fxml");
    }

    private void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            // Now switch the scene or pane
            kehadiranAnakAncPane.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately (e.g., show error message)
        }
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
