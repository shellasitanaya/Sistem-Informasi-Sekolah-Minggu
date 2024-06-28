package com.example.pbo_sekolahminggu.beans.transactional_data;

public class KehadiranAnak {
    private int ID_KEHADIRAN_ANAK;
    private boolean Presensi = false;  //hadir/tidak default tidak hadir

    // constructor
    public KehadiranAnak(){

    }

    // getter setter

    public int getID_KEHADIRAN_ANAK() {
        return ID_KEHADIRAN_ANAK;
    }

    public void setID_KEHADIRAN_ANAK(int ID_KEHADIRAN_ANAK) {
        this.ID_KEHADIRAN_ANAK = ID_KEHADIRAN_ANAK;
    }

    public boolean isPresensi() {
        return Presensi;
    }

    public void setPresensi(boolean presensi) {
        Presensi = presensi;
    }
}
