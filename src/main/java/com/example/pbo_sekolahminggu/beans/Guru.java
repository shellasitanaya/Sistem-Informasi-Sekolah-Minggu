package com.example.pbo_sekolahminggu.beans;

public class Guru {
    private int ID_GURU;
    private String NamaGuru;
    private String NIP;
    private String NoTelp;
    private String Alamat;

    // constructor
    public Guru(){

    }

    // getter setter
    public int getID_GURU() {
        return ID_GURU;
    }

    public void setID_GURU(int ID_GURU) {
        this.ID_GURU = ID_GURU;
    }

    public String getNamaGuru() {
        return NamaGuru;
    }

    public void setNamaGuru(String namaGuru) {
        NamaGuru = namaGuru;
    }

    public String getNIP() {
        return NIP;
    }

    public void setNIP(String NIP) {
        this.NIP = NIP;
    }

    public String getNoTelp() {
        return NoTelp;
    }

    public void setNoTelp(String noTelp) {
        NoTelp = noTelp;
    }

    public String getAlamat() {
        return Alamat;
    }

    public void setAlamat(String alamat) {
        Alamat = alamat;
    }
}