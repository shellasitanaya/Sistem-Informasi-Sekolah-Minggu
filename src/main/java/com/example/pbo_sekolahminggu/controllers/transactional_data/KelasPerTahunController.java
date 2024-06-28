package com.example.pbo_sekolahminggu.controllers.transactional_data;

import com.example.pbo_sekolahminggu.beans.master_data.TahunAjaran;
import com.example.pbo_sekolahminggu.beans.transactional_data.KelasPerTahun;
import com.example.pbo_sekolahminggu.dao.master_data.GuruDao;
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

            //  judul
            Paragraph title = new Paragraph("Laporan Kelas dan Jumlah Anak Pada Tiap Tahun Ajaran");
            title.setTextAlignment(TextAlignment.CENTER);
            title.setBold();
            doc.add(title);

            Table table = new Table(UnitValue.createPercentArray(new float[] {10, 30, 60})).useAllAvailableWidth();
            //Logo header
            Image logo = new Image(ImageDataFactory.create("src/main/resources/com/example/pbo_sekolahminggu/images/exportIcon.png"));
            logo.setWidth(UnitValue.createPercentValue(50));
            Cell logoCell = new Cell(1, 2).add(logo);
            logoCell.setBorder(Border.NO_BORDER);
            table.addCell(logoCell);

            Cell emptyCell = new Cell(1, 1);
            emptyCell.setBorder(Border.NO_BORDER);
            table.addCell(emptyCell);

            //Header Table
            for (int i = 0; i< kelasPerTahunTbl.getColumns().size(); i++) {
                TableColumn col = (TableColumn) kelasPerTahunTbl.getColumns().get(i);
                Cell headerCell = new Cell();
                title = new Paragraph(col.getText());
                title.setTextAlignment(TextAlignment.CENTER);
                title.setBold();

                headerCell.add(title);
                table.addCell(headerCell);
            }
            table.addCell(emptyCell);

            //Table Data
            for (int i = 0; i< kelasPerTahunTbl.getItems().size(); i++) {
                KelasPerTahun data = kelasPerTahunTbl.getItems().get(i);

                //Data id
                Paragraph idParagraph = new Paragraph(String.valueOf(data.getID_KELAS_PER_TAHUN()));
                idParagraph.setTextAlignment(TextAlignment.CENTER);
                Cell idCell = new Cell().add(idParagraph);
                table.addCell(idCell);

                // Nama Kelas
                Paragraph namaKelasParagraph = new Paragraph(data.getNamaKelas());
                Cell namaKelasCell = new Cell().add(namaKelasParagraph);
                table.addCell(namaKelasCell);

                // Kelas Paralel
                Paragraph kelasParalelParagraph = new Paragraph(data.getKelasParalel());
                Cell kelasParalelCell = new Cell().add(kelasParalelParagraph);
                table.addCell(kelasParalelCell);

                // Tahun Ajaran
                Paragraph tahunAjaranParagraph = new Paragraph(data.getTahunAjaran());
                Cell tahunAjaranCell = new Cell().add(tahunAjaranParagraph);
                table.addCell(tahunAjaranCell);

                // Ruang Kelas
                Paragraph ruangKelasParagraph = new Paragraph(data.getRuangKelas());
                Cell ruangKelasCell = new Cell().add(ruangKelasParagraph);
                table.addCell(ruangKelasCell);


                //3rd empty cell
                table.addCell(emptyCell);
            }

            doc.add(table);
            doc.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private void exportToExcel(File file) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("Guru Data");

        FileOutputStream out = null;
        Connection con = null;

        try {
            con = ConnectionManager.getConnection();
            int rowid = 0;

            // judul
            XSSFRow titleRow = spreadsheet.createRow(rowid++);
            XSSFCell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Laporan Kelas dan Jumlah Anak Pada Tiap Tahun Ajaran");

            //Export Header
            XSSFRow headerRow = spreadsheet.createRow(rowid++);
            Object[] headerArr = kelasPerTahunTbl.getColumns().toArray();

            int cellCounter = 0;
            for (Object obj : headerArr) {
                XSSFCell cell = headerRow.createCell(cellCounter++);
                cell.setCellValue(((TableColumn) obj).getText());
            }

            //Export Data
            Map<String, Object[]> data = GuruDao.getAllArrayObject(con);
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