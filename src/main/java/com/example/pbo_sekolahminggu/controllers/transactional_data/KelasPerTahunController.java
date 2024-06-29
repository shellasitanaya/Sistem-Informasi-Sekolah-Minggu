package com.example.pbo_sekolahminggu.controllers.transactional_data;

import com.example.pbo_sekolahminggu.beans.master_data.TahunAjaran;
import com.example.pbo_sekolahminggu.beans.transactional_data.KelasPerTahun;
import com.example.pbo_sekolahminggu.dao.transactional_data.KelasPerTahunDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import com.itextpdf.io.image.ImageDataFactory;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class KelasPerTahunController implements Initializable {

    @FXML
    TableView<KelasPerTahun> kelasPerTahunTbl;
    @FXML
    TableColumn<KelasPerTahun, String> idKelasPerTahun, namaKelasPerTahun, namaPararelKelasPerTahun, tahunAjaranKelasPerTahun, namaRuangKelasPerTahun;
//    @FXML
//    ChoiceBox<Kelas> namaKelasPerTahunCb;
//    ChoiceBox<KelasP>

    ObservableList<TahunAjaran> namaKelasList = FXCollections.observableArrayList();
    ObservableList<TahunAjaran> kelasPararelList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateKelasTable();
    }

//    public void fillKelasCb() throws SQLException {
//        try {
//            // Populate tahunAjaranList from database
//            namaKelasList.addAll(TahunAjaranDao.getAll(ConnectionManager.getConnection()));
//
//            // Set items and converter for ChoiceBox
//            tahunAjaranHistoriMengajarCb.setItems(tahunAjaranList);
//
//            // Set up StringConverter
//            tahunAjaranHistoriMengajarCb.setConverter(new StringConverter<TahunAjaran>() {
//                @Override
//                public String toString(TahunAjaran object) {
//                    return object != null ? object.getTahunAjaran() : "";
//                }
//
//                @Override
//                public TahunAjaran fromString(String string) {
//                    return tahunAjaranList.stream()
//                            .filter(ta -> ta.getTahunAjaran().equals(string))
//                            .findFirst()
//                            .orElse(null); // Return null if not found (though you should ideally handle this case better)
//                }
//            });
//        } catch (SQLException e) {
//            throw new RuntimeException("Error populating tahun ajaran list", e);
//        }
//    }

    public void populateKelasTable() {
        System.out.println("ha");
        try {
            // Get the ArrayList of Guru objects from the database
            ArrayList<KelasPerTahun> listHistoryMengajar = KelasPerTahunDao.getAll(ConnectionManager.getConnection());

            // Set cell value factory for each TableColumn
            idKelasPerTahun.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getID_KELAS_PER_TAHUN())));
            namaKelasPerTahun.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNamaKelas()));
            namaPararelKelasPerTahun.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelasParalel()));
            tahunAjaranKelasPerTahun.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTahunAjaran()));
            namaRuangKelasPerTahun.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRuangKelas()));

            // Add columns to the TableView
            kelasPerTahunTbl.getColumns().clear(); // Clear existing columns
            kelasPerTahunTbl.getColumns().addAll(idKelasPerTahun, namaKelasPerTahun, namaPararelKelasPerTahun, tahunAjaranKelasPerTahun, namaRuangKelasPerTahun);

            // Set the data to the TableView
            kelasPerTahunTbl.getItems().clear(); // Clear existing data
            kelasPerTahunTbl.getItems().addAll(listHistoryMengajar);

            // Add listener for selection change
            kelasPerTahunTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Handle the event when a row is clicked
                    System.out.println("Row clicked: " + newSelection);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --------------------------------------------------
    @FXML
    public void export() {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter excelFilter = new FileChooser.ExtensionFilter("Microsoft Excel Spreadsheet (*.xlsx)", "*.xlsx");
        FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("Portable Document Format files (*.pdf)", "*.pdf");
        chooser.getExtensionFilters().add(pdfFilter);
        chooser.getExtensionFilters().add(excelFilter);

        chooser.setInitialDirectory(new File("C:\\Users"));
        File file = chooser.showSaveDialog(kelasPerTahunTbl.getScene().getWindow());
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
            Paragraph title = new Paragraph("Laporan Data Kelas");
            title.setTextAlignment(TextAlignment.CENTER);
            title.setBold();
            doc.add(title);

            Table table = new Table(UnitValue.createPercentArray(new float[] {20, 40, 40})).useAllAvailableWidth();

            // Logo header (Ubah sesuai kebutuhan)
            Image logo = new Image(ImageDataFactory.create("src/main/resources/com/example/pbo_sekolahminggu/images/exportIcon.png"));
            logo.setWidth(UnitValue.createPercentValue(50));
            Cell logoCell = new Cell(1, 2).add(logo);
            logoCell.setBorder(Border.NO_BORDER);
            table.addCell(logoCell);

            Cell emptyCell = new Cell(1, 1);
            emptyCell.setBorder(Border.NO_BORDER);
            table.addCell(emptyCell);

            String[] headers = {"Nama Kelas", "Kelas Paralel", "Jumlah Murid"};
            for (String header : headers) {
                Cell headerCell = new Cell();
                Paragraph headerText = new Paragraph(header);
                headerText.setTextAlignment(TextAlignment.CENTER);
                headerText.setBold();
                headerCell.add(headerText);
                table.addCell(headerCell);
            }

            // Data Table
            KelasPerTahun selectedKelas = kelasPerTahunTbl.getSelectionModel().getSelectedItem();
            Map<String, Object[]> data = KelasPerTahunDao.getAllArrayObject(ConnectionManager.getConnection(), selectedKelas);
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object[] row = data.get(key);
                System.out.println("Nama Kelas: " + row[0]);
                System.out.println("Kelas Paralel: " + row[1]);
                System.out.println("Jumlah Murid: " + row[2]);

                Paragraph namaKelasParagraph = new Paragraph(String.valueOf(row[0]));
                namaKelasParagraph.setTextAlignment(TextAlignment.CENTER);
                Cell namaKelasCell = new Cell().add(namaKelasParagraph);
                table.addCell(namaKelasCell);

                Paragraph kelasParalelParagraph = new Paragraph(String.valueOf(row[1]));
                Cell kelasParalelCell = new Cell().add(kelasParalelParagraph);
                table.addCell(kelasParalelCell);

                Paragraph jumlahMuridParagraph = new Paragraph(String.valueOf(row[2]));
                Cell jumlahMuridCell = new Cell().add(jumlahMuridParagraph);
                table.addCell(jumlahMuridCell);
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
        XSSFSheet spreadsheet = workbook.createSheet("Laporan Data Kelas");

        FileOutputStream out = null;
        Connection con = null;

        try {
            con = ConnectionManager.getConnection();
            int rowid = 0;

            // Judul
            XSSFRow titleRow = spreadsheet.createRow(rowid++);
            XSSFCell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Laporan Data Kelas");

            // Export Header
            XSSFRow headerRow = spreadsheet.createRow(rowid++);
            String[] headers = {"Nama Kelas", "Kelas Paralel", "Jumlah Murid"};
            int cellCounter = 0;
            for (String header : headers) {
                XSSFCell cell = headerRow.createCell(cellCounter++);
                cell.setCellValue(header);

                spreadsheet.autoSizeColumn(cellCounter - 1);
            }

            // Export Data
            KelasPerTahun selectedKelas = kelasPerTahunTbl.getSelectionModel().getSelectedItem();
            Map<String, Object[]> data = KelasPerTahunDao.getAllArrayObject(con, selectedKelas);
            Set<String> keyid = data.keySet();

            for (String key : keyid) {
                XSSFRow row = spreadsheet.createRow(rowid++);
                Object[] objectArr = data.get(key);

                // Pastikan hanya mengambil Nama Kelas, Kelas Paralel, dan Jumlah Murid
                XSSFCell cellNamaKelas = row.createCell(0);
                cellNamaKelas.setCellValue(String.valueOf(objectArr[0]));

                XSSFCell cellKelasParalel = row.createCell(1);
                cellKelasParalel.setCellValue(String.valueOf(objectArr[1]));

                XSSFCell cellJumlahMurid = row.createCell(2);
                cellJumlahMurid.setCellValue(String.valueOf(objectArr[2]));

                spreadsheet.autoSizeColumn(0);
                spreadsheet.autoSizeColumn(1);
                spreadsheet.autoSizeColumn(2);
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

}