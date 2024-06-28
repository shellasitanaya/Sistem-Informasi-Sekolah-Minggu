package com.example.pbo_sekolahminggu.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class assignHistoriKelasAnakController implements Initializable {
    @FXML
    private Button addHistoriKelasBtn;

    @FXML
    private TableView<?> anakBerdasarkanTahunHistoriKelasTbl;

    @FXML
    private TableView<?> anakDalamKelasHistoriKelasTbl;

    @FXML
    private AnchorPane assignHistoriKelasAncPane;

    @FXML
    private Button cancelHistoriKelasBtn;

    @FXML
    private TableColumn<?, ?> idOrangTua;

    @FXML
    private TableColumn<?, ?> idOrangTua2;

    @FXML
    private TableColumn<?, ?> idOrangTua21;

    @FXML
    private Button removeHistoriKelasBtn;

    @FXML
    private Button saveHistoriKelasBtn;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void loadMenuHistoriKelasAnak() {
        loadFXML("/com/example/pbo_sekolahminggu/historiKelasAnak.fxml");
    }

    private void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            assignHistoriKelasAncPane.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately (e.g., show error message)
        }
    }
}
