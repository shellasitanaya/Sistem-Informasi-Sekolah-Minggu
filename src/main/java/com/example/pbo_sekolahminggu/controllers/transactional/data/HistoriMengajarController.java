package com.example.pbo_sekolahminggu.controllers.transactional.data;


import com.example.pbo_sekolahminggu.beans.transactional.data.HistoriKelasAnak;
import com.example.pbo_sekolahminggu.beans.transactional.data.HistoriMengajar;
import com.example.pbo_sekolahminggu.beans.transactional.data.KelasPerTahun;
import com.example.pbo_sekolahminggu.dao.transactional.data.HistoriKelasAnakDao;
import com.example.pbo_sekolahminggu.dao.transactional.data.HistoriMengajarDao;
import com.example.pbo_sekolahminggu.dao.master.data.TahunAjaranDao;
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
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import com.example.pbo_sekolahminggu.beans.master.data.TahunAjaran;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class HistoriMengajarController implements Initializable {
    @FXML
    private AnchorPane historiMengajarAncPane;
    @FXML
    TableView<HistoriMengajar> historiMengajarTbl;
    @FXML
    TableColumn<HistoriMengajar, String> idhistori, nama, nip, kelas, tahunAjaran;
    @FXML
    ComboBox<TahunAjaran> tahunAjaranHistoriMengajarCb;
    @FXML
    ComboBox<KelasPerTahun> kelasHistoriMengajarCb;
    @FXML
    private TextField historiMengajarSearchField;

    ObservableList<TahunAjaran> tahunAjaranList = FXCollections.observableArrayList();
    ObservableList<KelasPerTahun> dataKelas = FXCollections.observableArrayList();
    ObservableList<HistoriMengajar> listHistoriMengajar = FXCollections.observableArrayList() ;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateKelasTable();
        tahunAjaranHistoriMengajarCb.setPromptText(" ");
        kelasHistoriMengajarCb.setPromptText(" ");

        tahunAjaranHistoriMengajarCb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TahunAjaran>() {
            @Override
            public void changed(ObservableValue<? extends TahunAjaran> observable, TahunAjaran oldValue, TahunAjaran newValue) {
                if (newValue != null) {
                    System.out.println("Selection changed to: " + newValue.toString());

                    filterDataKelas();
                    //kosongin cb kelas
                    kelasHistoriMengajarCb.getSelectionModel().clearSelection();
                }
            }
        });

        try {
            fillTahunAjaranCb();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            listHistoriMengajar = FXCollections.
                    observableList(HistoriMengajarDao.getAll(con));
            for (HistoriMengajar historiMengajar: listHistoriMengajar) {
                System.out.println(historiMengajar);
            }
            historiMengajarTbl.setItems(listHistoriMengajar);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(con);
        }
    }
    public void Search() {

        FilteredList<HistoriMengajar> filter = new FilteredList<>(listHistoriMengajar, e -> true);

        historiMengajarSearchField.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate(predicateEmployeeData -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (predicateEmployeeData.getNamaGuru().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateEmployeeData.getNip().toLowerCase().contains(searchKey)) {
                    System.out.println("ada di nama");
                    return true;
                } else if (predicateEmployeeData.getKelas().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateEmployeeData.getTahunAjaran().toLowerCase().contains(searchKey)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<HistoriMengajar> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(historiMengajarTbl.comparatorProperty());
        historiMengajarTbl.setItems(sortList);
    }

    @FXML
    public void showFilter() {
        if (checkKolomError()) return;
        try {
            //selected nama kelas
            KelasPerTahun selectedKelas = (KelasPerTahun) kelasHistoriMengajarCb.getSelectionModel().getSelectedItem();
            //selected tahun ajaran
//            TahunAjaran selectedTahunAjaran = (TahunAjaran) tahunAjaranHistoriKelasCb.getSelectionModel().getSelectedItem();
            // Get the ArrayList of Guru objects from the database
            ArrayList<HistoriMengajar> listHistoryKelasGuru = HistoriMengajarDao.get(ConnectionManager.getConnection(), selectedKelas.getIdKelasPerTahun());

            if (listHistoryKelasGuru.isEmpty()) {
                alertWarning("Belum ada anak yang terdaftar di kelas ini!");
                return;
            }
            // Set cell value factory for each TableColumn
            idhistori.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdHistoriMengajar())));
            idhistori.setMinWidth(80);
            nama.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNamaGuru()));
            nama.setMinWidth(300);
            nip.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNip()));
            nip.setMinWidth(140);
            kelas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            kelas.setMinWidth(150);
            tahunAjaran.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTahunAjaran()));
            tahunAjaran.setMinWidth(140);

            // Add columns to the TableView
            historiMengajarTbl.getColumns().clear(); // Clear existing columns
            historiMengajarTbl.getColumns().addAll(idhistori, nama, nip, kelas, tahunAjaran);

            // Set the data to the TableView
            historiMengajarTbl.getItems().clear(); // Clear existing data
            historiMengajarTbl.getItems().addAll(listHistoryKelasGuru);

            // Add listener for selection change
            historiMengajarTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Handle the event when a row is clicked
                    System.out.println("Row clicked: " + newSelection);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public void fillTahunAjaranCb () throws SQLException {
        try {
            // Populate tahunAjaranList from database
            tahunAjaranList.addAll(TahunAjaranDao.getAll(ConnectionManager.getConnection()));

            // Set items and converter for ChoiceBox
            tahunAjaranHistoriMengajarCb.setItems(tahunAjaranList);

            // Set up StringConverter
            tahunAjaranHistoriMengajarCb.setConverter(new StringConverter<TahunAjaran>() {
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

    public void filterDataKelas () {
        TahunAjaran tahunSelected = tahunAjaranHistoriMengajarCb.getSelectionModel().getSelectedItem();
        if (tahunSelected == null) {
            alertWarning("Harap pilih tahun ajaran terlebih dahulu.");
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
            kelasHistoriMengajarCb.setItems(dataKelas);

            // Set StringConverter if needed
            kelasHistoriMengajarCb.setConverter(new StringConverter<KelasPerTahun>() {
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
//            if (!dataKelas.isEmpty()) {
//                kelasHistoriMengajarCb.getSelectionModel().selectFirst();
//            }

            System.out.println("Filtered classes loaded successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching filtered classes: " + e.getMessage(), e);
        } finally {
            ConnectionManager.close(con);
        }
    }


    public void populateKelasTable () {
        try {
            // Get the ArrayList of Guru objects from the database
            ArrayList<HistoriMengajar> listHistoryMengajar = HistoriMengajarDao.getAll(ConnectionManager.getConnection());

            // Set cell value factory for each TableColumn
            idhistori.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdHistoriMengajar())));
            nama.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNamaGuru()));
            nip.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNip()));
            kelas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            tahunAjaran.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTahunAjaran()));

            // Add columns to the TableView
            historiMengajarTbl.getColumns().clear(); // Clear existing columns
            historiMengajarTbl.getColumns().addAll(idhistori, nama, nip, kelas, tahunAjaran);

            // Set the data to the TableView
            historiMengajarTbl.getItems().clear(); // Clear existing data
            historiMengajarTbl.getItems().addAll(listHistoryMengajar);

            // Add listener for selection change
            historiMengajarTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
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
    public void editAssign() {
        if (checkKolomError()) return;
        Connection con = null;
        try {
            con = ConnectionManager.getConnection();

            //pass the selected class to the DAO
            KelasPerTahun selectedKelas = kelasHistoriMengajarCb.getSelectionModel().getSelectedItem();
            HistoriMengajarDao.setSelectedClass(selectedKelas);

            loadMenuAssignKelasGuru();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(con);
        }
    }

    @FXML
    public void checkTahunAjaran() {
        TahunAjaran tahunSelected = tahunAjaranHistoriMengajarCb.getSelectionModel().getSelectedItem();
        if (tahunSelected == null) {
            alertWarning("Harap pilih tahun ajaran terlebih dahulu.");
        }
    }
    // --------------------------------------------------
    @FXML
    public void export() {
        if (checkKolomError()) {
            return;
        }
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter excelFilter = new FileChooser.ExtensionFilter("Microsoft Excel Spreadsheet (*.xlsx)", "*.xlsx");
        FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("Portable Document Format files (*.pdf)", "*.pdf");
        chooser.getExtensionFilters().add(pdfFilter);
        chooser.getExtensionFilters().add(excelFilter);

        chooser.setInitialDirectory(new File("C:\\Users"));
        File file = chooser.showSaveDialog(historiMengajarTbl.getScene().getWindow());
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
            Paragraph title = new Paragraph("Laporan Top 3 Guru yang Mengajar Paling Banyak")
                    .setTextAlignment(TextAlignment.LEFT)
                    .setBold()
                    .setFontSize(20);
            com.itextpdf.layout.element.Cell titleCell = new com.itextpdf.layout.element.Cell().add(title);
            titleCell.setBorder(Border.NO_BORDER);
            titleCell.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
            headerTable.addCell(titleCell);

            doc.add(headerTable);
            Table table = new Table(UnitValue.createPercentArray(new float[] {10, 30, 60})).useAllAvailableWidth();

            String[] headers = {"ID Guru", "Nama Guru", "Jumlah Mengajar"};
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

            // Table Data
            Map<String, Object[]> data = HistoriMengajarDao.getAllArrayObject(ConnectionManager.getConnection());
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object[] row = data.get(key);
                System.out.println("ID Guru: " + row[0]);
                System.out.println("Nama Guru: " + row[1]);
                System.out.println("Jumlah Mengajar: " + row[2]);

                Paragraph idParagraph = new Paragraph(String.valueOf(row[0]));
                idParagraph.setTextAlignment(TextAlignment.CENTER);
                Cell idCell = new Cell().add(idParagraph);
                table.addCell(idCell);

                Paragraph namaParagraph = new Paragraph(String.valueOf(row[1]));
                Cell namaCell = new Cell().add(namaParagraph);
                table.addCell(namaCell);

                Paragraph jumlahParagraph = new Paragraph(String.valueOf(row[2]));
                Cell jumlahCell = new Cell().add(jumlahParagraph);
                table.addCell(jumlahCell);
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
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("Histori Mengajar Data");

        FileOutputStream out = null;
        Connection con = null;

        try {
            con = ConnectionManager.getConnection();
            int rowid = 0;

            // Mengatur style untuk judul
            XSSFRow titleRow = spreadsheet.createRow(rowid++);
            titleRow.setHeightInPoints(30); // Set tinggi baris untuk judul
            XSSFCell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Laporan Top 3 Guru yang Mengajar Paling Banyak");
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
            String[] headers = {"ID Guru", "Nama Guru", "Jumlah Mengajar"};
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
            Map<String, Object[]> data = HistoriMengajarDao.getAllArrayObject(con);
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

            int[] columnWidths = {4000, 5500, 5000};

            for (int i = 0; i < columnWidths.length; i++) {
                spreadsheet.setColumnWidth(i, columnWidths[i]);
            }

            out = new FileOutputStream(file);
            workbook.write(out);
        } catch (SQLException | IOException e) {
            throw new RuntimeException("Error exporting to Excel", e);
        } finally {
            ConnectionManager.close(con);
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("Error closing FileOutputStream", e);
            }
        }
    }



    //function to change the window
    private void loadMenuAssignKelasGuru () {
        loadFXML("/com/example/pbo_sekolahminggu/views/transactional.data/assignHistoriMengajar.fxml");
    }

    private void loadFXML (String fxmlFile){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            // Now switch the scene or pane
            historiMengajarAncPane.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately (e.g., show error message)
        }
    }

    private void alertWarning (String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean checkKolomError() {
        TahunAjaran tahunSelected = tahunAjaranHistoriMengajarCb.getSelectionModel().getSelectedItem();
        KelasPerTahun kelasSelected = kelasHistoriMengajarCb.getSelectionModel().getSelectedItem();
        if (tahunSelected == null || kelasSelected == null) {
            alertWarning("Harap pilih isi kolom.");
            return true;
        }
        return false;
    }
}