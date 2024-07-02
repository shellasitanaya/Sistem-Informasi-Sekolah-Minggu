package com.example.pbo_sekolahminggu.controllers.transactional.data;

import com.example.pbo_sekolahminggu.beans.master.data.TahunAjaran;
import com.example.pbo_sekolahminggu.beans.transactional.data.HistoriKelasAnak;
import com.example.pbo_sekolahminggu.beans.transactional.data.KelasPerTahun;
import com.example.pbo_sekolahminggu.dao.master.data.TahunAjaranDao;
import com.example.pbo_sekolahminggu.dao.transactional.data.HistoriKelasAnakDao;
import com.example.pbo_sekolahminggu.dao.transactional.data.KelasPerTahunDao;
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
    private ComboBox<KelasPerTahun> kelasHistoriKelasCb;
    @FXML
    private Button showHistoriKelas;
    @FXML
    private ComboBox<com.example.pbo_sekolahminggu.beans.master.data.TahunAjaran> tahunAjaranHistoriKelasCb;
    @FXML
    private TableColumn<HistoriKelasAnak, String> idHistori, nama, nis, kelas, tahunAjaran;
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
        if (checkKolomError()) return;
        try {
            //selected nama kelas
            KelasPerTahun selectedKelas = (KelasPerTahun) kelasHistoriKelasCb.getSelectionModel().getSelectedItem();
            //selected tahun ajaran
//            TahunAjaran selectedTahunAjaran = (TahunAjaran) tahunAjaranHistoriKelasCb.getSelectionModel().getSelectedItem();
            // Get the ArrayList of Guru objects from the database
            ArrayList<HistoriKelasAnak> listHistoryKelasAnak = HistoriKelasAnakDao.get(ConnectionManager.getConnection(), selectedKelas.getIdKelasPerTahun());

            if (listHistoryKelasAnak.isEmpty()) {
                alertWarning("Belum ada anak yang terdaftar di kelas ini!");
                return;
            }
            // Set cell value factory for each TableColumn
            idHistori.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdHistoriKelasAnak())));
            nama.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNamaAnak()));
            nis.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNis()));
            kelas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            tahunAjaran.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTahunAjaran()));


            // Add columns to the TableView
            historiKelasTbl.getColumns().clear(); // Clear existing columns
            historiKelasTbl.getColumns().addAll(idHistori, nama, nis, kelas, tahunAjaran);

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
            throw new RuntimeException(e);
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
            idHistori = new TableColumn<>("ID Histori");
            nama = new TableColumn<>("Nama");
            nis = new TableColumn<>("NIS");
            kelas = new TableColumn<>("Kelas");
            tahunAjaran = new TableColumn<>("Tahun Ajaran");


            // Set cell value factory for each TableColumn
            idHistori.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdHistoriKelasAnak())));
            nama.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNamaAnak()));
            nis.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNis()));
            kelas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            tahunAjaran.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTahunAjaran()));

            idHistori.setMinWidth(81);
            nama.setMinWidth(297);
            nis.setMinWidth(141);
            kelas.setMinWidth(151);
            tahunAjaran.setMinWidth(140);
            // Add columns to the TableView
            historiKelasTbl.getColumns().clear(); // Clear existing columns
            historiKelasTbl.getColumns().addAll(idHistori, nama, nis, kelas, tahunAjaran);

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
                            .filter(kelas -> kelas.getIdKelasPerTahun() == selectedHistoriAnak.getIdHistoriKelasAnak())
                            .findFirst()
                            .orElse(null);
                    kelasHistoriKelasCb.getSelectionModel().select(newKelas);
                    TahunAjaran newTahunAjaran = tahunAjaranHistoriKelasCb.getItems().stream()
                            .filter(tahunAjaran -> tahunAjaran.getIdTahunAjaran() == selectedHistoriAnak.getIdHistoriKelasAnak())
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
        if (checkKolomError()) return;
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

    @FXML
    public void checkTahunAjaran() {
        TahunAjaran tahunSelected = tahunAjaranHistoriKelasCb.getSelectionModel().getSelectedItem();
        if (tahunSelected == null) {
            alertWarning("Harap pilih tahun ajaran terlebih dahulu.");
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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean checkKolomError() {
        TahunAjaran tahunSelected = tahunAjaranHistoriKelasCb.getSelectionModel().getSelectedItem();
        KelasPerTahun kelasSelected = kelasHistoriKelasCb.getSelectionModel().getSelectedItem();
        if (tahunSelected == null || kelasSelected == null) {
            alertWarning("Harap isi semua kolom.");
            return true;
        }
        return false;
    }

    //EXPORT--------------------
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
        }
        System.out.println(file.getAbsolutePath());
        PdfDocument pdfDoc = null;
        try {
            pdfDoc = new PdfDocument(new PdfWriter(file.getAbsolutePath()));
            Document doc = new Document(pdfDoc);

            // Judul
            //buat nama kelas
            String kelasParalel = (selectedKelas.getKelasParalel() == null) ? "" :  selectedKelas.getKelasParalel();

            Paragraph title = new Paragraph("Laporan Kehadiran total dalam 1 tahun - Kelas " + selectedKelas.getNamaKelas() +" " + kelasParalel);
            title.setTextAlignment(TextAlignment.CENTER);
            title.setBold();
            doc.add(title);

            // Membuat tabel dengan kolom yang sesuai
            Table table = new Table(UnitValue.createPercentArray(new float[]{20, 40, 40})).useAllAvailableWidth();

            // Logo header
            Image logo = new Image(ImageDataFactory.create("src/main/resources/com/example/pbo_sekolahminggu/images/exportIcon.png"));
            logo.setWidth(UnitValue.createPercentValue(20));
            com.itextpdf.layout.element.Cell logoCell = new com.itextpdf.layout.element.Cell(1, 3).add(logo);
            logoCell.setBorder(Border.NO_BORDER);
            table.addCell(logoCell);

            // Header Tabel
            String[] headers = {"ID Anak", "Nama Anak", "Total Kehadiran"};
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
        }
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("Kehadiran Anak");

        FileOutputStream out = null;
        Connection con = null;

        try {
            con = ConnectionManager.getConnection();
            int rowid = 0;

            // Judul
            XSSFRow titleRow = spreadsheet.createRow(rowid++);
            XSSFCell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Laporan Kehadiran total dalam 1 tahun kelas");

            // Export Header
            XSSFRow headerRow = spreadsheet.createRow(rowid++);
            String[] headers = {"ID Anak", "Nama Anak", "Total Kehadiran"};
            int cellCounter = 0;
            for (String header : headers) {
                XSSFCell cell = headerRow.createCell(cellCounter++);
                cell.setCellValue(header);
            }

            // Export Data
            Map<String, Object[]> data = HistoriKelasAnakDao.getAllArrayObject(con, selectedKelas);

            Set<String> keyid = data.keySet();
            for (String key : keyid) {
                XSSFRow row = spreadsheet.createRow(rowid++);
                Object[] objectArr = data.get(key);

                XSSFCell cellIdAnak = row.createCell(0);
                cellIdAnak.setCellValue(String.valueOf(objectArr[0]));

                XSSFCell cellNamaAnak = row.createCell(1);
                cellNamaAnak.setCellValue(String.valueOf(objectArr[1]));

                XSSFCell cellTotalKehadiran = row.createCell(2);
                cellTotalKehadiran.setCellValue(String.valueOf(objectArr[2]));

                spreadsheet.autoSizeColumn(0);
                spreadsheet.autoSizeColumn(1);
                spreadsheet.autoSizeColumn(2);
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
