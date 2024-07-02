package com.example.pbo_sekolahminggu.controllers.transactional.data;

import com.example.pbo_sekolahminggu.beans.master.data.Kelas;
import com.example.pbo_sekolahminggu.beans.master.data.TahunAjaran;
import com.example.pbo_sekolahminggu.beans.transactional.data.KehadiranGuru;
import com.example.pbo_sekolahminggu.beans.transactional.data.KelasPerTahun;
import com.example.pbo_sekolahminggu.dao.master.data.KelasDao;
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
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class KelasPerTahunController implements Initializable {

    @FXML
    TableView<KelasPerTahun> kelasPerTahunTbl;
    @FXML
    TableColumn<KelasPerTahun, String> idKelasPerTahun, namaKelasPerTahun, namaPararelKelasPerTahun, tahunAjaranKelasPerTahun, namaRuangKelasPerTahun;
    @FXML
    ComboBox<Kelas> namaKelasPerTahunCb;
    @FXML
    ComboBox<TahunAjaran> tahunAjaranKelasPerTahunCb;
    @FXML
    private TextField kelasPerTahunSearchField;
    @FXML
    TextField namaPararelKelasPerTahunField, namaRuangKelasPerTahunField;

    ObservableList<Kelas> namaKelasList = FXCollections.observableArrayList();
    ObservableList<TahunAjaran> tahunAjaranList = FXCollections.observableArrayList();
    ObservableList<KelasPerTahun> listKelasPerTahun = FXCollections.observableArrayList() ;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        namaKelasPerTahunCb.setPromptText(" ");
        tahunAjaranKelasPerTahunCb.setPromptText(" ");
        namaKelasPerTahunCb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Kelas>() {

            @Override
            public void changed(ObservableValue<? extends Kelas> observableValue, Kelas kelas, Kelas t1) {

            }

            public void changedInKelasPerTahun(ObservableValue<? extends TahunAjaran> observable, TahunAjaran oldValue, TahunAjaran newValue) {
                if (newValue != null) {
                    System.out.println("Selection changed to: " + newValue.toString());

                    if(tahunAjaranKelasPerTahunCb.getValue() != null){
                        try {
                            namaPararelKelasPerTahunField.setText(KelasPerTahunDao.getNextParalel(ConnectionManager.getConnection(), namaKelasPerTahunCb.getSelectionModel().getSelectedItem().getIdKelas(), tahunAjaranKelasPerTahunCb.getSelectionModel().getSelectedItem().getIdTahunAjaran()));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
        tahunAjaranKelasPerTahunCb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TahunAjaran>() {
            @Override
            public void changed(ObservableValue<? extends TahunAjaran> observable, TahunAjaran oldValue, TahunAjaran newValue) {
                if (newValue != null) {
                    System.out.println("Selection changed to: " + newValue.toString());

                    if(namaKelasPerTahunCb.getValue() != null){
                        try {
                            namaPararelKelasPerTahunField.setText(KelasPerTahunDao.getNextParalel(ConnectionManager.getConnection(), namaKelasPerTahunCb.getSelectionModel().getSelectedItem().getIdKelas(), tahunAjaranKelasPerTahunCb.getSelectionModel().getSelectedItem().getIdTahunAjaran()));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });

        try {
            fillKelasCb();
            fillTahunAjaranCb();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        populateKelasTable();

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            listKelasPerTahun = FXCollections.
                    observableList(KelasPerTahunDao.getAll(con));
            for (KelasPerTahun kelasPerTahun: listKelasPerTahun) {
                System.out.println(kelasPerTahun);
            }
            kelasPerTahunTbl.setItems(listKelasPerTahun);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(con);
        }
    }
    public void Search() {

        FilteredList<KelasPerTahun> filter = new FilteredList<>(listKelasPerTahun, e -> true);

        kelasPerTahunSearchField.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate(predicateEmployeeData -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (predicateEmployeeData.getRuangKelas().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateEmployeeData.getKelasParalel().toLowerCase().contains(searchKey)) {
                    System.out.println("ada di nama");
                    return true;
                } else if (predicateEmployeeData.getNamaKelas().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateEmployeeData.getTahunAjaran().toLowerCase().contains(searchKey)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<KelasPerTahun> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(kelasPerTahunTbl.comparatorProperty());
        kelasPerTahunTbl.setItems(sortList);
    }

    public void addKelasPerTahun(){
        if(!isInputValid()){
            alertWarning("Harap isi semua kolom.");
            return;
        }
        KelasPerTahun kelasPerTahun = new KelasPerTahun();
        //selected nama kelas
        Kelas selectedKelas = (Kelas) namaKelasPerTahunCb.getSelectionModel().getSelectedItem();
        //selected tahun ajaran
        TahunAjaran selectedTahunAjaran = (TahunAjaran) tahunAjaranKelasPerTahunCb.getSelectionModel().getSelectedItem();
        kelasPerTahun.setIdKelas(selectedKelas.getIdKelas());
        kelasPerTahun.setNamaKelas(selectedKelas.getNamaKelas());
        kelasPerTahun.setKelasParalel(namaPararelKelasPerTahunField.getText());
        kelasPerTahun.setRuangKelas(namaRuangKelasPerTahunField.getText());
        kelasPerTahun.setIdTahunAjaran(selectedTahunAjaran.getIdTahunAjaran());
        kelasPerTahun.setTahunAjaran(selectedTahunAjaran.getTahunAjaran());


        // Establish database connection
        try (Connection con = ConnectionManager.getConnection()) {

            // Call save method
            KelasPerTahunDao.save(con, kelasPerTahun);

            //get id
            kelasPerTahun.setIdKelasPerTahun(KelasPerTahunDao.getIdByProperties(con, kelasPerTahun));

            kelasPerTahunTbl.getItems().add(kelasPerTahun);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Kelas Per Tahun berhasil ditambahkan!");
            alert.show();
//            skinCareDao.resetSequence(con);
        } catch (Exception e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Database Error");
                alert.setHeaderText("Tidak berhasil menyimpan data!");
                alert.setContentText("Terdapat data kelas per tahun yang sama.");
                alert.showAndWait();
            });
        }
    }

    public void delete(){
        if (!isInputValid()) {
            alertWarning("Harap isi semua kolom.");
            return;
        }
        // Get the selected item (i.e., the row that was clicked)
        KelasPerTahun selectedKelas = kelasPerTahunTbl.getSelectionModel().getSelectedItem();

        if (selectedKelas != null) {
            try (Connection con = ConnectionManager.getConnection()) {

                // Call delete method
                KelasPerTahunDao.delete(con, selectedKelas);

                // Remove the selected item from the TableView
                kelasPerTahunTbl.getItems().remove(selectedKelas);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Data Kelas Per Tahun berhasil dihapus!");
                alert.show();
//                skinCareDao.resetSequence(con);
            } catch (Exception e) {
                e.printStackTrace(); // Handle or log the exception
            }
        } else {
            // No row is selected
            alertWarning("Tidak ada data yang dipilih. Silahkan pilih baris tertentu terlebih dahulu!");
        }
    }
//
    public void edit() {
        if (!isInputValid()) {
            alertWarning("Harap isi semua kolom.");
            return;
        }
        // Get the selected item (i.e., the row that was clicked)
        KelasPerTahun selectedKelasPerTahun  = kelasPerTahunTbl.getSelectionModel().getSelectedItem();

        //selected nama kelas
        Kelas selectedKelas = (Kelas) namaKelasPerTahunCb.getSelectionModel().getSelectedItem();
        //selected tahun ajaran
        TahunAjaran selectedTahunAjaran = (TahunAjaran) tahunAjaranKelasPerTahunCb.getSelectionModel().getSelectedItem();

        if (selectedKelasPerTahun != null) {
            // Establish database connection
            try (Connection con = ConnectionManager.getConnection()) {
                // Update the selected item with the edited data
                selectedKelasPerTahun.setIdKelas(selectedKelas.getIdKelas());
                selectedKelasPerTahun.setNamaKelas(selectedKelas.getNamaKelas());
                selectedKelasPerTahun.setRuangKelas(namaRuangKelasPerTahunField.getText());
                selectedKelasPerTahun.setKelasParalel(namaPararelKelasPerTahunField.getText());
                selectedKelasPerTahun.setIdTahunAjaran(selectedTahunAjaran.getIdTahunAjaran());
                selectedKelasPerTahun.setTahunAjaran(selectedTahunAjaran.getTahunAjaran());


                // Call update method
                KelasPerTahunDao.edit(con, selectedKelasPerTahun);

                // Find the index of the selected item in the TableView
                int index = kelasPerTahunTbl.getItems().indexOf(selectedKelasPerTahun);
                if (index != -1) {
                    // Update the item in the TableView's items list
                    kelasPerTahunTbl.getItems().set(index, selectedKelasPerTahun);
//                    skinCareDao.resetSequence(con);
                } else {
                    System.out.println("Selected item not found in the TableView.");
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Data Kelas Per Tahun berhasil diupdate!");
                alert.show();
            } catch (Exception e) {
                e.printStackTrace(); // Handle or log the exception
            }
        } else {
            // No row is selected
            alertWarning("Tidak ada data yang dipilih. Silahkan pilih baris tertentu terlebih dahulu!");
        }
    }


    public void fillKelasCb() throws SQLException {
        try {
            // Populate tahunAjaranList from database
            namaKelasList.addAll(KelasDao.getAll(ConnectionManager.getConnection()));

            // Set items and converter for ChoiceBox
            namaKelasPerTahunCb.setItems(namaKelasList);

            // Set up StringConverter
            namaKelasPerTahunCb.setConverter(new StringConverter<Kelas>() {
                @Override
                public String toString(Kelas object) {
                    return object != null ? object.getNamaKelas() : "";
                }

                @Override
                public Kelas fromString(String string) {
                    return namaKelasList.stream()
                            .filter(ta -> ta.getNamaKelas().equals(string))
                            .findFirst()
                            .orElse(null); // Return null if not found (though you should ideally handle this case better)
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException("Error populating tahun ajaran list", e);
        }
    }

    public void fillTahunAjaranCb() throws SQLException {
        try {
            // Populate tahunAjaranList from database
            tahunAjaranList.addAll(TahunAjaranDao.getAll(ConnectionManager.getConnection()));

            // Set items and converter for ChoiceBox
            tahunAjaranKelasPerTahunCb.setItems(tahunAjaranList);

            // Set up StringConverter
            tahunAjaranKelasPerTahunCb.setConverter(new StringConverter<TahunAjaran>() {
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
            ArrayList<KelasPerTahun> listHistoryMengajar = KelasPerTahunDao.getAll(ConnectionManager.getConnection());

            // Set cell value factory for each TableColumn
            idKelasPerTahun.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdKelasPerTahun())));
            idKelasPerTahun.setMinWidth(115);
            namaKelasPerTahun.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNamaKelas()));
            namaKelasPerTahun.setMinWidth(165);
            namaPararelKelasPerTahun.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelasParalel()));
            namaPararelKelasPerTahun.setMinWidth(155);
            tahunAjaranKelasPerTahun.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTahunAjaran()));
            tahunAjaranKelasPerTahun.setMinWidth(185);
            namaRuangKelasPerTahun.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRuangKelas()));
            namaRuangKelasPerTahun.setMinWidth(190);

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
                    KelasPerTahun selectedKelasPerTahun  = kelasPerTahunTbl.getSelectionModel().getSelectedItem();
                    namaPararelKelasPerTahunField.setText(selectedKelasPerTahun.getKelasParalel());
                    namaRuangKelasPerTahunField.setText(selectedKelasPerTahun.getRuangKelas());
                    Kelas newKelas = namaKelasPerTahunCb.getItems().stream()
                            .filter(kelas -> kelas.getIdKelas() == selectedKelasPerTahun.getIdKelas())
                            .findFirst()
                            .orElse(null);
                    namaKelasPerTahunCb.getSelectionModel().select(newKelas);
                    TahunAjaran newTahunAjaran = tahunAjaranKelasPerTahunCb.getItems().stream()
                            .filter(tahunAjaran -> tahunAjaran.getIdTahunAjaran() == selectedKelasPerTahun.getIdTahunAjaran())
                            .findFirst()
                            .orElse(null);
                    tahunAjaranKelasPerTahunCb.getSelectionModel().select(newTahunAjaran);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clear(){
        namaKelasPerTahunCb.getSelectionModel().clearSelection();
        tahunAjaranKelasPerTahunCb.getSelectionModel().clearSelection();
        namaPararelKelasPerTahunField.clear();
        namaRuangKelasPerTahunField.clear();
    }

    public boolean isInputValid(){
//        if(!namaRuangKelasPerTahunField.getText().trim().isEmpty() &&
//                !namaPararelKelasPerTahunField.getText().trim().isEmpty() && namaKelasPerTahunCb.getValue() != null && tahunAjaranKelasPerTahunCb.getValue() != null){
//            return true;
//        }
        return !namaRuangKelasPerTahunField.getText().trim().isEmpty() &&
                !namaPararelKelasPerTahunField.getText().trim().isEmpty() && namaKelasPerTahunCb.getValue() != null && tahunAjaranKelasPerTahunCb.getValue() != null;

//        if(namaKelasPerTahunCb.getValue() == null){
//            alertWarning("Silahkan pilih Kelas terlebih dahulu.");
//        }else if(namaRuangKelasPerTahunField.getText().trim().isEmpty()){
//            alertWarning("Ruang Kelas masih kosong! Mohon isi.");
//        }else if(tahunAjaranKelasPerTahunCb.getValue() == null){
//            alertWarning("Silahkan pilih Tahun Ajaran terlebih dahulu.");
//        }else if(namaPararelKelasPerTahunField.getText().trim().isEmpty()) {
//            alertWarning("Kelas Paralel masih kosong! Mohon isi.");
//        }
    }

    //function kalo misalnya ada textfield yang kosong, atau kelas yang mau didelete/diedit blm dipilih
    private void alertWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // --------------------------------------------------
    @FXML
    public void export() {
        if (!isInputValid()) {
            alertWarning("Harap isi semua kolom.");
            return;
        }
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
        KelasPerTahun selectedKelas = kelasPerTahunTbl.getSelectionModel().getSelectedItem();
        if (selectedKelas == null) {
            alertWarning("Pilih Tahun Ajaran terlebih dahulu!");
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
            Paragraph title = new Paragraph("Laporan Kelas dan Jumlah Murid Diurutkan dari Paling Besar Ke Paling Kecil " + selectedKelas.getTahunAjaran())
                    .setTextAlignment(TextAlignment.LEFT)
                    .setBold()
                    .setFontSize(20);
            com.itextpdf.layout.element.Cell titleCell = new com.itextpdf.layout.element.Cell().add(title);
            titleCell.setBorder(Border.NO_BORDER);
            titleCell.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
            headerTable.addCell(titleCell);

            doc.add(headerTable);

            Table table = new Table(UnitValue.createPercentArray(new float[] {20, 40, 40})).useAllAvailableWidth();

            String[] headers = {"Nama Kelas", "Kelas Paralel", "Jumlah Murid"};
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

            // Data Table
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
        KelasPerTahun selectedKelas = kelasPerTahunTbl.getSelectionModel().getSelectedItem();
        if (selectedKelas == null) {
            alertWarning("Pilih Tahun Ajaran terlebih dahulu!");
            return;
        }
        // Data Table
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("Laporan Data Kelas Per Tahun");

        FileOutputStream out = null;
        Connection con = null;

        try {
            con = ConnectionManager.getConnection();
            int rowid = 0;

            // Judul
            // Mengatur style untuk judul
            XSSFRow titleRow = spreadsheet.createRow(rowid++);
            titleRow.setHeightInPoints(30); // Set tinggi baris untuk judul
            XSSFCell titleCell = titleRow.createCell(0);
            String judul = "Laporan Kelas dan Jumlah Murid Diurutkan dari Paling Besar Ke Paling Kecil " + selectedKelas.getTahunAjaran();
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
            String[] headers = {"Nama Kelas", "Kelas Paralel", "Jumlah Murid"};
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
            Map<String, Object[]> data = KelasPerTahunDao.getAllArrayObject(con, selectedKelas);
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

            int[] columnWidths = {8000, 6500, 6500};

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

}