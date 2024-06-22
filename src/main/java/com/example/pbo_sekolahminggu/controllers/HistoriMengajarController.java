package com.example.pbo_sekolahminggu.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HistoriMengajarController implements Initializable {
    @FXML
    private Button ediHistoriMengajar;

    @FXML
    private AnchorPane historiMengajarAncPane;

    @FXML
    private TextField historiMengajarSearchField;

    @FXML
    private TableView<?> historiMengajarTbl;

    @FXML
    private ChoiceBox<?> kelasHistoriMengajarCb;

    @FXML
    private Button showHistoriMengajar;

    @FXML
    private ChoiceBox<?> tahunAjaranHistoriMengajarCb;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    @FXML
    private void loadMenuAssignHistoriMengajar() {
        loadFXML("/com/example/pbo_sekolahminggu/assignHistoriMengajar.fxml");
    }

    private void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            historiMengajarAncPane.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately (e.g., show error message)
        }
    }
}