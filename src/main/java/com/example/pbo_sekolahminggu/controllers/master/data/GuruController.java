package com.example.pbo_sekolahminggu.controllers.master.data;

import com.example.pbo_sekolahminggu.beans.master.data.Guru;
import com.example.pbo_sekolahminggu.dao.master.data.GuruDao;
import com.example.pbo_sekolahminggu.utils.ConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class GuruController implements Initializable {
    // text field
    @FXML
    private TextField namaGuruField;
    @FXML
    private TextField nipGuruField;
    @FXML
    private TextField noTelpGuruField;
    @FXML
    private TextField alamatGuruField;
    @FXML
    private TextField guruSearchField;

    // table
    @FXML
    private TableView<Guru> guruTbl;
    private ObservableList<Guru> data;
    private Guru selectedGuru = null;

    // button
    @FXML
    private Button clearGuruBtn;
    @FXML
    private Button updateGuruBtn;
    @FXML
    private Button deleteGuruBtn;
    @FXML
    private Button createGuruBtn;


    ObservableList<Guru> listGuru = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // supaya data dan UI sinkron (auto update)
        listGuru = FXCollections.observableArrayList();
        // untuk pilih row
        guruTbl.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // kosongkan kolom dari tabel
        guruTbl.getColumns().clear();

        // inisialisasi kolom
        // Membuat kolom untuk ID_GURU
        TableColumn<Guru, Integer> idCol = new TableColumn<>("ID Guru");
        idCol.setMinWidth(100);
        idCol.setCellValueFactory(new PropertyValueFactory<>("ID_GURU"));

        // Membuat kolom untuk NamaGuru
        TableColumn<Guru, String> namaCol = new TableColumn<>("Nama Guru");
        namaCol.setMinWidth(150);
        namaCol.setCellValueFactory(new PropertyValueFactory<>("NamaGuru"));

        // Membuat kolom untuk NIP
        TableColumn<Guru, String> nipCol = new TableColumn<>("NIP");
        nipCol.setMinWidth(150);
        nipCol.setCellValueFactory(new PropertyValueFactory<>("NIP"));

        // Membuat kolom untuk NoTelp
        TableColumn<Guru, String> noTelpCol = new TableColumn<>("Nomor Telpon");
        noTelpCol.setMinWidth(150);
        noTelpCol.setCellValueFactory(new PropertyValueFactory<>("NoTelp"));

        // Membuat kolom untuk Alamat
        TableColumn<Guru, String> alamatCol = new TableColumn<>("Alamat");
        alamatCol.setMinWidth(150);
        alamatCol.setCellValueFactory(new PropertyValueFactory<>("Alamat"));

        // Menambahkan kolom ke TableView
        guruTbl.getColumns().addAll(idCol, namaCol, nipCol, noTelpCol, alamatCol);

        refreshData();

        //Inisialisasi data
        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            listGuru = FXCollections.
                    observableList(GuruDao.getAll(con));
            for (Guru guru: listGuru) {
                System.out.println(guru);
            }
            guruTbl.setItems(listGuru);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(con);
        }


        // nampilin selected items
        guruTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedGuru = newSelection;
                namaGuruField.setText(selectedGuru.getNamaGuru());
                nipGuruField.setText(selectedGuru.getNip());
                noTelpGuruField.setText(selectedGuru.getNoTelp());
                alamatGuruField.setText(selectedGuru.getAlamat());
            }
        });

    }

    // mengambil ulang data dari database
    private void refreshData() {
        Connection connection = null;
        try {
            connection = ConnectionManager.getConnection();
            data = FXCollections.observableArrayList(GuruDao.getAll(connection));
            listGuru.setAll(data);
            guruTbl.setItems(listGuru);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(connection);
        }
    }

    // kasih alert
    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // CRUD ----------------------------------------------------
    // CREATE
    @FXML
    public void create() {
        String nama= namaGuruField.getText();
        String nip = nipGuruField.getText();
        String noTelp = noTelpGuruField.getText();
        String alamat = alamatGuruField.getText();

        // cek input
        if (nama.isEmpty() || nip.isEmpty() || noTelp.isEmpty()|| alamat.isEmpty()) {
            showErrorMessage("Harap isi semua kolom.");
            return;
        }

        // buat objek dengan inputan datanya
        Guru guru = new Guru();
        guru.setNamaGuru(nama);
        guru.setNip(nip);
        guru.setNoTelp(noTelp);
        guru.setAlamat(alamat);

        // connect ke database
        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            GuruDao.create(con, guru); // create

            /*
            mendapatkan dan memperbarui semua data guru dari database
            buat atau inisialisasi ObservableList baru dengan data dari database
             */
            listGuru = FXCollections.observableArrayList(GuruDao.getAll(con));
            // tampilin update-an data di tableview
            guruTbl.setItems(listGuru);

            /*
            opsi lain observable list:
            mempertahankan referensi ObservableList yang sudah ada tetapi ingin
            mengganti isi dari ObservableList tersebut dengan data baru dari database
            */
            // -- listGuru.setAll(GuruDao.getAll(con));

            // menampilkan pesan sukses
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Guru berhasil ditambahkan!");
            alert.show();

            clear();
        } catch (SQLException e) {
            // menampilkan pesan error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat menambahkan data guru: " + e.getMessage());
            alert.show();
        } finally {
            ConnectionManager.close(con);
        }
    }

    // UPDATE
    @FXML
    public void update() {
        Guru selected = guruTbl.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showErrorMessage("Pilih kolom yang ingin diupdate.");
            return;
        }

        String nama = namaGuruField.getText();
        String nip = nipGuruField.getText();
        String noTelp = noTelpGuruField.getText();
        String alamat = alamatGuruField.getText();

        // cek input
        if (nama.isEmpty() || nip.isEmpty() || noTelp.isEmpty() || alamat.isEmpty()) {
            showErrorMessage("Harap isi semua kolom.");
            return;
        }

        // update selected Guru pake value baru
        selected.setNamaGuru(nama);
        selected.setNip(nip);
        selected.setNoTelp(noTelp);
        selected.setAlamat(alamat);

        // connect(update) ke database
        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            GuruDao.update(con, selected); // update

            // update observable list
            updateGuruInList(selected);

            guruTbl.refresh();
            // Menampilkan pesan sukses
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Data Guru berhasil diupdate!");
            alert.show();

            clear();

        } catch (SQLException e) {
            // Menampilkan pesan error jika terjadi kesalahan
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Terjadi kesalahan saat mengupdate data guru: " + e.getMessage());
            alert.show();
        } finally {
            ConnectionManager.close(con);
        }
    }


    private void updateGuruInList(Guru updatedGuru) {
        int index = listGuru.indexOf(selectedGuru); //cari index selected di listGuru
        if (index != -1) { //cek kalo selected ada di list
            listGuru.set(index, updatedGuru); // update list
        }
    }

    // DELETE
    @FXML
    public void delete() {
        if (guruTbl.getSelectionModel().getSelectedItems().size() != 0) {
            Guru selected = guruTbl.getSelectionModel().getSelectedItem();
            Connection connection = null;
            try {
                connection = ConnectionManager.getConnection();
                GuruDao.delete(connection, selected);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Data berhasil dihapus !");
                alert.show();

                refreshData();
                clear();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                ConnectionManager.close(connection);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Tidak ada data yang dipilih !");
            alert.show();
        }
    }

    // CLEAR -> text fieldnya saja saja)
    @FXML
    private void clear() {
        noTelpGuruField.clear();
        alamatGuruField.clear();
        nipGuruField.clear();
        namaGuruField.clear();
        selectedGuru = null;

    }
    public void Search() {

        FilteredList<Guru> filter = new FilteredList<>(listGuru, e -> true);

        guruSearchField.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate(predicateGuruData -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (predicateGuruData.getNamaGuru().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateGuruData.getNip().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateGuruData.getNoTelp().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateGuruData.getAlamat().toLowerCase().contains(searchKey)) {
                    return true;
                } else {
                    return false;
                }
            });
        });

        SortedList<Guru> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(guruTbl.comparatorProperty());
        guruTbl.setItems(sortList);
    }

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
//        File file = chooser.showSaveDialog(guruTbl.getScene().getWindow());
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
//
//    private void exportToPdf(File file) {
//        System.out.println(file.getAbsolutePath());
//        PdfDocument pdfDoc = null;
//        try {
//            pdfDoc = new PdfDocument(new PdfWriter(file.getAbsolutePath()));
//            Document doc = new Document(pdfDoc);
//
//            //  judul
//            Paragraph title = new Paragraph("Laporan BLABLA");
//            title.setTextAlignment(TextAlignment.CENTER);
//            title.setBold();
//            doc.add(title);
//
//            Table table = new Table(UnitValue.createPercentArray(new float[] {10, 30, 60})).useAllAvailableWidth();
//            //Logo header
//
//            Image logo = new Image(ImageDataFactory.create("src/main/resources/com/example/pbo_sekolahminggu/images/exportIcon.png"));
//            logo.setWidth(UnitValue.createPercentValue(50));
//            Cell logoCell = new Cell(1, 2).add(logo);
//            logoCell.setBorder(Border.NO_BORDER);
//            table.addCell(logoCell);
//
//
//            Cell emptyCell = new Cell(1, 1);
//            emptyCell.setBorder(Border.NO_BORDER);
//            table.addCell(emptyCell);
//
//            //Header Table
//            for (int i = 0; i< guruTbl.getColumns().size(); i++) {
//                TableColumn col = (TableColumn) guruTbl.getColumns().get(i);
//                Cell headerCell = new Cell();
//                title = new Paragraph(col.getText());
//                title.setTextAlignment(TextAlignment.CENTER);
//                title.setBold();
//
//                headerCell.add(title);
//                table.addCell(headerCell);
//            }
//            table.addCell(emptyCell);
//
//            //Table Data
//            for (int i = 0; i< guruTbl.getItems().size(); i++) {
//                Guru data = guruTbl.getItems().get(i);
//
//                //Data id
//                Paragraph idParagraph = new Paragraph(String.valueOf(data.getID_GURU()));
//                idParagraph.setTextAlignment(TextAlignment.CENTER);
//                Cell idCell = new Cell().add(idParagraph);
//                table.addCell(idCell);
//
//                //Data
//                Paragraph brandParagraph = new Paragraph(data.getNamaGuru());
//                Cell brandCell = new Cell().add(brandParagraph);
//                table.addCell(brandCell);
//
//                //3rd empty cell
//                table.addCell(emptyCell);
//            }
//
//            doc.add(table);
//            doc.close();
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//
//        } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void exportToExcel(File file) {
//        XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet spreadsheet = workbook.createSheet("Guru Data");
//
//        FileOutputStream out = null;
//        Connection con = null;
//
//        try {
//            con = ConnectionManager.getConnection();
//            int rowid = 0;
//
//            // judul
//            XSSFRow titleRow = spreadsheet.createRow(rowid++);
//            XSSFCell titleCell = titleRow.createCell(0);
//            titleCell.setCellValue("Laporan BLABLA");
//
//            //Export Header
//            XSSFRow headerRow = spreadsheet.createRow(rowid++);
//            Object[] headerArr = guruTbl.getColumns().toArray();
//
//            int cellCounter = 0;
//            for (Object obj : headerArr) {
//                XSSFCell cell = headerRow.createCell(cellCounter++);
//                cell.setCellValue(((TableColumn) obj).getText());
//            }
//
//            //Export Data
//            Map<String, Object[]> data = GuruDao.getAllArrayObject(con);
//            Set<String> keyid = data.keySet();
//
//            for (String key : keyid) {
//                XSSFRow row = spreadsheet.createRow(rowid++);
//                Object[] objectArr = data.get(key);
//                int cellid = 0;
//
//                for (Object obj : objectArr) {
//                    XSSFCell cell = row.createCell(cellid++);
//                    cell.setCellValue(String.valueOf(obj));
//                }
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

}