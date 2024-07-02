package com.example.pbo_sekolahminggu.controllers.transactional.data;

import com.example.pbo_sekolahminggu.beans.master.data.Kebaktian;
import com.example.pbo_sekolahminggu.beans.master.data.TahunAjaran;
import com.example.pbo_sekolahminggu.beans.transactional.data.KehadiranAnak;
import com.example.pbo_sekolahminggu.beans.transactional.data.KehadiranGuru;
import com.example.pbo_sekolahminggu.beans.transactional.data.KelasPerTahun;
import com.example.pbo_sekolahminggu.dao.master.data.KebaktianDao;
import com.example.pbo_sekolahminggu.dao.master.data.TahunAjaranDao;
import com.example.pbo_sekolahminggu.dao.transactional.data.KehadiranGuruDao;
import com.example.pbo_sekolahminggu.dao.transactional.data.KelasPerTahunDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class KehadiranGuruController implements Initializable {
    @FXML
    TableView<KehadiranGuru> kehadiranGuruTbl;
    @FXML
    TableColumn<KehadiranGuru, String> idKehadiranCol, namaCol, nipCol, kelasCol, kebaktianCol, tanggalCol, presensiCol;
    @FXML
    ComboBox<TahunAjaran> tahunAjaranKehadiranGuruCb;
    @FXML
    ComboBox<KelasPerTahun> kelasKehadiranGuruCb;
    @FXML
    ComboBox<Kebaktian> kebaktianKehadiranGuruCb;
    @FXML
    private AnchorPane kehadiranGuruAncPane;
    @FXML
    private TextField kehadiranGuruSearchField;

    ObservableList<KehadiranGuru> dataKehadiranGuru ;


    ObservableList<KelasPerTahun> dataKelas = FXCollections.observableArrayList();
    ObservableList<TahunAjaran> tahunAjaranList = FXCollections.observableArrayList();
    ObservableList<Kebaktian> dataKebaktian = FXCollections.observableArrayList();

    private Connection conn;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateKelasTable();
        tahunAjaranKehadiranGuruCb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TahunAjaran>() {
            @Override
            public void changed(ObservableValue<? extends TahunAjaran> observable, TahunAjaran oldValue, TahunAjaran newValue) {
                if (newValue != null) {
                    System.out.println("Selection changed to: " + newValue.toString());

                    filterDataKelas();
                    filterDataKebaktian();
                    kelasKehadiranGuruCb.getSelectionModel().clearSelection();
                    kebaktianKehadiranGuruCb.getSelectionModel().clearSelection();
                }
            }
        });
        //get the table data
        try {
            dataKehadiranGuru = FXCollections.observableArrayList(KehadiranGuruDao.getAll(ConnectionManager.getConnection()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        kehadiranGuruTbl.setItems(dataKehadiranGuru);

        try {
            fillTahunAjaranCb();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
//        tahunAjaranHistoriMengajarCb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TahunAjaran>() {
//            @Override
//            public void changed(ObservableValue<? extends TahunAjaran> observable, TahunAjaran oldValue, TahunAjaran newValue) {
//                if (newValue != null) {
//                    System.out.println("Selection changed to: " + newValue.toString());
//
//                    filterDataKelas();
//                }
//            }
//        });
    }


    public void filterDataKebaktian() {
        TahunAjaran tahunSelected = tahunAjaranKehadiranGuruCb.getSelectionModel().getSelectedItem();
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
            dataKebaktian.setAll(observableKebaktian);

            // Update ChoiceBox items
            kebaktianKehadiranGuruCb.setItems(dataKebaktian);

            // Set StringConverter if needed
            kebaktianKehadiranGuruCb.setConverter(new StringConverter<Kebaktian>() {
                @Override
                public String toString(Kebaktian object) {
                    return object.getJenisKebaktian() + " (" + object.getTanggal() + ")";
                }

                @Override
                public Kebaktian fromString(String string) {
                    return ((ObservableList<Kebaktian>)kebaktianKehadiranGuruCb.getItems()).stream()
                            .filter(selectedKebaktian -> string.equals(selectedKebaktian.getJenisKebaktian()))
                            .findAny()
                            .orElse(null);
                }
            });
            // Optionally, select an item in the ChoiceBox
            if (!dataKebaktian.isEmpty()) {
                kebaktianKehadiranGuruCb.getSelectionModel().selectFirst();
            }

            System.out.println("Filtered kebaktian loaded successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching filtered classes: " + e.getMessage(), e);
        } finally {
            ConnectionManager.close(con);
        }
//        kebaktianKehadiranGuruCb.show();
    }

    public void fillTahunAjaranCb() throws SQLException {
        try {
            // Populate tahunAjaranList from database
            tahunAjaranList.addAll(TahunAjaranDao.getAll(ConnectionManager.getConnection()));

            // Set items and converter for ChoiceBox
            tahunAjaranKehadiranGuruCb.setItems(tahunAjaranList);

            // Set up StringConverter
            tahunAjaranKehadiranGuruCb.setConverter(new StringConverter<TahunAjaran>() {
                @Override
                public String toString(TahunAjaran object) {
                    return object != null ? object.getTahunAjaran() : "";
                }

                @Override
                public TahunAjaran fromString(String string) {
                    return tahunAjaranList.stream()
                            .filter(ta -> ta.getTahunAjaran().equals(string))
                            .findFirst()
                            .orElse(null); // Return null if not found (though you should ideally handle this case better)
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException("Error populating tahun ajaran list", e);
        }
    }


    public void populateKelasTable() {

        try {
            // Get the ArrayList of Guru objects from the database
            ArrayList<KehadiranGuru> listKehadiranGuru = KehadiranGuruDao.getAll(ConnectionManager.getConnection());

            // Set cell value factory for each TableColumn
            idKehadiranCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdKehadiranGuru())));
            idKehadiranCol.setMinWidth(80);
            namaCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNama()));
            namaCol.setMinWidth(190);
            nipCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNip()));
            nipCol.setMinWidth(130);
            kelasCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            kelasCol.setMinWidth(130);
            kebaktianCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getJenisKebaktian()));
            kebaktianCol.setMinWidth(90);
            tanggalCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTanggal())));
            tanggalCol.setMinWidth(100);
            presensiCol.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().isPresensi() ? "Hadir" : "Tidak Hadir")));
            presensiCol.setMinWidth(90);

            // Add columns to the TableView
            kehadiranGuruTbl.getColumns().clear(); // Clear existing columns
            kehadiranGuruTbl.getColumns().addAll(idKehadiranCol, namaCol, nipCol, kelasCol, kebaktianCol, tanggalCol, presensiCol);

            // Set the data to the TableView
            kehadiranGuruTbl.getItems().clear(); // Clear existing data
            kehadiranGuruTbl.getItems().addAll(listKehadiranGuru);

            // Add listener for selection change
//            kehadiranGuruTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
//                if (newSelection != null) {
//                    // Handle the event when a row is clicked
//                    System.out.println("Row clicked: " + newSelection);
//                }
//                KehadiranGuru selectedKehadiranGuru = kehadiranGuruTbl.getSelectionModel().getSelectedItem();
//                KelasPerTahun newKelas = kelasKehadiranGuruCb.getItems().stream()
//                        .filter(kelas -> kelas.getIdKelasPerTahun() == selectedKehadiranGuru.getIdKelasPerTahun())
//                        .findFirst()
//                        .orElse(null);
//                kelasKehadiranGuruCb.getSelectionModel().select(newKelas);
//                TahunAjaran newTahunAjaran = tahunAjaranKehadiranGuruCb.getItems().stream()
//                        .filter(tahunAjaran -> tahunAjaran.getIdTahunAjaran() == selectedKehadiranGuru.getIdTahunAjaran())
//                        .findFirst()
//                        .orElse(null);
//                tahunAjaranKehadiranGuruCb.getSelectionModel().select(newTahunAjaran);
//                Kebaktian newKebaktian = kebaktianKehadiranGuruCb.getItems().stream()
//                        .filter(kebaktian -> kebaktian.getIdKebaktian() == selectedKehadiranGuru.getIdKebaktian())
//                        .findFirst()
//                        .orElse(null);
//                kebaktianKehadiranGuruCb.getSelectionModel().select(newKebaktian);
//            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void filterDataKelas() {
        TahunAjaran tahunSelected = tahunAjaranKehadiranGuruCb.getSelectionModel().getSelectedItem();

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();

            ArrayList<KelasPerTahun> filteredClasses = KelasPerTahunDao.getFilteredClasses(con, tahunSelected);

            // Convert to ObservableList and update dataKelas
            ObservableList<KelasPerTahun> observableClasses = FXCollections.observableArrayList(filteredClasses);
            dataKelas.setAll(observableClasses);

            // Update ChoiceBox items
            kelasKehadiranGuruCb.setItems(dataKelas);

            // Set StringConverter if needed
            kelasKehadiranGuruCb.setConverter(new StringConverter<KelasPerTahun>() {
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
                kelasKehadiranGuruCb.getSelectionModel().selectFirst();
            }

            System.out.println("Filtered classes loaded successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching filtered classes: " + e.getMessage(), e);
        } finally {
            ConnectionManager.close(con);
        }
//        kelasKehadiranGuruCb.show();
    }

    public void edit() {
        System.out.println("halo");
        if (checkComboBox()) return;  //check if one of the combobox is null
        System.out.println("halo2");
        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            KelasPerTahun selectedKelas = kelasKehadiranGuruCb.getSelectionModel().getSelectedItem();
            Kebaktian selectedKebaktian = kebaktianKehadiranGuruCb.getSelectionModel().getSelectedItem();
            KehadiranGuruDao.setSelectedKelas(selectedKelas);
            KehadiranGuruDao.setSelectedKebaktian(selectedKebaktian);

            //get the data kehadiran to check if it's empty or not
            dataKehadiranGuru = FXCollections.observableArrayList(KehadiranGuruDao.getSpecial(con, selectedKelas, selectedKebaktian));
            System.out.println(dataKehadiranGuru);
            if (dataKehadiranGuru.isEmpty()) {
                alertWarning("Data kehadiran yang terpilih belum tersedia!");
                return;
            }
            loadMenuAssignKehadiranGuru();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(con);
        }
    }

    private void loadMenuAssignKehadiranGuru() {
        loadFXML("/com/example/pbo_sekolahminggu/views/transactional.data/assignKehadiranGuru.fxml");
    }

    private void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            // Now switch the scene or pane
            kehadiranGuruAncPane.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately (e.g., show error message)
        }
    }

    public void showFilter(){
        try {
            conn = ConnectionManager.getConnection();
            if (checkComboBox()) return;
            //selected nama kelas
            KelasPerTahun selectedKelas = (KelasPerTahun) kelasKehadiranGuruCb.getSelectionModel().getSelectedItem();
            //selected tahun ajaran
            TahunAjaran selectedTahunAjaran = (TahunAjaran) tahunAjaranKehadiranGuruCb.getSelectionModel().getSelectedItem();
            //selected kebaktian
            Kebaktian selectedKebaktian = (Kebaktian) kebaktianKehadiranGuruCb.getSelectionModel(). getSelectedItem();


            // Get the ArrayList of Guru objects from the database
            ArrayList<KehadiranGuru> listKehadiranGuru = KehadiranGuruDao.get(ConnectionManager.getConnection(), selectedKebaktian.getIdKebaktian(), selectedKelas.getIdKelasPerTahun());

            if (listKehadiranGuru.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Konfirmasi pengisian data kehadiran");
                alert.setHeaderText(null);
                alert.setContentText("Tidak ada data kehadiran guru yang ditemukan. Isi data kehadiran guru di kelas dan kebaktian ini?");

                // Add buttons to the alert
                ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().setAll(cancelButton, confirmButton);

                // Show the alert and wait for the response
                Connection finalCon = conn;
                Optional<ButtonType> pilihan = alert.showAndWait();

                // Handle the user's response
                if (pilihan.isPresent() && pilihan.get() == confirmButton) {
                    //set the data to be passed
                    KehadiranGuruDao.setSelectedKelas(selectedKelas);
                    KehadiranGuruDao.setSelectedKebaktian(selectedKebaktian);

                    KehadiranGuruDao.populateTblKehadiranGuru(finalCon); // untuk mengisi kehadiran anak jika untuk kelas dan kebaktian yang terpilih, belum ada datanya
                    loadMenuAssignKehadiranGuru(); //move to the next window
                } else return;
            }
            // Set cell value factory for each TableColumn
            idKehadiranCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdKehadiranGuru())));
            namaCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNama()));
            nipCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNip()));
            kelasCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            kebaktianCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getJenisKebaktian()));
            tanggalCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTanggal())));
            presensiCol.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().isPresensi() ? "Hadir" : "Tidak Hadir")));


            // Add columns to the TableView
            kehadiranGuruTbl.getColumns().clear(); // Clear existing columns
            kehadiranGuruTbl.getColumns().addAll(idKehadiranCol, namaCol, nipCol, kelasCol, kebaktianCol, tanggalCol, presensiCol);

            // Set the data to the TableView
            kehadiranGuruTbl.getItems().clear(); // Clear existing data
            kehadiranGuruTbl.getItems().addAll(listKehadiranGuru);

            // Add listener for selection change
            kehadiranGuruTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Handle the event when a row is clicked
                    System.out.println("Row clicked: " + newSelection);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void search() {
        // Check if dataKehadiranGuru is null before creating FilteredList
        if (dataKehadiranGuru == null) {
            // Handle the case where dataKehadiranGuru is null
            System.out.println("Data Kehadiran Guru is null. Cannot perform search.");
            return;
        }

        // Create FilteredList
        FilteredList<KehadiranGuru> filter = new FilteredList<>(dataKehadiranGuru, e -> true);

        // Add listener to search field
        kehadiranGuruSearchField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            filter.setPredicate(kehadiranGuru -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Show all items if filter text is empty
                }

                String searchKey = newValue.toLowerCase();

                // Check for matches in each property of KehadiranGuru
                if (kehadiranGuru.getNama().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (kehadiranGuru.getNip().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (kehadiranGuru.getKelas().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (kehadiranGuru.getJenisKebaktian().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (kehadiranGuru.getTglKebaktian() != null &&
                        new SimpleDateFormat("yyyy-MM-dd").format(kehadiranGuru.getTglKebaktian()).contains(searchKey)) {
                    return true;
                } else if ((kehadiranGuru.isPresensi() ? "Hadir" : "Tidak Hadir").toLowerCase().contains(searchKey)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        // Create SortedList and bind to TableView
        SortedList<KehadiranGuru> sortList = new SortedList<>(filter);
        sortList.comparatorProperty().bind(kehadiranGuruTbl.comparatorProperty());
        kehadiranGuruTbl.setItems(sortList);
    }


    private void alertWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean checkComboBox() {
        if (kebaktianKehadiranGuruCb.getSelectionModel().getSelectedItem() == null || tahunAjaranKehadiranGuruCb.getSelectionModel().getSelectedItem() == null ||
                kelasKehadiranGuruCb.getSelectionModel().getSelectedItem() == null) {
            alertWarning("Harap pilih semua kolom.");
            return true;
        }
        return false;
    }


    public void clear(){
        tahunAjaranKehadiranGuruCb.getSelectionModel().clearSelection();
        kelasKehadiranGuruCb.getSelectionModel().clearSelection();
        kebaktianKehadiranGuruCb.getSelectionModel().clearSelection();
    }


    // --------------------------------------------------
    @FXML
    public void export() {
        if (checkComboBox()) return;
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter excelFilter = new FileChooser.ExtensionFilter("Microsoft Excel Spreadsheet (*.xlsx)", "*.xlsx");
        FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("Portable Document Format files (*.pdf)", "*.pdf");
        chooser.getExtensionFilters().add(pdfFilter);
        chooser.getExtensionFilters().add(excelFilter);

        chooser.setInitialDirectory(new File("C:\\Users"));
        File file = chooser.showSaveDialog(kehadiranGuruTbl.getScene().getWindow());
        FileChooser.ExtensionFilter selectedFilter = chooser.getSelectedExtensionFilter();

        if (file != null) {
            if (selectedFilter.getExtensions().get(0).equalsIgnoreCase("*.xlsx")) {
                exportToExcel(file);
            } else if (selectedFilter.getExtensions().get(0).equalsIgnoreCase("*.pdf")) {
                exportToPdf(file);
            }
        }
    }
    private void exportToPdf(File file) {
        TahunAjaran selectedTahun = tahunAjaranKehadiranGuruCb.getSelectionModel().getSelectedItem();
        if (selectedTahun == null) {
            alertWarning("Pilih tahun ajaran terlebih dahulu!");
            return;
        }
        System.out.println(file.getAbsolutePath());
        PdfDocument pdfDoc = null;
        try {
            pdfDoc = new PdfDocument(new PdfWriter(file.getAbsolutePath()));
            Document doc = new Document(pdfDoc);

            // Membuat table untuk logo dan judul
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 5})).useAllAvailableWidth();
            headerTable.setMarginBottom(10);

            // Menambahkan logo
            Image logo = new Image(ImageDataFactory.create("src/main/resources/com/example/pbo_sekolahminggu/images/sekolahMingguLogo.png"));
            logo.setWidth(UnitValue.createPercentValue(100));
            com.itextpdf.layout.element.Cell logoCell = new com.itextpdf.layout.element.Cell().add(logo);
            logoCell.setBorder(Border.NO_BORDER);
            headerTable.addCell(logoCell);

            // Menambahkan judul di sebelah logo
            Paragraph title = new Paragraph("Laporan Kehadiran Guru Berdasarkan Tahun Ajaran")
                    .setTextAlignment(TextAlignment.LEFT)
                    .setBold()
                    .setFontSize(20);
            com.itextpdf.layout.element.Cell titleCell = new com.itextpdf.layout.element.Cell().add(title);
            titleCell.setBorder(Border.NO_BORDER);
            titleCell.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
            headerTable.addCell(titleCell);

            doc.add(headerTable);


            // Membuat tabel dengan kolom yang sesuai
            Table table = new Table(UnitValue.createPercentArray(new float[]{40, 40, 20})).useAllAvailableWidth();

            // Header Tabel
            String[] headers = {"Nama Guru", "Tahun Ajaran", "Total Kehadiran"};
            Color customColor = new DeviceRgb(39, 106, 207);
            for (String header : headers) {
                com.itextpdf.layout.element.Cell headerCell = new com.itextpdf.layout.element.Cell();
                Paragraph headerParagraph = new Paragraph(header);
                headerParagraph.setTextAlignment(TextAlignment.CENTER);
                headerParagraph.setBold();
                headerParagraph.setFontColor(ColorConstants.WHITE);
                headerCell.setBackgroundColor(customColor);
                headerCell.add(headerParagraph);
                table.addCell(headerCell);
            }

            // Mengambil data dari DAO
            Connection con = ConnectionManager.getConnection();
            Map<String, Object[]> data = KehadiranGuruDao.getAllArrayObject(con, selectedTahun);

            // Mengisi tabel dengan data
            for (Object[] rowData : data.values()) {
                // Pastikan data yang diambil sesuai dengan urutan kolom yang diharapkan
                String namaGuru = rowData[0].toString();
                String tahunAjaran = rowData[1].toString();
                String totalKehadiran = rowData[2].toString();

                // Data Nama Guru
                com.itextpdf.layout.element.Cell namaGuruCell = new com.itextpdf.layout.element.Cell();
                Paragraph namaGuruParagraph = new Paragraph(namaGuru);
                namaGuruParagraph.setTextAlignment(TextAlignment.CENTER);
                namaGuruCell.add(namaGuruParagraph);
                table.addCell(namaGuruCell);

                // Data Tahun Ajaran
                com.itextpdf.layout.element.Cell tahunAjaranCell = new com.itextpdf.layout.element.Cell();
                Paragraph tahunAjaranParagraph = new Paragraph(tahunAjaran);
                tahunAjaranParagraph.setTextAlignment(TextAlignment.CENTER);
                tahunAjaranCell.add(tahunAjaranParagraph);
                table.addCell(tahunAjaranCell);

                // Data Total Kehadiran
                com.itextpdf.layout.element.Cell totalKehadiranCell = new com.itextpdf.layout.element.Cell();
                Paragraph totalKehadiranParagraph = new Paragraph(totalKehadiran);
                totalKehadiranParagraph.setTextAlignment(TextAlignment.CENTER);
                totalKehadiranCell.add(totalKehadiranParagraph);
                table.addCell(totalKehadiranCell);
            }

            doc.add(table);
            doc.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void exportToExcel(File file) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("Laporan Kehadiran Guru");

        FileOutputStream out = null;
        Connection con = null;


        try {
            TahunAjaran selectedTahun = tahunAjaranKehadiranGuruCb.getSelectionModel().getSelectedItem();
            if (selectedTahun == null) {
                alertWarning("Pilih tahun ajaran terlebih dahulu!");
                return;
            }
            con = ConnectionManager.getConnection();
            int rowid = 0;

            // Judul
            // Mengatur style untuk judul
            XSSFRow titleRow = spreadsheet.createRow(rowid++);
            titleRow.setHeightInPoints(30); // Set tinggi baris untuk judul
            XSSFCell titleCell = titleRow.createCell(0);
            String judul = "Laporan Kehadiran Guru Berdasarkan Tahun Ajaran";
            titleCell.setCellValue(judul);
            CellRangeAddress mergedRegion = new CellRangeAddress(0, 0, 0, 2); // merge kolom untuk judul
            spreadsheet.addMergedRegion(mergedRegion);

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            titleStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font titleFont = workbook.createFont();
            titleFont.setColor(IndexedColors.WHITE.getIndex());
            titleFont.setBold(true);
            titleStyle.setFont(titleFont);
            // Set border untuk judul
            titleStyle.setBorderBottom(BorderStyle.THIN);
            titleStyle.setBorderTop(BorderStyle.THIN);
            titleStyle.setBorderLeft(BorderStyle.THIN);
            titleStyle.setBorderRight(BorderStyle.THIN);
            titleCell.setCellStyle(titleStyle);

            // Mengatur style untuk header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER); // Menyesuaikan agar teks rata tengah
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.BLACK.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            // Set border untuk header
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Export Header
            XSSFRow headerRow = spreadsheet.createRow(rowid++);
            String[] headers = {"Nama Guru", "Tahun Ajaran", "Total Kehadiran"};
            int cellCounter = 0;
            for (String header : headers) {
                XSSFCell cell = headerRow.createCell(cellCounter++);
                cell.setCellValue(header);
                cell.setCellStyle(headerStyle);
                spreadsheet.autoSizeColumn(cellCounter - 1);
            }

            // Mengatur style untuk data
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.CENTER); // Menyesuaikan agar teks rata tengah
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            // Set border untuk data
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            // Export Data
            Map<String, Object[]> data = KehadiranGuruDao.getAllArrayObject(con, selectedTahun);
            Set<String> keyid = data.keySet();

            for (String key : keyid) {
                XSSFRow row = spreadsheet.createRow(rowid++);
                Object[] objectArr = data.get(key);

                for (int i = 0; i < objectArr.length; i++) {
                    XSSFCell cell = row.createCell(i);
                    cell.setCellValue(String.valueOf(objectArr[i]));
                    cell.setCellStyle(dataStyle); // Terapkan style untuk data di sini
                    spreadsheet.autoSizeColumn(i);
                }
            }

            int[] columnWidths = {5500, 4000, 4000};
            for (int i = 0; i < columnWidths.length; i++) {
                spreadsheet.setColumnWidth(i, columnWidths[i]);
            }

            out = new FileOutputStream(file);
            workbook.write(out);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(con);
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }



}