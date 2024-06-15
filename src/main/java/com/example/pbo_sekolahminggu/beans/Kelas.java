package com.example.pbo_sekolahminggu.beans;

public class Kelas {
    private int ID_KELAS;
    private String NamaKelas;

    // constructor
    public Kelas(){

    }

    // getter setter
    public int getID_KELAS() {
        return ID_KELAS;
    }

    public void setID_KELAS(int ID_KELAS) {
        this.ID_KELAS = ID_KELAS;
    }

    public String getNamaKelas() {
        return NamaKelas;
    }

    public void setNamaKelas(String namaKelas) {
        NamaKelas = namaKelas;
    }
}
