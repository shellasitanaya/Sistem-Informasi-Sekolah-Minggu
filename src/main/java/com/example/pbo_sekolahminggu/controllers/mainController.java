package com.example.pbo_sekolahminggu.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;


import java.io.IOException;

public class mainController {
    @FXML
    private Button anakBtn;

    @FXML
    private AnchorPane dashboardContentAreaInner;

    @FXML
    private AnchorPane dashboardContentAreaOuter;

    @FXML
    private Button dataKebaktianBtn;

    @FXML
    private Button dataKelasPerTahunBtn;

    @FXML
    private Button dataOrangTuaBtn;

    @FXML
    private Button dataTahunAjaranBtn;

    @FXML
    private Button guruBtn;

    @FXML
    private Button historiKelasAnakBtn;

    @FXML
    private Button historiMengajarBtn;

    @FXML
    private Button homeBtn;

    @FXML
    private Button kehadiranAnakBtn;

    @FXML
    private Button kehadiranGuruBtn;

    @FXML
    private Button kelasBtn;

    // This is where your menu content will be displayed

    public void initialize() {
        loadMenuHome();
    }

    @FXML
    private void loadMenuHome() {
        loadFXML("/com/example/pbo_sekolahminggu/home.fxml");
    }

    @FXML
    private void loadMenuGuru() {
        loadFXML("/com/example/pbo_sekolahminggu/guru.fxml");
    }

    @FXML
    private void loadMenuAnak() {
        loadFXML("/com/example/pbo_sekolahminggu/anak.fxml");
    }

    @FXML
    private void loadMenuKelas() {
        loadFXML("/com/example/pbo_sekolahminggu/kelas.fxml");
    }

    @FXML
    private void loadMenuOrangTua() {
        loadFXML("/com/example/pbo_sekolahminggu/orangTua.fxml");
    }
    @FXML
    private void loadMenuKebaktian() {
        loadFXML("/com/example/pbo_sekolahminggu/kebaktian.fxml");
    }

    @FXML
    private void loadMenuTahunAjaran() {
        loadFXML("/com/example/pbo_sekolahminggu/tahunAjaran.fxml");
    }

    @FXML
    private void loadMenuKelasPerTahun() {
        loadFXML("/com/example/pbo_sekolahminggu/kelasPerTahun.fxml");
    }

    private void loadFXML(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            dashboardContentAreaInner.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately (e.g., show error message)
        }
    }

}
