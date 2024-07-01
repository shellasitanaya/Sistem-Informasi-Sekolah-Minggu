package com.example.pbo_sekolahminggu.beans.transactional.data;

public class HistoriMengajar {
    private int idHistoriMengajar;
    private int idGuru, idKelasPerTahun;

    //non-original-database attributes
    private String namaGuru, nip, kelas, tahunAjaran;
    private int idTahunAjaran;


    // constructor
    public HistoriMengajar(){

    }

    // getter setter
    public int getIdHistoriMengajar() {
        return idHistoriMengajar;
    }

    public void setIdHistoriMengajar(int idHistoriMengajar) {
        this.idHistoriMengajar = idHistoriMengajar;
    }

    public int getIdGuru() {
        return idGuru;
    }

    public void setIdGuru(int idGuru) {
        this.idGuru = idGuru;
    }

    public int getIdKelasPerTahun() {
        return idKelasPerTahun;
    }

    public void setIdKelasPerTahun(int idKelasPerTahun) {
        this.idKelasPerTahun = idKelasPerTahun;
    }

    public String getNamaGuru() {
        return namaGuru;
    }

    public void setNamaGuru(String namaGuru) {
        this.namaGuru = namaGuru;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getTahunAjaran() {
        return tahunAjaran;
    }

    public void setTahunAjaran(String tahunAjaran) {
        this.tahunAjaran = tahunAjaran;
    }

    public int getIdTahunAjaran() {
        return idTahunAjaran;
    }

    public void setIdTahunAjaran(int idTahunAjaran) {
        this.idTahunAjaran = idTahunAjaran;
    }
}