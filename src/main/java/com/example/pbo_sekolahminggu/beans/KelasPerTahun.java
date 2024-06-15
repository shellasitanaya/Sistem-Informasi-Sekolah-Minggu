package com.example.pbo_sekolahminggu.beans;

public class KelasPerTahun {
    private int ID_KELAS_PER_TAHUN;
    private String RuangKelas;
    private String KelasParalel;

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
}
