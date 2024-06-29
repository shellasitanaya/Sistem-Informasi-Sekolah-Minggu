package com.example.pbo_sekolahminggu.beans;

import java.sql.Date;

public class KehadiranAnak {
    private int ID_KEHADIRAN_ANAK;
    private boolean Presensi = false;  //hadir/tidak default tidak hadir
    //foreign keys
    private int id_histori_kelas_anak;
    private int id_kebaktian;

    //non-database-attributes
    private String nama_anak;
    private String NIS;
    private String kelas;
    private String tahun_ajaran;
    private String kebaktian;
    private Date tgl_kebaktian;
    private int maxKehadiran;

    // constructor
    public KehadiranAnak(){

    }

    // getter setter
    public int getId_histori_kelas_anak() {
        return id_histori_kelas_anak;
    }

    public void setId_histori_kelas_anak(int id_histori_kelas_anak) {
        this.id_histori_kelas_anak = id_histori_kelas_anak;
    }

    public int getId_kebaktian() {
        return id_kebaktian;
    }

    public void setId_kebaktian(int id_kebaktian) {
        this.id_kebaktian = id_kebaktian;
    }

    public String getNama_anak() {
        return nama_anak;
    }

    public void setNama_anak(String nama_anak) {
        this.nama_anak = nama_anak;
    }

    public String getNIS() {
        return NIS;
    }

    public void setNIS(String NIS) {
        this.NIS = NIS;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
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

    public int getMaxKehadiran() {
        return maxKehadiran;
    }

    public void setMaxKehadiran(int maxKehadiran) {
        this.maxKehadiran = maxKehadiran;
    }

}