package com.example.pbo_sekolahminggu.beans.transactional_data;

public class HistoriMengajar {
    private int ID_HISTORI_MENGAJAR;
    private int ID_GURU, ID_KELAS_PER_TAHUN;

    //non-original-database attributes
    private String namaGuru, nip, kelas, tahunAjaran;
    private int ID_TAHUN_AJARAN;


    // constructor
    public HistoriMengajar(){

    }

    // getter setter
    public int getID_HISTORI_MENGAJAR() {
        return ID_HISTORI_MENGAJAR;
    }

    public void setID_HISTORI_MENGAJAR(int ID_HISTORI_MENGAJAR) {
        this.ID_HISTORI_MENGAJAR = ID_HISTORI_MENGAJAR;
    }

    public int getID_GURU() {
        return ID_GURU;
    }

    public void setID_GURU(int ID_GURU) {
        this.ID_GURU = ID_GURU;
    }

    public int getID_KELAS_PER_TAHUN() {
        return ID_KELAS_PER_TAHUN;
    }

    public void setID_KELAS_PER_TAHUN(int ID_KELAS_PER_TAHUN) {
        this.ID_KELAS_PER_TAHUN = ID_KELAS_PER_TAHUN;
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

    public int getID_TAHUN_AJARAN() {
        return ID_TAHUN_AJARAN;
    }

    public void setID_TAHUN_AJARAN(int ID_TAHUN_AJARAN) {
        this.ID_TAHUN_AJARAN = ID_TAHUN_AJARAN;
    }
}
