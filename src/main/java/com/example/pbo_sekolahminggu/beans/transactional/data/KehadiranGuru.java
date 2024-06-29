package com.example.pbo_sekolahminggu.beans.transactional.data;
import java.sql.Date;

public class KehadiranGuru {
    private int ID_KEHADIRAN_GURU;
    private int ID_HISTORI_MENGAJAR, ID_KEBAKTIAN;
    private boolean Presensi = false;  //hadir/tidak default tidak hadir

    //non-original-database attributes
    private String nama, nip, jenisKebaktian, kelas;
    private Date tanggal;
    private int ID_KELAS_PER_TAHUN;
    private int ID_TAHUN_AJARAN;
    private String tahun_ajaran;
    private String kebaktian;
    private Date tgl_kebaktian;



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

    public int getID_KELAS_PER_TAHUN() {
        return ID_KELAS_PER_TAHUN;
    }

    public void setID_KELAS_PER_TAHUN(int ID_KELAS_PER_TAHUN) {
        this.ID_KELAS_PER_TAHUN = ID_KELAS_PER_TAHUN;
    }


    public int getID_TAHUN_AJARAN() {
        return ID_TAHUN_AJARAN;
    }

    public void setID_TAHUN_AJARAN(int ID_TAHUN_AJARAN) {
        this.ID_TAHUN_AJARAN = ID_TAHUN_AJARAN;
    }

    public String getTahun_ajaran() {
        return tahun_ajaran;
    }

    public void setTahun_ajaran(String tahun_ajaran) {
        this.tahun_ajaran = tahun_ajaran;
    }

    public String getKebaktian() {
        return kebaktian;
    }

    public void setKebaktian(String kebaktian) {
        this.kebaktian = kebaktian;
    }

    public Date getTgl_kebaktian() {
        return tgl_kebaktian;
    }

    public void setTgl_kebaktian(Date tgl_kebaktian) {
        this.tgl_kebaktian = tgl_kebaktian;
    }
}