package com.example.pbo_sekolahminggu.controllers.transactional.data;

import com.example.pbo_sekolahminggu.beans.master.data.Kebaktian;
import com.example.pbo_sekolahminggu.beans.master.data.TahunAjaran;
import com.example.pbo_sekolahminggu.beans.transactional.data.KehadiranGuru;
import com.example.pbo_sekolahminggu.beans.transactional.data.KelasPerTahun;
import com.example.pbo_sekolahminggu.dao.master.data.KebaktianDao;
import com.example.pbo_sekolahminggu.dao.master.data.TahunAjaranDao;
import com.example.pbo_sekolahminggu.dao.transactional.data.KehadiranAnakDao;
import com.example.pbo_sekolahminggu.dao.transactional.data.KehadiranGuruDao;
import com.example.pbo_sekolahminggu.dao.transactional.data.KelasPerTahunDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
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
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class KehadiranGuruController implements Initializable {
    @FXML
    TableView<KehadiranGuru> kehadiranGuruTbl;
    @FXML
    TableColumn<KehadiranGuru, String> idKehadiranCol, namaCol, nipCol, kelasCol, kebaktianCol, tanggalCol, presensiCol;
    @FXML
    ComboBox<TahunAjaran> tahunAjaranKehadiranGuruCb;
    @FXML
    ComboBox<KelasPerTahun> kelasKehadiranGuruCb;
    @FXML
    ComboBox<Kebaktian> kebaktianKehadiranGuruCb;
    @FXML
    private AnchorPane kehadiranGuruAncPane;

    ObservableList<KehadiranGuru> dataKehadiranGuru ;


    ObservableList<KelasPerTahun> dataKelas = FXCollections.observableArrayList();
    ObservableList<TahunAjaran> tahunAjaranList = FXCollections.observableArrayList();
    ObservableList<Kebaktian> dataKebaktian = FXCollections.observableArrayList();

    private Connection conn;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateKelasTable();
        tahunAjaranKehadiranGuruCb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TahunAjaran>() {
            @Override
            public void changed(ObservableValue<? extends TahunAjaran> observable, TahunAjaran oldValue, TahunAjaran newValue) {
                if (newValue != null) {
                    System.out.println("Selection changed to: " + newValue.toString());

                    filterDataKelas();
                    filterDataKebaktian();
                    kelasKehadiranGuruCb.getSelectionModel().clearSelection();
                    kebaktianKehadiranGuruCb.getSelectionModel().clearSelection();
                }
            }
        });

        try {
            fillTahunAjaranCb();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
//        tahunAjaranHistoriMengajarCb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TahunAjaran>() {
//            @Override
//            public void changed(ObservableValue<? extends TahunAjaran> observable, TahunAjaran oldValue, TahunAjaran newValue) {
//                if (newValue != null) {
//                    System.out.println("Selection changed to: " + newValue.toString());
//
//                    filterDataKelas();
//                }
//            }
//        });
    }


    public void filterDataKebaktian() {
        TahunAjaran tahunSelected = tahunAjaranKehadiranGuruCb.getSelectionModel().getSelectedItem();
        if (tahunSelected == null) {
            alertWarning("Silahkan pilih tahun ajaran terlebih dahulu.");
            return;
        }

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            //filtered data of kebaktian
            ArrayList<Kebaktian> filteredKebaktian = KebaktianDao.getFilteredKebaktian(con, tahunSelected);

            // Convert to ObservableList and update data Kebaktian
            ObservableList<Kebaktian> observableKebaktian= FXCollections.observableArrayList(filteredKebaktian);
            dataKebaktian.setAll(observableKebaktian);

            // Update ChoiceBox items
            kebaktianKehadiranGuruCb.setItems(dataKebaktian);

            // Set StringConverter if needed
            kebaktianKehadiranGuruCb.setConverter(new StringConverter<Kebaktian>() {
                @Override
                public String toString(Kebaktian object) {
                    return object.getJenisKebaktian() + " (" + object.getTanggal() + ")";
                }

                @Override
                public Kebaktian fromString(String string) {
                    return ((ObservableList<Kebaktian>)kebaktianKehadiranGuruCb.getItems()).stream()
                            .filter(selectedKebaktian -> string.equals(selectedKebaktian.getJenisKebaktian()))
                            .findAny()
                            .orElse(null);
                }
            });
            // Optionally, select an item in the ChoiceBox
            if (!dataKebaktian.isEmpty()) {
                kelasKehadiranGuruCb.getSelectionModel().selectFirst();
            }

            System.out.println("Filtered kebaktian loaded successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching filtered classes: " + e.getMessage(), e);
        } finally {
            ConnectionManager.close(con);
        }
//        kebaktianKehadiranGuruCb.show();
    }

    public void fillTahunAjaranCb() throws SQLException {
        try {
            // Populate tahunAjaranList from database
            tahunAjaranList.addAll(TahunAjaranDao.getAll(ConnectionManager.getConnection()));

            // Set items and converter for ChoiceBox
            tahunAjaranKehadiranGuruCb.setItems(tahunAjaranList);

            // Set up StringConverter
            tahunAjaranKehadiranGuruCb.setConverter(new StringConverter<TahunAjaran>() {
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
            ArrayList<KehadiranGuru> listKehadiranGuru = KehadiranGuruDao.getAll(ConnectionManager.getConnection());

            // Set cell value factory for each TableColumn
            idKehadiranCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getID_KEHADIRAN_GURU())));
            namaCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNama()));
            nipCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNip()));
            kelasCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            kebaktianCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getJenisKebaktian()));
            tanggalCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTanggal())));
            presensiCol.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().isPresensi() ? "Hadir" : "Tidak Hadir")));


            // Add columns to the TableView
            kehadiranGuruTbl.getColumns().clear(); // Clear existing columns
            kehadiranGuruTbl.getColumns().addAll(idKehadiranCol, namaCol, nipCol, kelasCol, kebaktianCol, tanggalCol, presensiCol);

            // Set the data to the TableView
            kehadiranGuruTbl.getItems().clear(); // Clear existing data
            kehadiranGuruTbl.getItems().addAll(listKehadiranGuru);

            // Add listener for selection change
            kehadiranGuruTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Handle the event when a row is clicked
                    System.out.println("Row clicked: " + newSelection);
                }
                KehadiranGuru selectedKehadiranGuru = kehadiranGuruTbl.getSelectionModel().getSelectedItem();
                KelasPerTahun newKelas = kelasKehadiranGuruCb.getItems().stream()
                        .filter(kelas -> kelas.getID_KELAS_PER_TAHUN() == selectedKehadiranGuru.getID_KELAS_PER_TAHUN())
                        .findFirst()
                        .orElse(null);
                kelasKehadiranGuruCb.getSelectionModel().select(newKelas);
                TahunAjaran newTahunAjaran = tahunAjaranKehadiranGuruCb.getItems().stream()
                        .filter(tahunAjaran -> tahunAjaran.getID_TAHUN_AJARAN() == selectedKehadiranGuru.getID_TAHUN_AJARAN())
                        .findFirst()
                        .orElse(null);
                tahunAjaranKehadiranGuruCb.getSelectionModel().select(newTahunAjaran);
                Kebaktian newKebaktian = kebaktianKehadiranGuruCb.getItems().stream()
                        .filter(kebaktian -> kebaktian.getID_KEBAKTIAN() == selectedKehadiranGuru.getID_KEBAKTIAN())
                        .findFirst()
                        .orElse(null);
                kebaktianKehadiranGuruCb.getSelectionModel().select(newKebaktian);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void filterDataKelas() {
        TahunAjaran tahunSelected = tahunAjaranKehadiranGuruCb.getSelectionModel().getSelectedItem();
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
            kelasKehadiranGuruCb.setItems(dataKelas);

            // Set StringConverter if needed
            kelasKehadiranGuruCb.setConverter(new StringConverter<KelasPerTahun>() {
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
//                kelasKehadiranGuruCb.getSelectionModel().selectFirst();
//            }

            System.out.println("Filtered classes loaded successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching filtered classes: " + e.getMessage(), e);
        } finally {
            ConnectionManager.close(con);
        }
//        kelasHistoriMengajarCb.show();
    }

    public void edit() {
        try {
            conn = ConnectionManager.getConnection();

            KelasPerTahun selectedKelas = kelasKehadiranGuruCb.getSelectionModel().getSelectedItem();
            Kebaktian selectedKebaktian = kebaktianKehadiranGuruCb.getSelectionModel().getSelectedItem();
            KehadiranGuruDao.setSelectedKelas(selectedKelas);
            KehadiranGuruDao.setSelectedKebaktian(selectedKebaktian);

            //get the data kehadiran to check if it's empty or not
            dataKehadiranGuru = FXCollections.observableArrayList(KehadiranGuruDao.getSpecial(conn, selectedKelas, selectedKebaktian));

            if (dataKehadiranGuru.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Konfirmasi pengisian data kehadiran");
                alert.setHeaderText(null);
                alert.setContentText("Tidak ada data kehadiran guru yang ditemukan. Isi data kehadiran kelas ini?");

                // Add buttons to the alert
                ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().setAll(cancelButton, confirmButton);

                // Show the alert and wait for the response
                Connection finalCon = conn;
                Optional<ButtonType> pilihan = alert.showAndWait();

                // Handle the user's response
                if (pilihan.isPresent() && pilihan.get() == confirmButton) {
                    KehadiranGuruDao.populateTblKehadiranGuru(finalCon); // untuk mengisi kehadiran anak jika untuk kelas dan kebaktian yang terpilih, belum ada datanya
                } else return;
            }
            loadMenuAssignKehadiranGuru();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    ConnectionManager.close(conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadMenuAssignKehadiranGuru() {
        loadFXML("/com/example/pbo_sekolahminggu/views/transactional.data/assignKehadiranGuru.fxml");
    }

    private void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            // Now switch the scene or pane
            kehadiranGuruAncPane.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately (e.g., show error message)
        }
    }

    public void showFilter(){
        try {
            //selected nama kelas
            KelasPerTahun selectedKelas = (KelasPerTahun) kelasKehadiranGuruCb.getSelectionModel().getSelectedItem();
            //selected tahun ajaran
            TahunAjaran selectedTahunAjaran = (TahunAjaran) tahunAjaranKehadiranGuruCb.getSelectionModel().getSelectedItem();
            //selected kebaktian
            Kebaktian selectedKebaktian = (Kebaktian) kebaktianKehadiranGuruCb.getSelectionModel(). getSelectedItem();



            // Get the ArrayList of Guru objects from the database
            ArrayList<KehadiranGuru> listKehadiranGuru = KehadiranGuruDao.get(ConnectionManager.getConnection(), selectedKebaktian.getID_KEBAKTIAN());

            // Set cell value factory for each TableColumn
            idKehadiranCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getID_KEHADIRAN_GURU())));
            namaCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNama()));
            nipCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNip()));
            kelasCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKelas()));
            kebaktianCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getJenisKebaktian()));
            tanggalCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTanggal())));
            presensiCol.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().isPresensi() ? "Hadir" : "Tidak Hadir")));


            // Add columns to the TableView
            kehadiranGuruTbl.getColumns().clear(); // Clear existing columns
            kehadiranGuruTbl.getColumns().addAll(idKehadiranCol, namaCol, nipCol, kelasCol, kebaktianCol, tanggalCol, presensiCol);

            // Set the data to the TableView
            kehadiranGuruTbl.getItems().clear(); // Clear existing data
            kehadiranGuruTbl.getItems().addAll(listKehadiranGuru);

            // Add listener for selection change
            kehadiranGuruTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Handle the event when a row is clicked
                    System.out.println("Row clicked: " + newSelection);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void alertWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void clear(){
        tahunAjaranKehadiranGuruCb.getSelectionModel().clearSelection();
        kelasKehadiranGuruCb.getSelectionModel().clearSelection();
        kebaktianKehadiranGuruCb.getSelectionModel().clearSelection();
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
        File file = chooser.showSaveDialog(kehadiranGuruTbl.getScene().getWindow());
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
            Paragraph title = new Paragraph("Laporan Kehadiran Guru Berdasarkan Tahun Ajaran");
            title.setTextAlignment(TextAlignment.CENTER);
            title.setBold();
            doc.add(title);

            // Membuat tabel dengan kolom yang sesuai
            Table table = new Table(UnitValue.createPercentArray(new float[]{40, 40, 20})).useAllAvailableWidth();

            // Header Tabel
            String[] headers = {"Nama Guru", "Tahun Ajaran", "Total Kehadiran"};
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
            Map<String, Object[]> data = KehadiranGuruDao.getAllArrayObject(con);

            // Mengisi tabel dengan data
            for (Object[] rowData : data.values()) {
                // Pastikan data yang diambil sesuai dengan urutan kolom yang diharapkan
                String namaGuru = rowData[0].toString();
                String tahunAjaran = rowData[1].toString();
                String totalKehadiran = rowData[2].toString();

                // Data Nama Guru
                com.itextpdf.layout.element.Cell namaGuruCell = new com.itextpdf.layout.element.Cell();
                Paragraph namaGuruParagraph = new Paragraph(namaGuru);
                namaGuruParagraph.setTextAlignment(TextAlignment.CENTER);
                namaGuruCell.add(namaGuruParagraph);
                table.addCell(namaGuruCell);

                // Data Tahun Ajaran
                com.itextpdf.layout.element.Cell tahunAjaranCell = new com.itextpdf.layout.element.Cell();
                Paragraph tahunAjaranParagraph = new Paragraph(tahunAjaran);
                tahunAjaranParagraph.setTextAlignment(TextAlignment.CENTER);
                tahunAjaranCell.add(tahunAjaranParagraph);
                table.addCell(tahunAjaranCell);

                // Data Total Kehadiran
                com.itextpdf.layout.element.Cell totalKehadiranCell = new com.itextpdf.layout.element.Cell();
                Paragraph totalKehadiranParagraph = new Paragraph(totalKehadiran);
                totalKehadiranParagraph.setTextAlignment(TextAlignment.CENTER);
                totalKehadiranCell.add(totalKehadiranParagraph);
                table.addCell(totalKehadiranCell);
            }

            doc.add(table);
            doc.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void exportToExcel(File file) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("Laporan Kehadiran Guru");

        FileOutputStream out = null;
        Connection con = null;

        try {
            con = ConnectionManager.getConnection();
            int rowid = 0;

            // Judul
            XSSFRow titleRow = spreadsheet.createRow(rowid++);
            XSSFCell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Laporan Kehadiran Guru Berdasarkan Tahun Ajaran");

            // Export Header
            XSSFRow headerRow = spreadsheet.createRow(rowid++);
            String[] headers = {"Nama Guru", "Tahun Ajaran", "Total Kehadiran"};
            int cellCounter = 0;
            for (String header : headers) {
                XSSFCell cell = headerRow.createCell(cellCounter++);
                cell.setCellValue(header);
            }

            // Export Data
            Map<String, Object[]> data = KehadiranGuruDao.getAllArrayObject(con);
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
