package com.example.pbo_sekolahminggu.beans.master.data;

public class Anak {
    private int ID_ANAK;
    private String NIS;
    private String Nama;
    private String JenisKelamin;
    private String namaOrangTua;
    private String alamatOrangTua;
    private String noTelpOrangTua;

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

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public String getJenisKelamin() {
        return JenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        JenisKelamin = jenisKelamin;
    }

    public String getNamaOrangTua() {
        return namaOrangTua;
    }

    public void setNamaOrangTua(String namaOrangTua) {
        this.namaOrangTua = namaOrangTua;
    }

    public String getAlamatOrangTua() {
        return alamatOrangTua;
    }

    public void setAlamatOrangTua(String alamatOrangTua) {
        this.alamatOrangTua = alamatOrangTua;
    }

    public String getNoTelpOrangTua() {
        return noTelpOrangTua;
    }

    public void setNoTelpOrangTua(String noTelpOrangTua) {
        this.noTelpOrangTua = noTelpOrangTua;
    }
}




