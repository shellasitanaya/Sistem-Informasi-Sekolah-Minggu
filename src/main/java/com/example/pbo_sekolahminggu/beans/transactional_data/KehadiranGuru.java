package com.example.pbo_sekolahminggu.beans.transactional_data;
import java.sql.Date;

public class KehadiranGuru {
    private int ID_KEHADIRAN_GURU;
    private int ID_HISTORI_MENGAJAR, ID_KEBAKTIAN;
    private boolean Presensi = false;  //hadir/tidak default tidak hadir

    //non-original-database attributes
    private String nama, nip, jenisKebaktian, kelas;
    private Date tanggal;


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

    public int getID_HISTORI_MENGAJAR() {
        return ID_HISTORI_MENGAJAR;
    }

    public void setID_HISTORI_MENGAJAR(int ID_HISTORI_MENGAJAR) {
        this.ID_HISTORI_MENGAJAR = ID_HISTORI_MENGAJAR;
    }

    public int getID_KEBAKTIAN() {
        return ID_KEBAKTIAN;
    }

    public void setID_KEBAKTIAN(int ID_KEBAKTIAN) {
        this.ID_KEBAKTIAN = ID_KEBAKTIAN;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getJenisKebaktian() {
        return jenisKebaktian;
    }

    public void setJenisKebaktian(String jenisKebaktian) {
        this.jenisKebaktian = jenisKebaktian;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }
}
