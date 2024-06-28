package com.example.pbo_sekolahminggu.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HistoriKelasAnakController implements Initializable {
    @FXML
    private Button ediHistoriKelas;

    @FXML
    private AnchorPane historiKelasAncPane;

    @FXML
    private TextField historiKelasSearchField;

    @FXML
    private TableView<?> historiKelasTbl;

    @FXML
    private ComboBox<?> kelasHistoriKelasCb;

    @FXML
    private Button showHistoriKelas;

    @FXML
    private ComboBox<?> tahunAjaranHistoriKelasCb;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void loadMenuAssignHistoriKelasAnak() {
        loadFXML("/com/example/pbo_sekolahminggu/assignHistoriKelasAnak.fxml");
    }

    private void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            historiKelasAncPane.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately (e.g., show error message)
        }
    }
}