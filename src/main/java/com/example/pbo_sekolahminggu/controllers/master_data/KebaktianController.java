package com.example.pbo_sekolahminggu.controllers.master_data;

import com.example.pbo_sekolahminggu.beans.master_data.Kebaktian;
import com.example.pbo_sekolahminggu.dao.master_data.KebaktianDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
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
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class KebaktianController implements Initializable {
    // text field
    @FXML
    private TextField jenisKebaktianField;
    @FXML
    private TextField kebaktianSearchField;
    @FXML
    private DatePicker tanggalKebaktianPicker;

    // table
    @FXML
    private TableView<Kebaktian> kebaktianTbl;
    private ObservableList<Kebaktian> data;
    private Kebaktian selectedKebaktian = null;

    // button
    @FXML
    private Button clearKebaktianBtn;
    @FXML
    private Button updateKebaktianBtn;
    @FXML
    private Button deleteKebaktianBtn;
    @FXML
    private Button createKebaktianBtn;

    ObservableList<Kebaktian> listKebaktian = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listKebaktian = FXCollections.observableArrayList();
        kebaktianTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        kebaktianTbl.getColumns().clear();

        // Inisialisasi table columns
        TableColumn<Kebaktian, Integer> idCol = new TableColumn<>("ID Kebaktian");
        idCol.setMinWidth(100);
        idCol.setCellValueFactory(new PropertyValueFactory<>("ID_KEBAKTIAN"));

        TableColumn<Kebaktian, String> jenisCol = new TableColumn<>("Jenis Kebaktian");
        jenisCol.setMinWidth(150);
        jenisCol.setCellValueFactory(new PropertyValueFactory<>("jenisKebaktian"));

        TableColumn<Kebaktian, Date> tanggalCol = new TableColumn<>("Tanggal");
        tanggalCol.setMinWidth(150);
        tanggalCol.setCellValueFactory(new PropertyValueFactory<>("tanggal"));

        kebaktianTbl.getColumns().addAll(idCol, jenisCol, tanggalCol);

        refreshData();

        kebaktianTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedKebaktian = newSelection;
                jenisKebaktianField.setText(selectedKebaktian.getJenisKebaktian());
                tanggalKebaktianPicker.setValue(selectedKebaktian.getTanggal().toLocalDate());
            }
        });
    }

    private void refreshData() {
        Connection connection = null;
        try {
            connection = ConnectionManager.getConnection();
            data = FXCollections.observableArrayList(KebaktianDao.getAll(connection));
            listKebaktian.setAll(data);
            kebaktianTbl.setItems(listKebaktian);
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
        String jenis = jenisKebaktianField.getText();
        Date tanggal = Date.valueOf(tanggalKebaktianPicker.getValue());

        if (jenis.isEmpty() || tanggal == null) {
            showErrorMessage("Harap isi semua kolom.");
            return;
        }

        Kebaktian kebaktian = new Kebaktian();
        kebaktian.setJenisKebaktian(jenis);
        kebaktian.setTanggal(tanggal);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            KebaktianDao.create(con, kebaktian);

            listKebaktian = FXCollections.observableArrayList(KebaktianDao.getAll(con));
            kebaktianTbl.setItems(listKebaktian);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Kebaktian berhasil ditambahkan!");
            alert.show();

            clear();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat menambahkan data kebaktian: " + e.getMessage());
            alert.show();
        } finally {
            ConnectionManager.close(con);
        }
    }

    @FXML
    public void update() {
        Kebaktian selected = kebaktianTbl.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorMessage("Pilih kolom yang ingin diupdate.");
            return;
        }

        String jenis = jenisKebaktianField.getText();
        Date tanggal = Date.valueOf(tanggalKebaktianPicker.getValue());

        if (jenis.isEmpty() || tanggal == null) {
            showErrorMessage("Harap isi semua kolom.");
            return;
        }

        selected.setJenisKebaktian(jenis);
        selected.setTanggal(tanggal);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            KebaktianDao.update(con, selected);

            updateKebaktianInList(selected);

            kebaktianTbl.refresh();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Kebaktian berhasil diupdate!");
            alert.show();

            clear();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat mengupdate data kebaktian: " + e.getMessage());
            alert.show();

        } finally {
            ConnectionManager.close(con);
        }
    }


    private void updateKebaktianInList(Kebaktian updatedKebaktian) {
        int index = listKebaktian.indexOf(selectedKebaktian);
        if (index != -1) {
            listKebaktian.set(index, updatedKebaktian);
        }
    }

    @FXML
    public void delete() {
        if (kebaktianTbl.getSelectionModel().getSelectedItems().size() != 0) {
            Kebaktian selected = kebaktianTbl.getSelectionModel().getSelectedItem();
            Connection connection = null;
            try {
                connection = ConnectionManager.getConnection();
                KebaktianDao.delete(connection, selected);
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
        jenisKebaktianField.clear();
        tanggalKebaktianPicker.setValue(null);
        selectedKebaktian = null;
    }

    public String formatDateToString(LocalDate date) {
        if (date == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    @FXML
    public void Search() {
        FilteredList<Kebaktian> filter = new FilteredList<>(listKebaktian, e -> true);

        kebaktianSearchField.textProperty().addListener((Observable, oldValue, newValue) -> {
            filter.setPredicate(predicateKebaktianData -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (predicateKebaktianData.getJenisKebaktian().toLowerCase().contains(searchKey)) {
                    return true;
                }
                String tanggalString = predicateKebaktianData.getTanggal().toString();
                if (tanggalString.toLowerCase().contains(searchKey)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Kebaktian> sortedData = new SortedList<>(filter);
        sortedData.comparatorProperty().bind(kebaktianTbl.comparatorProperty());
        kebaktianTbl.setItems(sortedData);
    }

    // EXPORTT
    @FXML
    public void export() {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter excelFilter = new FileChooser.ExtensionFilter("Microsoft Excel Spreadsheet (*.xlsx)", "*.xlsx");
        FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("Portable Document Format files (*.pdf)", "*.pdf");
        chooser.getExtensionFilters().add(pdfFilter);
        chooser.getExtensionFilters().add(excelFilter);

        chooser.setInitialDirectory(new File("C:\\Users"));
        File file = chooser.showSaveDialog(kebaktianTbl.getScene().getWindow());
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

            // Judul
            Paragraph title = new Paragraph("Laporan Kehadiran Tiap Minggu Di Kelas");
            title.setTextAlignment(TextAlignment.CENTER);
            title.setBold();
            doc.add(title);

            // Membuat tabel dengan kolom yang sesuai
            Table table = new Table(UnitValue.createPercentArray(new float[]{20, 20, 20, 10, 10, 20})).useAllAvailableWidth();

            // Logo header
            Image logo = new Image(ImageDataFactory.create("src/main/resources/com/example/pbo_sekolahminggu/images/exportIcon.png"));
            logo.setWidth(UnitValue.createPercentValue(50));
            com.itextpdf.layout.element.Cell logoCell = new com.itextpdf.layout.element.Cell(1, 6).add(logo);
            logoCell.setBorder(Border.NO_BORDER);
            table.addCell(logoCell);

            // Header Tabel
            String[] headers = {"Jenis Kebaktian", "Tanggal", "Kelas", "Laki-laki", "Perempuan", "Total"};
            for (String header : headers) {
                com.itextpdf.layout.element.Cell headerCell = new com.itextpdf.layout.element.Cell();
                Paragraph headerParagraph = new Paragraph(header);
                headerParagraph.setTextAlignment(TextAlignment.CENTER);
                headerParagraph.setBold();
                headerCell.add(headerParagraph);
                table.addCell(headerCell);
            }

            // Mengambil data dari DAO
            Connection con = ConnectionManager.getConnection();
            Map<String, Object[]> data = KebaktianDao.getAllArrayObject(con);

            // Mengisi tabel dengan data
            for (Object[] rowData : data.values()) {
                for (Object cellData : rowData) {
                    Paragraph cellParagraph = new Paragraph(cellData != null ? cellData.toString() : "");
                    cellParagraph.setTextAlignment(TextAlignment.CENTER);
                    com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell().add(cellParagraph);
                    table.addCell(cell);
                }
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
        XSSFSheet spreadsheet = workbook.createSheet("Kebaktian Data");

        FileOutputStream out = null;
        Connection con = null;

        try {
            con = ConnectionManager.getConnection();
            int rowid = 0;

            // Judul
            XSSFRow titleRow = spreadsheet.createRow(rowid++);
            XSSFCell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Laporan Kehadiran Tiap Minggu Di Kelas");

            // Export Header
            XSSFRow headerRow = spreadsheet.createRow(rowid++);
            String[] headers = {"Jenis Kebaktian", "Tanggal", "Kelas", "Laki-laki", "Perempuan", "Total"};
            int cellCounter = 0;
            for (String header : headers) {
                XSSFCell cell = headerRow.createCell(cellCounter++);
                cell.setCellValue(header);
            }

            // Export Data
            Map<String, Object[]> data = KebaktianDao.getAllArrayObject(con);
            Set<String> keyid = data.keySet();

            for (String key : keyid) {
                XSSFRow row = spreadsheet.createRow(rowid++);
                Object[] objectArr = data.get(key);
                int cellid = 0;

                for (Object obj : objectArr) {
                    XSSFCell cell = row.createCell(cellid++);
                    cell.setCellValue(String.valueOf(obj));
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                spreadsheet.autoSizeColumn(i);
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
