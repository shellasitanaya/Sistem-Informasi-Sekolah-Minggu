//package com.example.pbo_sekolahminggu.controllers.transactional.data;
//
//import com.example.pbo_sekolahminggu.beans.master.data.TahunAjaran;
//import com.example.pbo_sekolahminggu.beans.transactional.data.KehadiranGuru;
//import com.example.pbo_sekolahminggu.beans.transactional.data.KelasPerTahun;
//import com.example.pbo_sekolahminggu.dao.transactional.data.KehadiranGuruDao;
//import com.example.pbo_sekolahminggu.utils.ConnectionManager;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.element.Cell;
//import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.layout.element.Table;
//import com.itextpdf.layout.properties.TextAlignment;
//import com.itextpdf.layout.properties.UnitValue;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.stage.FileChooser;
//import org.apache.poi.xssf.usermodel.XSSFCell;
//import org.apache.poi.xssf.usermodel.XSSFRow;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.net.URL;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.ResourceBundle;
//import java.util.Set;
//
//public class KehadiranGuruController implements Initializable {
//    @FXML
//    TableView<KehadiranGuru> kehadiranGuruTbl;
//    @FXML
//    TableColumn<KehadiranGuru, String> idKehadiranCol, namaCol, nipCol, kelasCol, kebaktianCol, tanggalCol, presensiCol;
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        populateKelasTable();
//    }
//
//    public void populateKelasTable() {
//        System.out.println("ha");
//        try {
//            // Get the ArrayList of Guru objects from the database
//            ArrayList<KehadiranGuru> listKehadiranGuru = KehadiranGuruDao.getAll(ConnectionManager.getConnection());
//
//            // Set cell value factory for each TableColumn
//            idKehadiranCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getID_KEHADIRAN_GURU())));
//            namaCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNama()));
//            nipCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNip()));
//            kelasCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
//            kebaktianCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getJenisKebaktian()));
//            tanggalCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTanggal())));
//            presensiCol.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().isPresensi() ? "Hadir" : "Tidak Hadir")));
//
//
//            // Add columns to the TableView
//            kehadiranGuruTbl.getColumns().clear(); // Clear existing columns
//            kehadiranGuruTbl.getColumns().addAll(idKehadiranCol, namaCol, nipCol, kelasCol, kebaktianCol, tanggalCol, presensiCol);
//
//            // Set the data to the TableView
//            kehadiranGuruTbl.getItems().clear(); // Clear existing data
//            kehadiranGuruTbl.getItems().addAll(listKehadiranGuru);
//
//            // Add listener for selection change
//            kehadiranGuruTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
//                if (newSelection != null) {
//                    // Handle the event when a row is clicked
//                    System.out.println("Row clicked: " + newSelection);
//                }
//            });
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // --------------------------------------------------
//    @FXML
//    public void export() {
//        FileChooser chooser = new FileChooser();
//        FileChooser.ExtensionFilter excelFilter = new FileChooser.ExtensionFilter("Microsoft Excel Spreadsheet (*.xlsx)", "*.xlsx");
//        FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("Portable Document Format files (*.pdf)", "*.pdf");
//        chooser.getExtensionFilters().add(pdfFilter);
//        chooser.getExtensionFilters().add(excelFilter);
//
//        chooser.setInitialDirectory(new File("C:\\Users"));
//        File file = chooser.showSaveDialog(kehadiranGuruTbl.getScene().getWindow());
//        FileChooser.ExtensionFilter selectedFilter = chooser.getSelectedExtensionFilter();
//
//        if (file != null) {
//            if (selectedFilter.getExtensions().get(0).equalsIgnoreCase("*.xlsx")) {
//                exportToExcel(file);
//            } else if (selectedFilter.getExtensions().get(0).equalsIgnoreCase("*.pdf")) {
//                exportToPdf(file);
//            }
//        }
//    }
//    private void exportToPdf(File file) {
////        TahunAjaran selectedTahun = cb
//        System.out.println(file.getAbsolutePath());
//        PdfDocument pdfDoc = null;
//        try {
//            pdfDoc = new PdfDocument(new PdfWriter(file.getAbsolutePath()));
//            Document doc = new Document(pdfDoc);
//
//            // Judul
//            Paragraph title = new Paragraph("Laporan Kehadiran Guru Berdasarkan Tahun Ajaran");
//            title.setTextAlignment(TextAlignment.CENTER);
//            title.setBold();
//            doc.add(title);
//
//            // Membuat tabel dengan kolom yang sesuai
//            Table table = new Table(UnitValue.createPercentArray(new float[]{40, 40, 20})).useAllAvailableWidth();
//
//            // Header Tabel
//            String[] headers = {"Nama Guru", "Tahun Ajaran", "Total Kehadiran"};
//            for (String header : headers) {
//                Cell headerCell = new Cell();
//                Paragraph headerParagraph = new Paragraph(header);
//                headerParagraph.setTextAlignment(TextAlignment.CENTER);
//                headerParagraph.setBold();
//                headerCell.add(headerParagraph);
//                table.addCell(headerCell);
//            }
//
//            // Mengambil data dari DAO
//            // KODE BUAT SELECTED TAHUN AJARAN??
//            Connection con = ConnectionManager.getConnection();
//            Map<String, Object[]> data = KehadiranGuruDao.getAllArrayObject(con, ??);
//
//
//            Set<String> keySet = data.keySet();
//            for (String key : keySet) {
//                Object[] row = data.get(key);
//                System.out.println("Nama Guru: " + row[0]);
//                System.out.println("Tahun ajaran: " + row[1]);
//                System.out.println("Total Kehadiran: " + row[2]);
//
//                Paragraph namaGuruParagraph = new Paragraph(String.valueOf(row[0]));
//                namaGuruParagraph.setTextAlignment(TextAlignment.CENTER);
//                Cell namaGuruCell = new Cell().add(namaGuruParagraph);
//                table.addCell(namaGuruCell);
//
//                Paragraph tahunAjaranParagraph = new Paragraph(String.valueOf(row[1]));
//                tahunAjaranParagraph.setTextAlignment(TextAlignment.CENTER);
//                Cell tahunAjaranCell = new Cell().add(tahunAjaranParagraph);
//                table.addCell(tahunAjaranCell);
//
//                Paragraph totalKehadiranParagraph = new Paragraph(String.valueOf(row[2]));
//                totalKehadiranParagraph.setTextAlignment(TextAlignment.CENTER);
//                Cell totalKehadiranCell = new Cell().add(totalKehadiranParagraph);
//                table.addCell(totalKehadiranCell);
//            }
//
//            doc.add(table);
//            doc.close();
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void exportToExcel(File file) {
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet spreadsheet = workbook.createSheet("Laporan Kehadiran Guru");
//
//        FileOutputStream out = null;
//        Connection con = null;
//
//        try {
//            con = ConnectionManager.getConnection();
//            int rowid = 0;
//
//            // Judul
//            XSSFRow titleRow = spreadsheet.createRow(rowid++);
//            XSSFCell titleCell = titleRow.createCell(0);
//            titleCell.setCellValue("Laporan Kehadiran Guru Berdasarkan Tahun Ajaran");
//
//            // Export Header
//            XSSFRow headerRow = spreadsheet.createRow(rowid++);
//            String[] headers = {"Nama Guru", "Tahun Ajaran", "Total Kehadiran"};
//            int cellCounter = 0;
//            for (String header : headers) {
//                XSSFCell cell = headerRow.createCell(cellCounter++);
//                cell.setCellValue(header);    spreadsheet.autoSizeColumn(cellCounter - 1);
//
//            }
//
//            // Export Data
//            // KODE SELECTED TAHUN AJARAN??
//            Map<String, Object[]> data = KehadiranGuruDao.getAllArrayObject(con, ??);
//            Set<String> keyid = data.keySet();
//
//            for (String key : keyid) {
//                XSSFRow row = spreadsheet.createRow(rowid++);
//                Object[] objectArr = data.get(key);
//
//                // Isi data ke dalam kolom Excel
//                XSSFCell cellNamaGuru = row.createCell(0);
//                cellNamaGuru.setCellValue(String.valueOf(objectArr[0]));
//
//                XSSFCell cellTahunAjaran = row.createCell(1);
//                cellTahunAjaran.setCellValue(String.valueOf(objectArr[1]));
//
//                XSSFCell cellTotalKehadiran = row.createCell(2);
//                cellTotalKehadiran.setCellValue(String.valueOf(objectArr[2]));
//
//                spreadsheet.autoSizeColumn(0);
//                spreadsheet.autoSizeColumn(1);
//                spreadsheet.autoSizeColumn(2);
//            }
//            out = new FileOutputStream(file);
//            workbook.write(out);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } finally {
//            ConnectionManager.close(con);
//            try {
//                if (out != null) {
//                    out.close();
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//
//
//
//}