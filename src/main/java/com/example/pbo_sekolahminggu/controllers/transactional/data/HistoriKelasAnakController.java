package com.example.pbo_sekolahminggu.controllers.transactional.data;

import com.example.pbo_sekolahminggu.beans.master.data.TahunAjaran;
import com.example.pbo_sekolahminggu.beans.transactional.data.HistoriKelasAnak;
import com.example.pbo_sekolahminggu.beans.transactional.data.KelasPerTahun;
import com.example.pbo_sekolahminggu.dao.master.data.TahunAjaranDao;
import com.example.pbo_sekolahminggu.dao.transactional.data.HistoriKelasAnakDao;
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
import java.util.*;


public class HistoriKelasAnakController implements Initializable {

    @FXML
    private Button ediHistoriKelas;
    @FXML
    private AnchorPane historiKelasAncPane;
    @FXML
    private TextField historiKelasSearchField;
    @FXML
    private TableView<HistoriKelasAnak> historiKelasTbl;
    @FXML
    private ChoiceBox<KelasPerTahun> kelasHistoriKelasCb;
    @FXML
    private Button showHistoriKelas;
    @FXML
    private ChoiceBox<com.example.pbo_sekolahminggu.beans.master.data.TahunAjaran> tahunAjaranHistoriKelasCb;
    @FXML
    private TableColumn<HistoriKelasAnak, String> IDHistori, Nama, NIS, Kelas, TahunAjaran;
    ObservableList<HistoriKelasAnak> dataKehadiranAnak ;
    ObservableList<TahunAjaran> dataTahunAjaran = FXCollections.observableArrayList();
    ObservableList<KelasPerTahun> dataKelas = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        populateKelasTable();

        tahunAjaranHistoriKelasCb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TahunAjaran>() {
            @Override
            public void changed(ObservableValue<? extends TahunAjaran> observable, TahunAjaran oldValue, TahunAjaran newValue) {
                if (newValue != null) {
                    System.out.println("Selection changed to: " + newValue.toString());

                    filterDataKelas();
                }
            }
        });

        try {
            fillTahunAjaranCb();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void showFilter(){  //buat filter data pas teken show
        try {
            //selected nama kelas
            KelasPerTahun selectedKelas = (KelasPerTahun) kelasHistoriKelasCb.getSelectionModel().getSelectedItem();
            //selected tahun ajaran
//            TahunAjaran selectedTahunAjaran = (TahunAjaran) tahunAjaranHistoriKelasCb.getSelectionModel().getSelectedItem();
            // Get the ArrayList of Guru objects from the database
            ArrayList<HistoriKelasAnak> listHistoryKelasAnak = HistoriKelasAnakDao.get(ConnectionManager.getConnection(), selectedKelas.getID_KELAS_PER_TAHUN());

            if (listHistoryKelasAnak.isEmpty()) {
                alertWarning("Belum ada anak yang terdaftar di kelas ini!");
            }
            // Set cell value factory for each TableColumn
            IDHistori.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getID_HISTORI_KELAS_ANAK())));
            Nama.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNama_anak()));
            NIS.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNis()));
            Kelas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            TahunAjaran.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTahun_ajaran()));


            // Add columns to the TableView
            historiKelasTbl.getColumns().clear(); // Clear existing columns
            historiKelasTbl.getColumns().addAll(IDHistori, Nama, NIS, Kelas, TahunAjaran);

            // Set the data to the TableView
            historiKelasTbl.getItems().clear(); // Clear existing data
            historiKelasTbl.getItems().addAll(listHistoryKelasAnak);

            // Add listener for selection change
            historiKelasTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Handle the event when a row is clicked
                    System.out.println("Row clicked: " + newSelection);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void fillTahunAjaranCb() throws SQLException {
        try {
            // Populate tahunAjaranList from database
            dataTahunAjaran.addAll(TahunAjaranDao.getAll(ConnectionManager.getConnection()));

            // Set items and converter for ChoiceBox
            tahunAjaranHistoriKelasCb.setItems(dataTahunAjaran);

            // Set up StringConverter
            tahunAjaranHistoriKelasCb.setConverter(new StringConverter<TahunAjaran>() {
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


    public void filterDataKelas() {
        TahunAjaran tahunSelected = tahunAjaranHistoriKelasCb.getSelectionModel().getSelectedItem();
        if (tahunSelected == null) {
            System.out.println("No TahunAjaran selected!");
            return;
        }

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();

            ArrayList<KelasPerTahun> filteredClasses = KelasPerTahunDao.getFilteredClasses(con, tahunSelected);

            // Convert to ObservableList and update dataKelas
            ObservableList<KelasPerTahun> observableClasses = FXCollections.observableArrayList(filteredClasses);
            dataKelas.setAll(observableClasses);

            // Update ChoiceBox items
            kelasHistoriKelasCb.setItems(dataKelas);

            // Set StringConverter if needed
            kelasHistoriKelasCb.setConverter(new StringConverter<KelasPerTahun>() {
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
                kelasHistoriKelasCb.getSelectionModel().selectFirst();
            }

            System.out.println("Filtered classes loaded successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching filtered classes: " + e.getMessage(), e);
        } finally {
            ConnectionManager.close(con);
        }
    }

    public void populateKelasTable() {
        try {
            // Get the ArrayList of Guru objects from the database
            ArrayList<HistoriKelasAnak> listHistoryMengajar = HistoriKelasAnakDao.getAll(ConnectionManager.getConnection());

            //initialize
            IDHistori = new TableColumn<>("ID Histori");
            Nama = new TableColumn<>("Nama");
            NIS = new TableColumn<>("NIS");
            Kelas = new TableColumn<>("Kelas");
            TahunAjaran = new TableColumn<>("Tahun Ajaran");


            // Set cell value factory for each TableColumn
            IDHistori.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getID_HISTORI_KELAS_ANAK())));
            Nama.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNama_anak()));
            NIS.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNis()));
            Kelas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            TahunAjaran.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTahun_ajaran()));

            IDHistori.setMinWidth(81);
            Nama.setMinWidth(297);
            NIS.setMinWidth(141);
            Kelas.setMinWidth(151);
            TahunAjaran.setMinWidth(140);
            // Add columns to the TableView
            historiKelasTbl.getColumns().clear(); // Clear existing columns
            historiKelasTbl.getColumns().addAll(IDHistori, Nama, NIS, Kelas, TahunAjaran);

            // Set the data to the TableView
            historiKelasTbl.getItems().clear(); // Clear existing data
            historiKelasTbl.getItems().addAll(listHistoryMengajar);

            // Add listener for selection change
            historiKelasTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Handle the event when a row is clicked
                    System.out.println("Row clicked: " + newSelection);

//                    //selected kelasPerTahun
//                    KelasPerTahun selectedKelasPerTahun = (KelasPerTahun) kelasHistoriMengajarCb.getSelectionModel().getSelectedItem();
//                    //selected tahun ajaran
//                    TahunAjaran selectedTahunAjaran = (TahunAjaran) tahunAjaranHistoriMengajarCb.getSelectionModel().getSelectedItem();
                    HistoriKelasAnak selectedHistoriAnak = historiKelasTbl.getSelectionModel().getSelectedItem();
                    KelasPerTahun newKelas = kelasHistoriKelasCb.getItems().stream()
                            .filter(kelas -> kelas.getID_KELAS_PER_TAHUN() == selectedHistoriAnak.getID_HISTORI_KELAS_ANAK())
                            .findFirst()
                            .orElse(null);
                    kelasHistoriKelasCb.getSelectionModel().select(newKelas);
                    TahunAjaran newTahunAjaran = tahunAjaranHistoriKelasCb.getItems().stream()
                            .filter(tahunAjaran -> tahunAjaran.getID_TAHUN_AJARAN() == selectedHistoriAnak.getID_HISTORI_KELAS_ANAK())
                            .findFirst()
                            .orElse(null);
                    tahunAjaranHistoriKelasCb.getSelectionModel().select(newTahunAjaran);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //fxml function
    @FXML
    public void editAssign() {
        Connection con = null;
        try {
            con = ConnectionManager.getConnection();

            //pass the selected class to the DAO
            KelasPerTahun selectedKelas = kelasHistoriKelasCb.getSelectionModel().getSelectedItem();
            HistoriKelasAnakDao.setSelectedClass(selectedKelas);

            loadMenuAssignKelasAnak();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(con);
        }
    }



    //function to change the window
    private void loadMenuAssignKelasAnak() {
        loadFXML("/com/example/pbo_sekolahminggu/views/transactional.data/assignHistoriKelasAnak.fxml");
    }

    private void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            // Now switch the scene or pane
            historiKelasAncPane.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately (e.g., show error message)
        }
    }

    private void alertWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //EXPORT--------------------
    @FXML
    public void export() {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter excelFilter = new FileChooser.ExtensionFilter("Microsoft Excel Spreadsheet (*.xlsx)", "*.xlsx");
        FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("Portable Document Format files (*.pdf)", "*.pdf");
        chooser.getExtensionFilters().add(pdfFilter);
        chooser.getExtensionFilters().add(excelFilter);

        chooser.setInitialDirectory(new File("C:\\Users"));
        File file = chooser.showSaveDialog(historiKelasTbl.getScene().getWindow());
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
        KelasPerTahun selectedKelas = kelasHistoriKelasCb.getSelectionModel().getSelectedItem();
        if (selectedKelas == null) {
            alertWarning("Silahkan pilih kelas terlebih dahulu!");
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

            // Judul
            //buat nama kelas
            String kelasParalel = (selectedKelas.getKelasParalel() == null) ? "" :  selectedKelas.getKelasParalel();

            Paragraph title = new Paragraph("Laporan Kehadiran total dalam 1 tahun - Kelas " + selectedKelas.getNamaKelas() +" " + kelasParalel + " " + selectedKelas.getTahunAjaran())
                    .setTextAlignment(TextAlignment.LEFT)
                    .setBold()
                    .setFontSize(20);
            com.itextpdf.layout.element.Cell titleCell = new com.itextpdf.layout.element.Cell().add(title);
            titleCell.setBorder(Border.NO_BORDER);
            titleCell.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
            headerTable.addCell(titleCell);

            doc.add(headerTable);

            // Membuat tabel dengan kolom yang sesuai
            Table table = new Table(UnitValue.createPercentArray(new float[]{20, 40, 40})).useAllAvailableWidth();

            // Header Tabel
            String[] headers = {"ID Anak", "Nama Anak", "Total Kehadiran"};
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
//            HistoriKelasAnakDao.setSelectedClass(kelasHistoriKelasCb.getSelectionModel().getSelectedItem());
            Map<String, Object[]> data = HistoriKelasAnakDao.getAllArrayObject(con, selectedKelas);

            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object[] row = data.get(key);
                System.out.println("ID Anak: " + row[0]);
                System.out.println("Nama Anak: " + row[1]);
                System.out.println("Total Kehadiran: " + row[2]);

                Paragraph idAnakParagraph = new Paragraph(String.valueOf(row[0]));
                idAnakParagraph.setTextAlignment(TextAlignment.CENTER);
                Cell idAnakCell = new Cell().add(idAnakParagraph);
                table.addCell(idAnakCell);

                Paragraph namaAnakParagraph = new Paragraph(String.valueOf(row[1]));
                namaAnakParagraph.setTextAlignment(TextAlignment.CENTER);
                Cell namaAnakCell = new Cell().add(namaAnakParagraph);
                table.addCell(namaAnakCell);

                Paragraph totalKehadiranParagraph = new Paragraph(String.valueOf(row[2]));
                totalKehadiranParagraph.setTextAlignment(TextAlignment.CENTER);
                Cell totalKehadiranCell = new Cell().add(totalKehadiranParagraph);
                table.addCell(totalKehadiranCell);
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
        KelasPerTahun selectedKelas = kelasHistoriKelasCb.getSelectionModel().getSelectedItem();
        if (selectedKelas == null) {
            alertWarning("Silahkan pilih kelas terlebih dahulu!");
            return;
        }
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("Kehadiran Anak");

        FileOutputStream out = null;
        Connection con = null;

        try {
            con = ConnectionManager.getConnection();
            int rowid = 0;
            String kelasParalel = (selectedKelas.getKelasParalel() == null) ? "" :  selectedKelas.getKelasParalel();

            // Judul
            // Mengatur style untuk judul
            XSSFRow titleRow = spreadsheet.createRow(rowid++);
            titleRow.setHeightInPoints(30); // Set tinggi baris untuk judul
            XSSFCell titleCell = titleRow.createCell(0);
            String judul = "Laporan Kehadiran total dalam 1 tahun - Kelas " + selectedKelas.getNamaKelas() + " " + kelasParalel + " " + selectedKelas.getTahunAjaran();
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
            String[] headers = {"ID Anak", "Nama Anak", "Total Kehadiran"};
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
            Map<String, Object[]> data = HistoriKelasAnakDao.getAllArrayObject(con, selectedKelas);
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

            int[] columnWidths = {4000, 7000, 5000};

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
