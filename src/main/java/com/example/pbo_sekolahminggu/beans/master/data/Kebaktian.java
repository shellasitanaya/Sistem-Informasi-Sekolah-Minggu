package com.example.pbo_sekolahminggu.beans.master.data;

import java.sql.Date;

public class Kebaktian {
    private int idKebaktian;
    private String jenisKebaktian;
    private Date tanggal;

    // constructor
    public Kebaktian(){

    }

    // getter setter
    public int getIdKebaktian() {
        return idKebaktian;
    }

    public void setIdKebaktian(int idKebaktian) {
        this.idKebaktian = idKebaktian;
    }

    public String getJenisKebaktian() {
        return jenisKebaktian;
    }

    public void setJenisKebaktian(String jenisKebaktian) {
        this.jenisKebaktian = jenisKebaktian;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }
}
