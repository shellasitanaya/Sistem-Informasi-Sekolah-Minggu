package com.example.pbo_sekolahminggu.controllers.transactional.data;

import com.example.pbo_sekolahminggu.beans.master.data.Kebaktian;
import com.example.pbo_sekolahminggu.beans.master.data.TahunAjaran;
import com.example.pbo_sekolahminggu.beans.transactional.data.KehadiranAnak;
import com.example.pbo_sekolahminggu.beans.transactional.data.KelasPerTahun;
import com.example.pbo_sekolahminggu.dao.master.data.KebaktianDao;
import com.example.pbo_sekolahminggu.dao.master.data.TahunAjaranDao;
import com.example.pbo_sekolahminggu.dao.transactional.data.KehadiranAnakDao;
import com.example.pbo_sekolahminggu.dao.transactional.data.KelasPerTahunDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
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

public class KehadiranAnakController implements Initializable {
    @FXML
    private Button editKehadiranAnakBtn;
    @FXML
    private Button showKehadiranAnakBtn;
    @FXML
    private AnchorPane kehadiranAnakAncPane;
    @FXML
    private TextField kehadiranAnakSearchField;
    @FXML
    private TableView<KehadiranAnak> kehadiranAnakTbl;
    @FXML
    private ComboBox<TahunAjaran> tahunAjaranKehadiranAnakCb;
    @FXML
    private ComboBox<KelasPerTahun> kelasKehadiranAnakCb;
    @FXML
    private ComboBox<Kebaktian> kebaktianKehadiranAnakCb;

    ObservableList<KehadiranAnak> dataKehadiranAnak ;
    ObservableList<TahunAjaran> dataTahunAjaran;
    ObservableList<KelasPerTahun> dataKelas;
    ObservableList<Kebaktian> dataKebaktian;
    private Connection conn;

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dataKehadiranAnak = FXCollections.observableArrayList();
        dataTahunAjaran = FXCollections.observableArrayList();
        dataKelas = FXCollections.observableArrayList();
        dataKebaktian = FXCollections.observableArrayList();
        kehadiranAnakTbl.getSelectionModel().setSelectionMode(
                SelectionMode.SINGLE
        );

        kehadiranAnakTbl.getColumns().clear();
        //column no
        TableColumn idCol = new TableColumn<>("ID Kehadiran");
        idCol.setMinWidth(81);
        idCol.setCellValueFactory(new PropertyValueFactory<KehadiranAnak, Integer>("idKehadiranAnak"));  //yg ini harus sama dgn attribute di beans

        //column nama anak
        TableColumn namaAnakCol = new TableColumn("Nama");
        namaAnakCol.setMinWidth(188);
        namaAnakCol.setCellValueFactory(
                new PropertyValueFactory<KehadiranAnak, String>("namaAnak"));

        TableColumn nisCOL = new TableColumn("NIS");
        nisCOL.setMinWidth(128);
        nisCOL.setCellValueFactory(
                new PropertyValueFactory<KehadiranAnak, String>("nis"));

        TableColumn namaKelasCol = new TableColumn("Kelas");
        namaKelasCol.setMinWidth(127);
        namaKelasCol.setCellValueFactory(
                new PropertyValueFactory<KehadiranAnak, String>("kelas"));

        TableColumn kebaktianCol = new TableColumn("Kebaktian");
        kebaktianCol.setMinWidth(88);
        kebaktianCol.setCellValueFactory(
                new PropertyValueFactory<KehadiranAnak, String>("kebaktian"));

        TableColumn tanggalCol = new TableColumn("Tanggal");
        tanggalCol.setMinWidth(106);
        tanggalCol.setCellValueFactory(
                new PropertyValueFactory<KehadiranAnak, String>("tglKebaktian"));

        TableColumn presensiCol = new TableColumn("Presensi");
        presensiCol.setMinWidth(92);
        presensiCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<KehadiranAnak, String>, ObservableValue<String>>() {
            @Override
            public javafx.beans.value.ObservableValue<String> call(TableColumn.CellDataFeatures<KehadiranAnak, String> cellData) {
                return new SimpleStringProperty(cellData.getValue().isPresensi() ? "Hadir" : "Tidak Hadir");
            }
        });
        kehadiranAnakTbl.getColumns().addAll(idCol, namaAnakCol, nisCOL, namaKelasCol, kebaktianCol, tanggalCol, presensiCol);

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            dataTahunAjaran.addAll(TahunAjaranDao.getAll(con));
            //populate the tahun ajaran choice box
            tahunAjaranKehadiranAnakCb.setItems(dataTahunAjaran);
            tahunAjaranKehadiranAnakCb.setConverter(new StringConverter<TahunAjaran>() {
                @Override
                public String toString(TahunAjaran object) {
                    return object.getTahunAjaran();
                }

                @Override
                public TahunAjaran fromString(String string) {
                    return ((ObservableList<TahunAjaran>) tahunAjaranKehadiranAnakCb.getItems()).stream()
                            .filter(selectedKelas -> string.equals(selectedKelas.getTahunAjaran()))
                            .findAny()
                            .orElse(null);
                }
            });
            tahunAjaranKehadiranAnakCb.getSelectionModel().select(0);

            TahunAjaran tahunSelected = tahunAjaranKehadiranAnakCb.getSelectionModel().getSelectedItem();

            //data kelas
            dataKelas.addAll(KelasPerTahunDao.getAll(con));
            kelasKehadiranAnakCb.setItems(dataKelas);

            kelasKehadiranAnakCb.setConverter(new StringConverter<KelasPerTahun>() {
                @Override
                public String toString(KelasPerTahun object) {
                    String paralel;
                    if (object.getKelasParalel() == null)  {
                        paralel = "";
                    } else paralel = " " + object.getKelasParalel();
                    return object.getNamaKelas() + paralel;
                }

                @Override
                public KelasPerTahun fromString(String string) {
                    return ((ObservableList<KelasPerTahun>)kelasKehadiranAnakCb.getItems()).stream()
                            .filter(selectedKelas -> string.equals(selectedKelas.getNamaKelas()))
                            .findAny()
                            .orElse(null);
                }
            });
            kelasKehadiranAnakCb.getSelectionModel().select(0);

            //data dropdown kebaktian
            dataKebaktian.addAll(KebaktianDao.getAll((con)));
            kebaktianKehadiranAnakCb.setItems(dataKebaktian);
            kebaktianKehadiranAnakCb.setConverter(new StringConverter<Kebaktian>() {
                @Override
                public String toString(Kebaktian object) {
                    return object.getJenisKebaktian() + " (" + object.getTanggal() + ")";
                }

                @Override
                public Kebaktian fromString(String string) {
                    return ((ObservableList<Kebaktian>)kebaktianKehadiranAnakCb.getItems()).stream()
                            .filter(selectedKebaktian -> string.equals(selectedKebaktian.getJenisKebaktian()))
                            .findAny()
                            .orElse(null);
                }
            });
            kebaktianKehadiranAnakCb.getSelectionModel().select(0);

            //get the table data
            dataKehadiranAnak = FXCollections.observableArrayList(KehadiranAnakDao.getAll(con));
            kehadiranAnakTbl.setItems(dataKehadiranAnak);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            ConnectionManager.close(con);
        }

//        tahunAjaranKehadiranAnakCb.setValue(null);
    }

    @FXML
    public void show() {
        //cek input kosong atau tidak
        if (checkComboBox()) return;

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            refreshTable(con);
            if (refreshTable(con)) {  //klo data kehadiran untuk kelas dan kebaktian yang dipilih, kosonk
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Konfirmasi pengisian data kehadiran");
                alert.setHeaderText(null);
                alert.setContentText("Tidak ada data kehadiran anak yang ditemukan. Isi data kehadiran anak di kelas dan kebaktian ini?");

                // Add buttons to the alert
                ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().setAll(cancelButton, confirmButton);

                // Show the alert and wait for the response
                Connection finalCon = con;
                Optional<ButtonType> pilihan = alert.showAndWait();

                // Handle the user's response
                if (pilihan.isPresent() && pilihan.get() == confirmButton) {
                    //set the data to be passed
                    KelasPerTahun selectedKelas = kelasKehadiranAnakCb.getSelectionModel().getSelectedItem();
                    Kebaktian selectedKebaktian = kebaktianKehadiranAnakCb.getSelectionModel().getSelectedItem();
                    KehadiranAnakDao.setSelectedKelas(selectedKelas);
                    KehadiranAnakDao.setSelectedKebaktian(selectedKebaktian);

                    KehadiranAnakDao.populateTblKehadiranAnak(finalCon); // untuk mengisi kehadiran anak jika untuk kelas dan kebaktian yang terpilih, belum ada datanya
                    loadMenuAssignKehadiranAnak(); //move to the next window
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            ConnectionManager.close(con);
        }
    }

    @FXML
    public void edit() {
        if (checkComboBox()) return; //check if one of the combobox is null

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            KelasPerTahun selectedKelas = kelasKehadiranAnakCb.getSelectionModel().getSelectedItem();
            Kebaktian selectedKebaktian = kebaktianKehadiranAnakCb.getSelectionModel().getSelectedItem();
            KehadiranAnakDao.setSelectedKelas(selectedKelas);
            KehadiranAnakDao.setSelectedKebaktian(selectedKebaktian);

            // Get the data kehadiran
            dataKehadiranAnak = FXCollections.observableArrayList(KehadiranAnakDao.getAllFiltered(con, selectedKelas, selectedKebaktian));

            if (dataKehadiranAnak.isEmpty()) {
                alertWarning("Data kehadiran yang terpilih belum tersedia!");
                return;
            }
            loadMenuAssignKehadiranAnak();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(con);
        }
    }

    @FXML
    public void filterDataKelas() {
        TahunAjaran tahunSelected = tahunAjaranKehadiranAnakCb.getSelectionModel().getSelectedItem();
        if (tahunSelected == null) {
            alertWarning("Silahkan pilih tahun ajaran terlebih dahulu.");
            return;
        }

        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            //filtered data of kelas per tahun
            ArrayList<KelasPerTahun> filteredClasses = KelasPerTahunDao.getFilteredClasses(con, tahunSelected);

            // Convert to ObservableList and update dataKelas
            ObservableList<KelasPerTahun> observableClasses = FXCollections.observableArrayList(filteredClasses);

            // Update ChoiceBox items
            kelasKehadiranAnakCb.setItems(observableClasses);

            // Set StringConverter if needed
            kelasKehadiranAnakCb.setConverter(new StringConverter<KelasPerTahun>() {
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
                kelasKehadiranAnakCb.getSelectionModel().selectFirst();
            }

            System.out.println("Filtered classes loaded successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching filtered classes: " + e.getMessage(), e);
        } finally {
            ConnectionManager.close(con);
        }
        kelasKehadiranAnakCb.show();
    }

    @FXML
    public void filterDataKebaktian() {
        TahunAjaran tahunSelected = tahunAjaranKehadiranAnakCb.getSelectionModel().getSelectedItem();
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

            // Update ChoiceBox items
            kebaktianKehadiranAnakCb.setItems(observableKebaktian);

            // Set StringConverter if needed
            kebaktianKehadiranAnakCb.setConverter(new StringConverter<Kebaktian>() {
                @Override
                public String toString(Kebaktian object) {
                    return object.getJenisKebaktian() + " (" + object.getTanggal() + ")";
                }

                @Override
                public Kebaktian fromString(String string) {
                    return ((ObservableList<Kebaktian>)kebaktianKehadiranAnakCb.getItems()).stream()
                            .filter(selectedKebaktian -> string.equals(selectedKebaktian.getJenisKebaktian()))
                            .findAny()
                            .orElse(null);
                }
            });
            // Optionally, select an item in the ChoiceBox
            if (!dataKebaktian.isEmpty()) {
                kelasKehadiranAnakCb.getSelectionModel().selectFirst();
            }

            System.out.println("Filtered kebaktian loaded successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching filtered classes: " + e.getMessage(), e);
        } finally {
            ConnectionManager.close(con);
        }
        kebaktianKehadiranAnakCb.show();
    }

    //refresh view tabel biar terlihat perubahan
    private boolean refreshTable(Connection con) {
        boolean empty = true;
        KelasPerTahun selectedKelas = kelasKehadiranAnakCb.getSelectionModel().getSelectedItem();
        Kebaktian selectedKbk = kebaktianKehadiranAnakCb.getSelectionModel().getSelectedItem();
        //get the table data
        dataKehadiranAnak = FXCollections.observableArrayList(KehadiranAnakDao.getAllFiltered(con, selectedKelas, selectedKbk));
        if (!dataKehadiranAnak.isEmpty()) {
            kehadiranAnakTbl.setItems(dataKehadiranAnak);
            empty = false;
        }
        return empty;
    }

    private void loadMenuAssignKehadiranAnak() {
        loadFXML("/com/example/pbo_sekolahminggu/views/transactional.data/assignKehadiranAnak.fxml");
    }

    private void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            // Now switch the scene or pane
            kehadiranAnakAncPane.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately (e.g., show error message)
        }
    }

    //function kalo misalnya ada textfield yang kosong, atau kelas yang mau didelete/diedit blm dipilih
    private void alertWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean checkComboBox() {
        if (kebaktianKehadiranAnakCb.getSelectionModel().getSelectedItem() == null || kelasKehadiranAnakCb.getSelectionModel().getSelectedItem() == null ||
                tahunAjaranKehadiranAnakCb.getSelectionModel().getSelectedItem() == null) {
            alertWarning("Harap pilih semua kolom.");
            return true;
        }
        return false;
    }

    // --------------------------------------------------
    @FXML
    public void export() {
        if (checkComboBox()) return;
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter excelFilter = new FileChooser.ExtensionFilter("Microsoft Excel Spreadsheet (*.xlsx)", "*.xlsx");
        FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("Portable Document Format files (*.pdf)", "*.pdf");
        chooser.getExtensionFilters().add(pdfFilter);
        chooser.getExtensionFilters().add(excelFilter);

        chooser.setInitialDirectory(new File("C:\\Users"));
        File file = chooser.showSaveDialog(kehadiranAnakTbl.getScene().getWindow());
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
        TahunAjaran selectedTahun = tahunAjaranKehadiranAnakCb.getSelectionModel().getSelectedItem();
        System.out.println(file.getAbsolutePath());
        PdfDocument pdfDoc = null;
        try {
            pdfDoc = new PdfDocument(new PdfWriter(file.getAbsolutePath()));
            Document doc = new Document(pdfDoc);

            // Judul
            Paragraph title = new Paragraph("Laporan Kehadiran Anak di Kelas");
            title.setTextAlignment(TextAlignment.CENTER);
            title.setBold();
            doc.add(title);

            // Membuat tabel dengan kolom yang sesuai
            Table table = new Table(UnitValue.createPercentArray(new float[]{20, 20, 20, 20, 20})).useAllAvailableWidth();

            // Logo header (jika ada)
//             Image logo = new Image(ImageDataFactory.create("src/main/resources/com/example/pbo_sekolahminggu/images/exportIcon.png"));
//             logo.setWidth(UnitValue.createPercentValue(50));
//             com.itextpdf.layout.element.Cell logoCell = new com.itextpdf.layout.element.Cell(1, 6).add(logo);
//             logoCell.setBorder(Border.NO_BORDER);
//             table.addCell(logoCell);

            // Header Tabel
            String[] headers = {"ID Histori Kelas Anak", "NIS", "Nama Anak", "Kelas", "Max Kehadiran"};
            for (String header : headers) {
                Cell headerCell = new Cell();
                Paragraph headerText = new Paragraph(header);
                headerText.setTextAlignment(TextAlignment.CENTER);
                headerText.setBold();
                headerCell.add(headerText);
                table.addCell(headerCell);
            }

            // Mengambil data dari DAO
            Connection con = ConnectionManager.getConnection();
            Map<String, Object[]> data = KehadiranAnakDao.getAllArrayObject(con, selectedTahun);

            // Mengisi tabel dengan data
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object[] row = data.get(key);
                // debug
                System.out.println("ID Histori Kelas Anak: " + row[0]);
                System.out.println("NIS: " + row[1]);
                System.out.println("Nama Anak: " + row[2]);
                System.out.println("Kelas: " + row[3]);
                System.out.println("Max Kehadiran: " + row[4]);

                Cell idHistoriKelasAnakCell = new Cell().add(new Paragraph(String.valueOf(row[0])));
                table.addCell(idHistoriKelasAnakCell);

                Cell nisCell = new Cell().add(new Paragraph(String.valueOf(row[1])));
                table.addCell(nisCell);

                Cell namaAnakCell = new Cell().add(new Paragraph(String.valueOf(row[2])));
                table.addCell(namaAnakCell);

                Cell kelasCell = new Cell().add(new Paragraph(String.valueOf(row[3])));
                table.addCell(kelasCell);

                Cell maxKehadiranCell = new Cell().add(new Paragraph(String.valueOf(row[4])));
                table.addCell(maxKehadiranCell);
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
        TahunAjaran selectedTahun = tahunAjaranKehadiranAnakCb.getSelectionModel().getSelectedItem();
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("Kehadiran Anak Data");

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
            String[] headers = {"ID Histori Kelas Anak", "NIS", "Nama Anak", "Kelas", "Max Kehadiran"};
            int cellCounter = 0;
            for (String header : headers) {
                XSSFCell cell = headerRow.createCell(cellCounter++);
                cell.setCellValue(header);

                spreadsheet.autoSizeColumn(cellCounter - 1);
            }

            // Export Data
            Map<String, Object[]> data = KehadiranAnakDao.getAllArrayObject(con, selectedTahun);
            Set<String> keyid = data.keySet();

            for (String key : keyid) {
                XSSFRow row = spreadsheet.createRow(rowid++);
                Object[] objectArr = data.get(key);

                XSSFCell cellIdHistori = row.createCell(0);
                cellIdHistori.setCellValue(String.valueOf(objectArr[0]));

                XSSFCell cellNIS = row.createCell(1);
                cellNIS.setCellValue(String.valueOf(objectArr[1]));

                XSSFCell cellNamaAnak = row.createCell(2);
                cellNamaAnak.setCellValue(String.valueOf(objectArr[2]));

                XSSFCell cellKelas = row.createCell(3);
                cellKelas.setCellValue(String.valueOf(objectArr[3]));

                XSSFCell cellMaxKehadiran = row.createCell(4);
                cellMaxKehadiran.setCellValue(String.valueOf(objectArr[4]));

                spreadsheet.autoSizeColumn(0);
                spreadsheet.autoSizeColumn(1);
                spreadsheet.autoSizeColumn(2);
                spreadsheet.autoSizeColumn(3);
                spreadsheet.autoSizeColumn(4);
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