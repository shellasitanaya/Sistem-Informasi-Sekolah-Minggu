package com.example.pbo_sekolahminggu.beans.master.data;

public class TahunAjaran {
    private int ID_TAHUN_AJARAN;
    private String TahunAjaran;

    // constructor
    public TahunAjaran(){

    }

    // getter setter

    public int getID_TAHUN_AJARAN() {
        return ID_TAHUN_AJARAN;
    }

    public void setID_TAHUN_AJARAN(int ID_TAHUN_AJARAN) {
        this.ID_TAHUN_AJARAN = ID_TAHUN_AJARAN;
    }

    public String getTahunAjaran() {
        return TahunAjaran;
    }

    public void setTahunAjaran(String tahunAjaran) {
        TahunAjaran = tahunAjaran;
    }
}
