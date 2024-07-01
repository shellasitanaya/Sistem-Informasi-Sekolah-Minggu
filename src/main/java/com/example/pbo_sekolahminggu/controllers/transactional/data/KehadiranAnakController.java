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
    private ChoiceBox<TahunAjaran> tahunAjaranKehadiranAnakCb;
    @FXML
    private ChoiceBox<KelasPerTahun> kelasKehadiranAnakCb;
    @FXML
    private ChoiceBox<Kebaktian> kebaktianKehadiranAnakCb;

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
        idCol.setCellValueFactory(new PropertyValueFactory<KehadiranAnak, Integer>("ID_KEHADIRAN_ANAK"));  //yg ini harus sama dgn attribute di beans

        //column nama anak
        TableColumn namaAnakCol = new TableColumn("Nama");
        namaAnakCol.setMinWidth(188);
        namaAnakCol.setCellValueFactory(
                new PropertyValueFactory<KehadiranAnak, String>("nama_anak"));

        TableColumn nisCOL = new TableColumn("NIS");
        nisCOL.setMinWidth(128);
        nisCOL.setCellValueFactory(
                new PropertyValueFactory<KehadiranAnak, String>("NIS"));

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
                new PropertyValueFactory<KehadiranAnak, String>("tgl_kebaktian"));

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
        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            refreshTable(con);
            if (refreshTable(con)) {
                alertWarning("Belum ada data kehadiran!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            ConnectionManager.close(con);
        }
    }

    @FXML
    public void edit() {
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
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Konfirmasi pengisian data kehadiran");
                alert.setHeaderText(null);
                alert.setContentText("Tidak ada data kehadiran anak yang ditemukan. Isi data kehadiran kelas ini?");

                // Add buttons to the alert
                ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
                alert.getButtonTypes().setAll(cancelButton, confirmButton);

                // Show the alert and wait for the response
                Connection finalCon = con;
                Optional<ButtonType> pilihan = alert.showAndWait();

                // Handle the user's response
                if (pilihan.isPresent() && pilihan.get() == confirmButton) {
                    KehadiranAnakDao.populateTblKehadiranAnak(finalCon); // untuk mengisi kehadiran anak jika untuk kelas dan kebaktian yang terpilih, belum ada datanya
                } else return;
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

    //    ini untuk dialog button
    private void dialogBox(String message) {
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        Dialog<String> dialog = new Dialog<>();
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().add(okButtonType);
        dialog.showAndWait();
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

            // Membuat table untuk logo dan judul
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 5})).useAllAvailableWidth();
            headerTable.setMarginBottom(10);

            // Menambahkan logo
            Image logo = new Image(ImageDataFactory.create("src/main/resources/com/example/pbo_sekolahminggu/images/sekolahMingguLogo.png"));
            logo.setWidth(UnitValue.createPercentValue(100));
            com.itextpdf.layout.element.Cell logoCell = new com.itextpdf.layout.element.Cell().add(logo);
            logoCell.setBorder(Border.NO_BORDER);
            headerTable.addCell(logoCell);

            // Judul
            Paragraph title = new Paragraph("Laporan Kehadiran Tiap Minggu Di Kelas Pada Tahun" + " " + selectedTahun.getTahunAjaran())
                    .setTextAlignment(TextAlignment.LEFT)
                    .setBold()
                    .setFontSize(20);
            com.itextpdf.layout.element.Cell titleCell = new com.itextpdf.layout.element.Cell().add(title);
            titleCell.setBorder(Border.NO_BORDER);
            titleCell.setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE);
            headerTable.addCell(titleCell);
            doc.add(headerTable);

            // Membuat tabel dengan kolom yang sesuai
            Table table = new Table(UnitValue.createPercentArray(new float[]{20, 20, 20, 20, 20})).useAllAvailableWidth();

            // Header Tabel
            String[] headers = {"ID Histori Kelas Anak", "NIS", "Nama Anak", "Kelas", "Max Kehadiran"};
            Color customColor = new DeviceRgb(39, 106, 207);
            for (String header : headers) {
                Cell headerCell = new Cell();
                Paragraph headerText = new Paragraph(header);
                headerText.setTextAlignment(TextAlignment.CENTER);
                headerText.setBold();
                headerText.setFontColor(ColorConstants.WHITE);
                headerCell.setBackgroundColor(customColor);
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
        } catch (MalformedURLException e) {
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
            titleRow.setHeightInPoints(30); // Set tinggi baris untuk judul
            CellRangeAddress mergedRegion = new CellRangeAddress(0, 0, 0, 5); // merge kolom untuk judul
            spreadsheet.addMergedRegion(mergedRegion);
            XSSFCell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Laporan Kehadiran Tiap Minggu Di Kelas Pada Tahun" + " " + selectedTahun.getTahunAjaran());
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            titleStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setBorderBottom(BorderStyle.THIN); // Border bawah
            titleStyle.setBorderTop(BorderStyle.THIN); // Border atas
            titleStyle.setBorderLeft(BorderStyle.THIN); // Border kiri
            titleStyle.setBorderRight(BorderStyle.THIN); // Border kanan
            Font titleFont = workbook.createFont();
            titleFont.setColor(IndexedColors.WHITE.getIndex()); // Warna teks
            titleFont.setBold(true);
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);

            // Export Header
            XSSFRow headerRow = spreadsheet.createRow(rowid++);
            String[] headers = {"ID Histori Kelas Anak", "NIS", "Nama Anak", "Kelas", "Max Kehadiran"};
            int cellCounter = 0;
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex()); // Warna latar belakang
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN); // Border bawah
            headerStyle.setBorderTop(BorderStyle.THIN); // Border atas
            headerStyle.setBorderLeft(BorderStyle.THIN); // Border kiri
            headerStyle.setBorderRight(BorderStyle.THIN); // Border kanan
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.BLACK.getIndex()); // Warna teks
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
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

            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                spreadsheet.autoSizeColumn(i);
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