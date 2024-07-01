package com.example.pbo_sekolahminggu.beans.transactional.data;

public class HistoriKelasAnak {
    private int idHistoriKelasAnak;
    //foreign keys
    private int idAnak;
    private int idKelasPerTahun;

    //diluar tabel
    private String namaAnak;
    private String nis;
    private String kelas;
    private String tahunAjaran;
    // constructor
    public HistoriKelasAnak(){

    }

    // getter setter

    public int getIdAnak() {
        return idAnak;
    }

    public void setIdAnak(int idAnak) {
        this.idAnak = idAnak;
    }

    public int getIdKelasPerTahun() {
        return idKelasPerTahun;
    }

    public void setIdKelasPerTahun(int idKelasPerTahun) {
        this.idKelasPerTahun = idKelasPerTahun;
    }

    public String getNamaAnak() {
        return namaAnak;
    }

    public void setNamaAnak(String namaAnak) {
        this.namaAnak = namaAnak;
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

    public String getTahunAjaran() {
        return tahunAjaran;
    }

    public void setTahunAjaran(String tahunAjaran) {
        this.tahunAjaran = tahunAjaran;
    }

    public int getIdHistoriKelasAnak() {
        return idHistoriKelasAnak;
    }

    public void setIdHistoriKelasAnak(int idHistoriKelasAnak) {
        this.idHistoriKelasAnak = idHistoriKelasAnak;
    }
}
