package com.example.pbo_sekolahminggu.beans;

public class Anak {
    private int ID_ANAK;
    private String NIS;
    private String NamaAnak;
    private String JenisKelamin;
    private String Alamat;

    // constructor
    public Anak(){

    }

    // getter setter
    public int getID_ANAK() {
        return ID_ANAK;
    }

    public void setID_ANAK(int ID_ANAK) {
        this.ID_ANAK = ID_ANAK;
    }

    public String getNIS() {
        return NIS;
    }

    public void setNIS(String NIS) {
        this.NIS = NIS;
    }

    public String getNamaAnak() {
        return NamaAnak;
    }

    public void setNamaAnak(String namaAnak) {
        NamaAnak = namaAnak;
    }

    public String getJenisKelamin() {
        return JenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        JenisKelamin = jenisKelamin;
    }

    public String getAlamat() {
        return Alamat;
    }

    public void setAlamat(String alamat) {
        Alamat = alamat;
    }
}




