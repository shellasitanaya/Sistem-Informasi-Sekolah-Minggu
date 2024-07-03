package com.example.pbo_sekolahminggu.beans.master.data;

public class Anak {
    private int idAnak;
    private String nis;
    private String nama;
    private String jenisKelamin;
    private String namaOrangTua;
    private String alamatOrangTua;
    private String noTelpOrangTua;

    // constructor
    public Anak(){

    }

    // getter setter
    public int getIdAnak() {
        return idAnak;
    }

    public void setIdAnak(int idAnak) {
        this.idAnak = idAnak;
    }

    public String getNis() {
        return nis;
    }

    public void setNis(String nis) {
        this.nis = nis;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
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




