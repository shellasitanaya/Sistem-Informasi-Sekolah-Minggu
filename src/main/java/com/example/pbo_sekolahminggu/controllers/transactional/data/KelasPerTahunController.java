package com.example.pbo_sekolahminggu.controllers.transactional.data;

import com.example.pbo_sekolahminggu.beans.master.data.Kelas;
import com.example.pbo_sekolahminggu.beans.master.data.TahunAjaran;
import com.example.pbo_sekolahminggu.beans.transactional.data.KelasPerTahun;
import com.example.pbo_sekolahminggu.dao.master.data.KelasDao;
import com.example.pbo_sekolahminggu.dao.master.data.TahunAjaranDao;
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
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
    TextField namaPararelKelasPerTahunField, namaRuangKelasPerTahunField;

    ObservableList<Kelas> namaKelasList = FXCollections.observableArrayList();
    ObservableList<TahunAjaran> tahunAjaranList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
    }

    public void addKelasPerTahun(){
        if(!isInputValid()){
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
            e.printStackTrace(); // Handle or log the exception
        }
    }

    public void delete(){
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
            System.out.println("No row selected.");
        }
    }
//
    public void edit() {
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
            System.out.println("No row selected.");
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
        if(!namaRuangKelasPerTahunField.getText().equals("") && namaKelasPerTahunCb.getValue() != null && tahunAjaranKelasPerTahunCb.getValue() != null){
            return true;
        }

        if(namaKelasPerTahunCb.getValue() == null){
            alertWarning("Silahkan pilih Kelas terlebih dahulu.");
        }else if(namaRuangKelasPerTahunField.getText().equals("")){
            alertWarning("Ruang Kelas masih kosong! Mohon isi.");
        }else if(tahunAjaranKelasPerTahunCb.getValue() == null){
            alertWarning("Silahkan pilih Tahun Ajaran terlebih dahulu.");
        }

        return false;
    }

    //function kalo misalnya ada textfield yang kosong, atau kelas yang mau didelete/diedit blm dipilih
    private void alertWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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