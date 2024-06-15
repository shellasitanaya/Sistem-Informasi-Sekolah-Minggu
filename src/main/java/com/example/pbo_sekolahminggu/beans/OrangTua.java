package com.example.pbo_sekolahminggu.beans;

public class OrangTua {
    private int ID_ORANG_TUA;
    private String NamaOrangTua;
    private String AlamatOrangTua;
    private String NoTelpOrangTua;

    // constructor
    public OrangTua (){

    }

    // getter setter
    public int getID_ORANG_TUA() {
        return ID_ORANG_TUA;
    }

    public void setID_ORANG_TUA(int ID_ORANG_TUA) {
        this.ID_ORANG_TUA = ID_ORANG_TUA;
    }

    public String getNamaOrangTua() {
        return NamaOrangTua;
    }

    public void setNamaOrangTua(String namaOrangTua) {
        NamaOrangTua = namaOrangTua;
    }

    public String getAlamatOrangTua() {
        return AlamatOrangTua;
    }

    public void setAlamatOrangTua(String alamatOrangTua) {
        AlamatOrangTua = alamatOrangTua;
    }

    public String getNoTelpOrangTua() {
        return NoTelpOrangTua;
    }

    public void setNoTelpOrangTua(String noTelpOrangTua) {
        NoTelpOrangTua = noTelpOrangTua;
    }
}
