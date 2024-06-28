package com.example.pbo_sekolahminggu.beans;

public class HistoriKelasAnak {
    private int ID_HISTORI_KELAS_ANAK;
    //foreign keys
    private int id_anak;
    private int id_kelas_per_tahun;

    //diluar tabel
    private String nama_anak;
    private String nis;
    private String kelas;
    private String tahun_ajaran;
    // constructor
    public HistoriKelasAnak(){

    }

    // getter setter

    public int getId_anak() {
        return id_anak;
    }

    public void setId_anak(int id_anak) {
        this.id_anak = id_anak;
    }

    public int getId_kelas_per_tahun() {
        return id_kelas_per_tahun;
    }

    public void setId_kelas_per_tahun(int id_kelas_per_tahun) {
        this.id_kelas_per_tahun = id_kelas_per_tahun;
    }

    public String getNama_anak() {
        return nama_anak;
    }

    public void setNama_anak(String nama_anak) {
        this.nama_anak = nama_anak;
    }

    public String getNis() {
        return nis;
    }

    public void setNis(String nis) {
        this.nis = nis;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getTahun_ajaran() {
        return tahun_ajaran;
    }

    public void setTahun_ajaran(String tahun_ajaran) {
        this.tahun_ajaran = tahun_ajaran;
    }

    public int getID_HISTORI_KELAS_ANAK() {
        return ID_HISTORI_KELAS_ANAK;
    }

    public void setID_HISTORI_KELAS_ANAK(int ID_HISTORI_KELAS_ANAK) {
        this.ID_HISTORI_KELAS_ANAK = ID_HISTORI_KELAS_ANAK;
    }
}
