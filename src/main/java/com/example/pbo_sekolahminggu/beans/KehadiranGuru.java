package com.example.pbo_sekolahminggu.beans;

public class KehadiranGuru {
    private int ID_KEHADIRAN_GURU;
    private boolean Presensi = false;  //hadir/tidak default tidak hadir

    // constructor
    public KehadiranGuru(){

    }

    // getter setter


    public int getID_KEHADIRAN_GURU() {
        return ID_KEHADIRAN_GURU;
    }

    public void setID_KEHADIRAN_GURU(int ID_KEHADIRAN_GURU) {
        this.ID_KEHADIRAN_GURU = ID_KEHADIRAN_GURU;
    }

    public boolean isPresensi() {
        return Presensi;
    }

    public void setPresensi(boolean presensi) {
        Presensi = presensi;
    }
}
