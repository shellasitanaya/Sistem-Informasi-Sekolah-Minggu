package com.example.pbo_sekolahminggu.controllers.transactional.data;

import com.example.pbo_sekolahminggu.beans.master.data.Kebaktian;
import com.example.pbo_sekolahminggu.beans.master.data.Kelas;
import com.example.pbo_sekolahminggu.beans.master.data.TahunAjaran;
import com.example.pbo_sekolahminggu.beans.transactional.data.*;
import com.example.pbo_sekolahminggu.dao.master.data.KebaktianDao;
import com.example.pbo_sekolahminggu.dao.master.data.TahunAjaranDao;
import com.example.pbo_sekolahminggu.dao.transactional.data.*;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
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
import java.util.*;

public class KehadiranAnakController implements Initializable {
    @FXML
    TableColumn<KehadiranAnak, String> idKehadiranCol, namaCol, nisCol, kelasCol, kebaktianCol, tanggalCol, presensiCol;
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
    private ComboBox<TahunAjaran> tahunAjaranKehadiranAnakCb;
    @FXML
    private ComboBox<KelasPerTahun> kelasKehadiranAnakCb;
    @FXML
    private ComboBox<Kebaktian> kebaktianKehadiranAnakCb;

    ObservableList<KehadiranAnak> listKehadiranAnak = FXCollections.observableArrayList() ;

    ObservableList<KehadiranAnak> dataKehadiranAnak ;

    ObservableList<TahunAjaran> dataTahunAjaran = FXCollections.observableArrayList();;
    ObservableList<KelasPerTahun> dataKelas = FXCollections.observableArrayList();;
    ObservableList<Kebaktian> dataKebaktian = FXCollections.observableArrayList();;
    private Connection conn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateKelasTable();
        tahunAjaranKehadiranAnakCb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TahunAjaran>() {
            @Override
            public void changed(ObservableValue<? extends TahunAjaran> observable, TahunAjaran oldValue, TahunAjaran newValue) {
                if (newValue != null) {
                    System.out.println("Selection changed to: " + newValue.toString());

                    filterDataKelas();
                    filterDataKebaktian();
                    kelasKehadiranAnakCb.getSelectionModel().clearSelection();
                    kebaktianKehadiranAnakCb.getSelectionModel().clearSelection();
                }
            }
        });
        //get the table data
        try {
            dataKehadiranAnak = FXCollections.observableArrayList(KehadiranAnakDao.getAll(ConnectionManager.getConnection()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        kehadiranAnakTbl.setItems(dataKehadiranAnak);

        try {
            fillTahunAjaranCb();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
//
    }

    public void fillTahunAjaranCb() throws SQLException {
        try {
            // Populate tahunAjaranList from database
            dataTahunAjaran.addAll(TahunAjaranDao.getAll(ConnectionManager.getConnection()));

            // Set items and converter for ChoiceBox
            tahunAjaranKehadiranAnakCb.setItems(dataTahunAjaran);

            // Set up StringConverter
            tahunAjaranKehadiranAnakCb.setConverter(new StringConverter<TahunAjaran>() {
                @Override
                public String toString(TahunAjaran object) {
                    return object != null ? object.getTahunAjaran() : "";
                }

                @Override
                public TahunAjaran fromString(String string) {
                    return dataTahunAjaran.stream()
                            .filter(ta -> ta.getTahunAjaran().equals(string))
                            .findFirst()
                            .orElse(null); // Return null if not found (though you should ideally handle this case better)
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException("Error populating tahun ajaran list", e);
        }
    }


    public void Search() {

        FilteredList<KehadiranAnak> filter = new FilteredList<>(listKehadiranAnak, e -> true);

        kehadiranAnakSearchField.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate(predicateEmployeeData -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (predicateEmployeeData.getNamaAnak().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateEmployeeData.getNis().toLowerCase().contains(searchKey)) {
                    System.out.println("ada di nama");
                    return true;
                } else if (predicateEmployeeData.getKelas().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateEmployeeData.getTahunAjaran().toLowerCase().contains(searchKey)) {
                    return true;
                }else if (predicateEmployeeData.getKebaktian().toLowerCase().contains(searchKey)) {
                    return true;
                }else if (predicateEmployeeData.getTglKebaktian().toString().contains(searchKey)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<KehadiranAnak> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(kehadiranAnakTbl.comparatorProperty());
        kehadiranAnakTbl.setItems(sortList);
    }

    @FXML
    public void show() {
        try {
            conn = ConnectionManager.getConnection();
            if (checkComboBox()) return;
            //selected nama kelas
            KelasPerTahun selectedKelas = (KelasPerTahun) kelasKehadiranAnakCb.getSelectionModel().getSelectedItem();
            //selected tahun ajaran
            TahunAjaran selectedTahunAjaran = (TahunAjaran) tahunAjaranKehadiranAnakCb.getSelectionModel().getSelectedItem();
            //selected kebaktian
            Kebaktian selectedKebaktian = (Kebaktian) kebaktianKehadiranAnakCb.getSelectionModel(). getSelectedItem();


            // Get the ArrayList of Guru objects from the database
            ArrayList<KehadiranAnak> listKehadiranAnak= KehadiranAnakDao.getAllFiltered(ConnectionManager.getConnection(), selectedKelas, selectedKebaktian);
            ArrayList<HistoriKelasAnak> listKelasAnak = HistoriKelasAnakDao.get(ConnectionManager.getConnection(), selectedKelas.getIdKelasPerTahun());
            if (listKelasAnak.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning!");
                alert.setHeaderText(null);
                alert.setContentText("Tidak ada data anak yang terdaftar dalam kelas pada tahun ajaran ini. Data kehadiran tidak bisa diedit.");
                alert.showAndWait();
                return;
            }
            if (listKehadiranAnak.isEmpty()) {
                alertWarning("Data kehadiran anak tidak tersedia! Silahkan edit kehadiran terlebih dahulu.");
                return;
            }
            // Set cell value factory for each TableColumn
            idKehadiranCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdKehadiranAnak())));
            namaCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNamaAnak()));
            nisCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNis()));
            kelasCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            kebaktianCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKebaktian()));
            tanggalCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTglKebaktian())));
            presensiCol.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().isPresensi() ? "Hadir" : "Tidak Hadir")));


            // Add columns to the TableView
            kehadiranAnakTbl.getColumns().clear(); // Clear existing columns
            kehadiranAnakTbl.getColumns().addAll(idKehadiranCol, namaCol, nisCol, kelasCol, kebaktianCol, tanggalCol, presensiCol);

            // Set the data to the TableView
            kehadiranAnakTbl.getItems().clear(); // Clear existing data
            kehadiranAnakTbl.getItems().addAll(listKehadiranAnak);

            // Add listener for selection change
            kehadiranAnakTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Handle the event when a row is clicked
                    System.out.println("Row clicked: " + newSelection);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void edit() {
        if (checkComboBox()) return; //check if one of the combobox is null

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            KelasPerTahun selectedKelas = kelasKehadiranAnakCb.getSelectionModel().getSelectedItem();
            Kebaktian selectedKebaktian = kebaktianKehadiranAnakCb.getSelectionModel().getSelectedItem();
            KehadiranAnakDao.setSelectedKelas(selectedKelas);
            KehadiranAnakDao.setSelectedKebaktian(selectedKebaktian);

            // Get the data kehadiran
            dataKehadiranAnak = FXCollections.observableArrayList(KehadiranAnakDao.getAllFiltered(con, selectedKelas, selectedKebaktian));
            ArrayList<HistoriKelasAnak> listKelasAnak = HistoriKelasAnakDao.get(ConnectionManager.getConnection(), selectedKelas.getIdKelasPerTahun());
            if (listKelasAnak.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning!");
                alert.setHeaderText(null);
                alert.setContentText("Tidak ada data anak yang terdaftar dalam kelas pada tahun ajaran ini. Data kehadiran tidak bisa diedit.");
                alert.showAndWait();
                return;
            }
            if (dataKehadiranAnak.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Konfirmasi pengisian data kehadiran");
                alert.setHeaderText(null);
                alert.setContentText("Tidak ada data kehadiran anak yang ditemukan. Isi data kehadiran anak di kelas dan kebaktian ini?");

                // Add buttons to the alert
                ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().setAll(cancelButton, confirmButton);

                // Show the alert and wait for the response
                Connection finalCon = con;
                Optional<ButtonType> pilihan = alert.showAndWait();

                // Handle the user's response
                if (pilihan.isPresent() && pilihan.get() == confirmButton) {
                    //set the data to be passed
                    KehadiranAnakDao.setSelectedKelas(selectedKelas);
                    KehadiranAnakDao.setSelectedKebaktian(selectedKebaktian);

                    KehadiranAnakDao.populateTblKehadiranAnak(finalCon); // untuk mengisi kehadiran anak jika untuk kelas dan kebaktian yang terpilih, belum ada datanya
                    loadMenuAssignKehadiranAnak(); //move to the next window
                } else return;
            }
            loadMenuAssignKehadiranAnak();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(con);
        }
    }

    public void filterDataKelas() {
        TahunAjaran tahunSelected = tahunAjaranKehadiranAnakCb.getSelectionModel().getSelectedItem();
        if (!isTahunAjaranSelected()) return;
        Connection con = null;
        try {
            con = ConnectionManager.getConnection();

            ArrayList<KelasPerTahun> filteredClasses = KelasPerTahunDao.getFilteredClasses(con, tahunSelected);

            // Convert to ObservableList and update dataKelas
            ObservableList<KelasPerTahun> observableClasses = FXCollections.observableArrayList(filteredClasses);
            dataKelas.setAll(observableClasses);

            // Update ChoiceBox items
            kelasKehadiranAnakCb.setItems(dataKelas);

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
//        kelasKehadiranGuruCb.show();
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

    public void clear() {
        tahunAjaranKehadiranAnakCb.getSelectionModel().clearSelection();
        kelasKehadiranAnakCb.getSelectionModel().clearSelection();
        kebaktianKehadiranAnakCb.getSelectionModel().clearSelection();
    }

    public void search() {

        FilteredList<KehadiranAnak> filter = new FilteredList<>(dataKehadiranAnak, e -> true);

        kehadiranAnakSearchField.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate(predicateKelasData -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (predicateKelasData.getNamaAnak().toLowerCase().contains(searchKey)) {
                    return true;
                }else if (predicateKelasData.getNis().toLowerCase().contains(searchKey)) {
                    return true;
                }else if (predicateKelasData.getKelas().toLowerCase().contains(searchKey)) {
                    return true;
                }else if (predicateKelasData.getKebaktian().toLowerCase().contains(searchKey)) {
                    return true;
                }else if (predicateKelasData.getTglKebaktian().toString().contains(searchKey)) {
                    return true;
                }else if ((predicateKelasData.isPresensi() ? "Hadir" : "Tidak Hadir").toLowerCase().contains(searchKey)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<KehadiranAnak> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(kehadiranAnakTbl.comparatorProperty());
        kehadiranAnakTbl.setItems(sortList);
    }

    private void loadMenuAssignKehadiranAnak() {
        loadFXML("/com/example/pbo_sekolahminggu/views/transactional.data/assignKehadiranAnak.fxml");
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

    //function kalo misalnya ada textfield yang kosong, atau kelas yang mau didelete/diedit blm dipilih
    private void alertWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean checkComboBox() {
        if (kebaktianKehadiranAnakCb.getSelectionModel().getSelectedItem() == null || kelasKehadiranAnakCb.getSelectionModel().getSelectedItem() == null ||
                tahunAjaranKehadiranAnakCb.getSelectionModel().getSelectedItem() == null) {
            alertWarning("Harap isi semua kolom.");
            return true;
        }
        return false;
    }

    public boolean isTahunAjaranSelected() {
        TahunAjaran tahunSelected = tahunAjaranKehadiranAnakCb.getSelectionModel().getSelectedItem();
        if (tahunSelected == null) {
            alertWarning("Harap pilih tahun ajaran terlebih dahulu.");
            return false;
        }
        return true;
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
        File file = chooser.showSaveDialog(kehadiranAnakTbl.getScene().getWindow());
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
        TahunAjaran selectedTahun = tahunAjaranKehadiranAnakCb.getSelectionModel().getSelectedItem();
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

            // Judul
            Paragraph title = new Paragraph("Laporan Kehadiran Tiap Minggu Di Kelas Pada Tahun" + " " + selectedTahun.getTahunAjaran())
                    .setTextAlignment(TextAlignment.LEFT)
                    .setBold()
                    .setFontSize(20);
            com.itextpdf.layout.element.Cell titleCell = new com.itextpdf.layout.element.Cell().add(title);
            titleCell.setBorder(Border.NO_BORDER);
            titleCell.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
            headerTable.addCell(titleCell);
            doc.add(headerTable);

            // Membuat tabel dengan kolom yang sesuai
            Table table = new Table(UnitValue.createPercentArray(new float[]{20, 20, 20, 20, 20})).useAllAvailableWidth();

            // Header Tabel
            String[] headers = {"ID Histori Kelas Anak", "NIS", "Nama Anak", "Kelas", "Max Kehadiran"};
            Color customColor = new DeviceRgb(39, 106, 207);
            for (String header : headers) {
                Cell headerCell = new Cell();
                Paragraph headerText = new Paragraph(header);
                headerText.setTextAlignment(TextAlignment.CENTER);
                headerText.setBold();
                headerText.setFontColor(ColorConstants.WHITE);
                headerCell.setBackgroundColor(customColor);
                headerCell.add(headerText);
                table.addCell(headerCell);
            }

            // Mengambil data dari DAO
            Connection con = ConnectionManager.getConnection();
            Map<String, Object[]> data = KehadiranAnakDao.getAllArrayObject(con, selectedTahun);

            // Mengisi tabel dengan data
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object[] row = data.get(key);
                // debug
                System.out.println("ID Histori Kelas Anak: " + row[0]);
                System.out.println("NIS: " + row[1]);
                System.out.println("Nama Anak: " + row[2]);
                System.out.println("Kelas: " + row[3]);
                System.out.println("Max Kehadiran: " + row[4]);

                Cell idHistoriKelasAnakCell = new Cell().add(new Paragraph(String.valueOf(row[0])));
                table.addCell(idHistoriKelasAnakCell);

                Cell nisCell = new Cell().add(new Paragraph(String.valueOf(row[1])));
                table.addCell(nisCell);

                Cell namaAnakCell = new Cell().add(new Paragraph(String.valueOf(row[2])));
                table.addCell(namaAnakCell);

                Cell kelasCell = new Cell().add(new Paragraph(String.valueOf(row[3])));
                table.addCell(kelasCell);

                Cell maxKehadiranCell = new Cell().add(new Paragraph(String.valueOf(row[4])));
                table.addCell(maxKehadiranCell);
            }
            doc.add(table);
            doc.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void exportToExcel(File file) {
        TahunAjaran selectedTahun = tahunAjaranKehadiranAnakCb.getSelectionModel().getSelectedItem();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("Kehadiran Anak Data");

        FileOutputStream out = null;
        Connection con = null;

        try {
            con = ConnectionManager.getConnection();
            int rowid = 0;

            /// Mengatur style untuk judul
            XSSFRow titleRow = spreadsheet.createRow(rowid++);
            titleRow.setHeightInPoints(30); // Set tinggi baris untuk judul
            XSSFCell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Laporan Kehadiran Tiap Minggu Di Kelas Pada Tahun" + " " + selectedTahun.getTahunAjaran());
            CellRangeAddress mergedRegion = new CellRangeAddress(0, 0, 0, 4); // merge kolom untuk judul
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
            titleCell.setCellStyle(titleStyle);
            // Set border setelah font
            titleStyle.setBorderBottom(BorderStyle.THIN);
            titleStyle.setBorderTop(BorderStyle.THIN);
            titleStyle.setBorderLeft(BorderStyle.THIN);
            titleStyle.setBorderRight(BorderStyle.THIN);

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
            // Set border setelah font
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);


            // Export Header
            XSSFRow headerRow = spreadsheet.createRow(rowid++);
            String[] headers = {"ID Histori Kelas Anak", "NIS", "Nama Anak", "Kelas", "Max Kehadiran"};
            int cellCounter = 0;

            for (String header : headers) {
                XSSFCell cell = headerRow.createCell(cellCounter++);
                cell.setCellValue(header);
                cell.setCellStyle(headerStyle); // Terapkan style header di sini
                spreadsheet.autoSizeColumn(cellCounter - 1);
            }


            // Export Data
            Map<String, Object[]> data = KehadiranAnakDao.getAllArrayObject(con, selectedTahun);
            Set<String> keyid = data.keySet();

            for (String key : keyid) {
                XSSFRow row = spreadsheet.createRow(rowid++);
                Object[] objectArr = data.get(key);

                XSSFCell cellIdHistori = row.createCell(0);
                cellIdHistori.setCellValue(String.valueOf(objectArr[0]));

                XSSFCell cellNIS = row.createCell(1);
                cellNIS.setCellValue(String.valueOf(objectArr[1]));

                XSSFCell cellNamaAnak = row.createCell(2);
                cellNamaAnak.setCellValue(String.valueOf(objectArr[2]));

                XSSFCell cellKelas = row.createCell(3);
                cellKelas.setCellValue(String.valueOf(objectArr[3]));

                XSSFCell cellMaxKehadiran = row.createCell(4);
                cellMaxKehadiran.setCellValue(String.valueOf(objectArr[4]));

            }

            int[] columnWidths = {5000, 4500, 6500, 6000, 5500};
            for (int i = 0; i < columnWidths.length; i++) {
                spreadsheet.setColumnWidth(i, columnWidths[i]);
            }

            out = new FileOutputStream(file);
            workbook.write(out);
        } catch (SQLException | IOException e) {
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

    public void populateKelasTable() {

        try {
            // Get the ArrayList of Guru objects from the database
            ArrayList<KehadiranAnak> listKehadiranAnak = KehadiranAnakDao.getAll(ConnectionManager.getConnection());

            // Set cell value factory for each TableColumn
            idKehadiranCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdKehadiranAnak())));
            idKehadiranCol.setMinWidth(80);
            namaCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNamaAnak()));
            namaCol.setMinWidth(190);
            nisCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNis()));
            nisCol.setMinWidth(130);
            kelasCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            kelasCol.setMinWidth(130);
            kebaktianCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKebaktian()));
            kebaktianCol.setMinWidth(90);
            tanggalCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTglKebaktian())));
            tanggalCol.setMinWidth(100);
            presensiCol.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().isPresensi() ? "Hadir" : "Tidak Hadir")));
            presensiCol.setMinWidth(90);

            // Add columns to the TableView
            kehadiranAnakTbl.getColumns().clear(); // Clear existing columns
            kehadiranAnakTbl.getColumns().addAll(idKehadiranCol, namaCol, nisCol, kelasCol, kebaktianCol, tanggalCol, presensiCol);

            // Set the data to the TableView
            kehadiranAnakTbl.getItems().clear(); // Clear existing data
            kehadiranAnakTbl.getItems().addAll(listKehadiranAnak);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}