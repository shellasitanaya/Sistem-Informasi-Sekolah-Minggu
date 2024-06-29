package com.example.pbo_sekolahminggu.controllers;

import com.example.pbo_sekolahminggu.beans.HistoriMengajar;
import com.example.pbo_sekolahminggu.beans.Kelas;
import com.example.pbo_sekolahminggu.beans.KelasPerTahun;
import com.example.pbo_sekolahminggu.dao.HistoriMengajarDao;
import com.example.pbo_sekolahminggu.dao.TahunAjaranDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import com.example.pbo_sekolahminggu.beans.TahunAjaran;
import javafx.util.StringConverter;
import com.example.pbo_sekolahminggu.dao.KelasPerTahunDao;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.example.pbo_sekolahminggu.beans.HistoriKelasAnak;
import com.example.pbo_sekolahminggu.beans.HistoriMengajar;
import com.example.pbo_sekolahminggu.beans.KelasPerTahun;
import com.example.pbo_sekolahminggu.dao.HistoriKelasAnakDao;
import com.example.pbo_sekolahminggu.dao.HistoriMengajarDao;
import com.example.pbo_sekolahminggu.dao.TahunAjaranDao;
import com.example.pbo_sekolahminggu.dao.HistoriMengajarDao;
import com.example.pbo_sekolahminggu.dao.KelasPerTahunDao;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import com.example.pbo_sekolahminggu.beans.TahunAjaran;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
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
    TableColumn<HistoriMengajar, String> IDHistori, Nama, NIP, Kelas, TahunAjaran;
    @FXML
    ComboBox<TahunAjaran> tahunAjaranHistoriMengajarCb;
    @FXML
    ComboBox<KelasPerTahun> kelasHistoriMengajarCb;

    ObservableList<TahunAjaran> tahunAjaranList = FXCollections.observableArrayList();
    ObservableList<KelasPerTahun> dataKelas = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateKelasTable();
        try {
            fillTahunAjaranCb();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void showFilter() {
        try {
            //selected nama kelas
            KelasPerTahun selectedKelas = (KelasPerTahun) kelasHistoriMengajarCb.getSelectionModel().getSelectedItem();
            //selected tahun ajaran
//            TahunAjaran selectedTahunAjaran = (TahunAjaran) tahunAjaranHistoriKelasCb.getSelectionModel().getSelectedItem();
            // Get the ArrayList of Guru objects from the database
            ArrayList<HistoriMengajar> listHistoryKelasGuru = HistoriMengajarDao.get(ConnectionManager.getConnection(), selectedKelas.getID_KELAS_PER_TAHUN());

            if (listHistoryKelasGuru.isEmpty()) {
                alertWarning("Belum ada anak yang terdaftar di kelas ini!");
            }
            // Set cell value factory for each TableColumn
            IDHistori.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getID_HISTORI_MENGAJAR())));
            Nama.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNamaGuru()));
            NIP.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNip()));
            Kelas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            TahunAjaran.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTahunAjaran()));

            // Add columns to the TableView
            historiMengajarTbl.getColumns().clear(); // Clear existing columns
            historiMengajarTbl.getColumns().addAll(IDHistori, Nama, NIP, Kelas, TahunAjaran);

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
            if (!dataKelas.isEmpty()) {
                kelasHistoriMengajarCb.getSelectionModel().selectFirst();
            }

            System.out.println("Filtered classes loaded successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching filtered classes: " + e.getMessage(), e);
        } finally {
            ConnectionManager.close(con);
        }
        kelasHistoriMengajarCb.show();
    }


    public void populateKelasTable () {
        System.out.println("ha");
        try {
            // Get the ArrayList of Guru objects from the database
            ArrayList<HistoriMengajar> listHistoryMengajar = HistoriMengajarDao.getAll(ConnectionManager.getConnection());

            // Set cell value factory for each TableColumn
            IDHistori.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getID_HISTORI_MENGAJAR())));
            Nama.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNamaGuru()));
            NIP.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNip()));
            Kelas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            TahunAjaran.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTahunAjaran()));

            // Add columns to the TableView
            historiMengajarTbl.getColumns().clear(); // Clear existing columns
            historiMengajarTbl.getColumns().addAll(IDHistori, Nama, NIP, Kelas, TahunAjaran);

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
    // --------------------------------------------------
    @FXML
    public void export() {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter excelFilter = new FileChooser.ExtensionFilter("Microsoft Excel Spreadsheet (.xlsx)", ".xlsx");
        FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("Portable Document Format files (.pdf)", ".pdf");
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

            //  judul
            Paragraph title = new Paragraph("Laporan Top 3 Guru yang Mengajar Paling Banyak");
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


            String[] headers = {"ID Guru", "Nama Guru", "Jumlah Mengajar"};
            for (String header : headers) {
                Cell headerCell = new Cell();
                Paragraph headerText = new Paragraph(header);
                headerText.setTextAlignment(TextAlignment.CENTER);
                headerText.setBold();
                headerCell.add(headerText);
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

                // Add empty cell as needed
                table.addCell(emptyCell);
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

            // Judul
            XSSFRow titleRow = spreadsheet.createRow(rowid++);
            XSSFCell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Laporan Top 3 Guru yang Mengajar Paling Banyak\n");

            // Export Header
            XSSFRow headerRow = spreadsheet.createRow(rowid++);
            String[] headers = {"ID Guru", "Nama Guru", "Jumlah Mengajar"};
            int cellCounter = 0;
            for (String header : headers) {
                XSSFCell cell = headerRow.createCell(cellCounter++);
                cell.setCellValue(header);

                spreadsheet.autoSizeColumn(cellCounter - 1);
            }

            // Export Data
            Map<String, Object[]> data = HistoriMengajarDao.getAllArrayObject(con);
            Set<String> keyid = data.keySet();

            for (String key : keyid) {
                XSSFRow row = spreadsheet.createRow(rowid++);
                Object[] objectArr = data.get(key);

                // Pastikan hanya mengambil ID Guru, Nama Guru, dan Jumlah Mengajar
                XSSFCell cellIdGuru = row.createCell(0);
                cellIdGuru.setCellValue(String.valueOf(objectArr[0]));

                XSSFCell cellNamaGuru = row.createCell(1);
                cellNamaGuru.setCellValue(String.valueOf(objectArr[1]));

                XSSFCell cellJumlahMengajar = row.createCell(2);
                cellJumlahMengajar.setCellValue(String.valueOf(objectArr[2]));


                //spreadsheet.autoSizeColumn(0);
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



    //function to change the window
    private void loadMenuAssignKelasGuru () {
        loadFXML("/com/example/pbo_sekolahminggu/views/transactional_data/assignHistoriMengajar.fxml");
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
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}