package com.example.pbo_sekolahminggu.beans;

import javafx.scene.control.DatePicker;

import java.sql.Date;

public class Kebaktian {
    private int ID_KEBAKTIAN;
    private String jenisKebaktian;
    private Date Tanggal;

    // constructor
    public Kebaktian(){

    }

    // getter setter
    public int getID_KEBAKTIAN() {
        return ID_KEBAKTIAN;
    }

    public void setID_KEBAKTIAN(int ID_KEBAKTIAN) {
        this.ID_KEBAKTIAN = ID_KEBAKTIAN;
    }

    public String getJenisKebaktian() {
        return jenisKebaktian;
    }

    public void setJenisKebaktian(String jenisKebaktian) {
        this.jenisKebaktian = jenisKebaktian;
    }

    public Date getTanggal() {
        return Tanggal;
    }

    public void setTanggal(Date tanggal) {
        Tanggal = tanggal;
    }
}