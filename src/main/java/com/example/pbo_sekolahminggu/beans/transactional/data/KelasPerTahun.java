package com.example.pbo_sekolahminggu.beans.transactional.data;

public class KelasPerTahun {
    private int idKelasPerTahun;
    private int idKelas, idTahunAjaran;
    private String ruangKelas;
    private String kelasParalel;

    //non-original-database attributes
    private String namaKelas;
    private String tahunAjaran;


    // construcor
    public KelasPerTahun(){

    }

    // getter setter
    public int getIdKelasPerTahun() {
        return idKelasPerTahun;
    }

    public void setIdKelasPerTahun(int idKelasPerTahun) {
        this.idKelasPerTahun = idKelasPerTahun;
    }

    public String getRuangKelas() {
        return ruangKelas;
    }

    public void setRuangKelas(String ruangKelas) {
        this.ruangKelas = ruangKelas;
    }

    public String getKelasParalel() {
        return kelasParalel;
    }

    public void setKelasParalel(String kelasParalel) {
        this.kelasParalel = kelasParalel;
    }

    public String getNamaKelas() {
        return namaKelas;
    }

    public void setNamaKelas(String namaKelas) {
        this.namaKelas = namaKelas;
    }

    public String getTahunAjaran() {
        return tahunAjaran;
    }

    public void setTahunAjaran(String tahunAjaran) {
        this.tahunAjaran = tahunAjaran;
    }

    public int getIdKelas() {
        return idKelas;
    }

    public void setIdKelas(int idKelas) {
        this.idKelas = idKelas;
    }

    public int getIdTahunAjaran() {
        return idTahunAjaran;
    }

    public void setIdTahunAjaran(int idTahunAjaran) {
        this.idTahunAjaran = idTahunAjaran;
    }
}
