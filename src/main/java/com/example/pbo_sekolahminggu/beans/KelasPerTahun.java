package com.example.pbo_sekolahminggu.beans;

public class KelasPerTahun {
    private int ID_KELAS_PER_TAHUN;
    private int ID_KELAS, ID_TAHUN_AJARAN;
    private String RuangKelas;
    private String KelasParalel;

    //non-original-database attributes
    private String namaKelas;
    private String tahunAjaran;


    // construcor
    public KelasPerTahun(){

    }

    // getter setter
    public int getID_KELAS_PER_TAHUN() {
        return ID_KELAS_PER_TAHUN;
    }

    public void setID_KELAS_PER_TAHUN(int ID_KELAS_PER_TAHUN) {
        this.ID_KELAS_PER_TAHUN = ID_KELAS_PER_TAHUN;
    }

    public String getRuangKelas() {
        return RuangKelas;
    }

    public void setRuangKelas(String ruangKelas) {
        RuangKelas = ruangKelas;
    }

    public String getKelasParalel() {
        return KelasParalel;
    }

    public void setKelasParalel(String kelasParalel) {
        KelasParalel = kelasParalel;
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

    public int getID_KELAS() {
        return ID_KELAS;
    }

    public void setID_KELAS(int ID_KELAS) {
        this.ID_KELAS = ID_KELAS;
    }

    public int getID_TAHUN_AJARAN() {
        return ID_TAHUN_AJARAN;
    }

    public void setID_TAHUN_AJARAN(int ID_TAHUN_AJARAN) {
        this.ID_TAHUN_AJARAN = ID_TAHUN_AJARAN;
    }
}
