package com.example.pbo_sekolahminggu.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;


import java.io.IOException;

public class MainController {
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
        loadFXML("/com/example/pbo_sekolahminggu/views/home.fxml");
    }

    @FXML
    private void loadMenuGuru() {
        loadFXML("/com/example/pbo_sekolahminggu/views/master.data/guru.fxml");
    }

    @FXML
    private void loadMenuAnak() {
        loadFXML("/com/example/pbo_sekolahminggu/views/master.data/anak.fxml");
    }

    @FXML
    private void loadMenuKelas() {
        loadFXML("/com/example/pbo_sekolahminggu/views/master.data/kelas.fxml");
    }

    @FXML
    private void loadMenuKebaktian() {
        loadFXML("/com/example/pbo_sekolahminggu/views/master.data/kebaktian.fxml");
    }

    @FXML
    private void loadMenuTahunAjaran() {
        loadFXML("/com/example/pbo_sekolahminggu/views/master.data/tahunAjaran.fxml");
    }
    @FXML
    private void loadMenuKelasPerTahun() {
        loadFXML("/com/example/pbo_sekolahminggu/views/transactional.data/kelasPerTahun.fxml");
    }
    @FXML
    private void loadMenuKehadiranAnak() {
        loadFXML("/com/example/pbo_sekolahminggu/views/transactional.data/kehadiranAnak.fxml");
    }
    @FXML
    private void loadMenuKehadiranGuru() {
        loadFXML("/com/example/pbo_sekolahminggu/views/transactional.data/kehadiranGuru.fxml");
    }
    @FXML
    private void loadMenuHistorikelasAnak() {
        loadFXML("/com/example/pbo_sekolahminggu/views/transactional.data/historiKelasAnak.fxml");
    }
    @FXML
    private void loadMenuHistoriMengajar() {
        loadFXML("/com/example/pbo_sekolahminggu/views/transactional.data/historiMengajar.fxml");
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
