package com.example.pbo_sekolahminggu.beans.transactional.data;
import java.sql.Date;

public class KehadiranGuru {
    private int idKehadiranGuru;
    private int idHistoriMengajar, idKebaktian;
    private boolean presensi = false;  //hadir/tidak default tidak hadir

    //non-original-database attributes
    private String nama, nip, jenisKebaktian, kelas;
    private Date tanggal;
    private int idKelasPerTahun;
    private int idTahunAjaran;
    private String tahunAjaran;
    private String kebaktian;
    private Date tglKebaktian;



    // constructor
    public KehadiranGuru(){

    }

    // getter setter


    public int getIdKehadiranGuru() {
        return idKehadiranGuru;
    }

    public void setIdKehadiranGuru(int idKehadiranGuru) {
        this.idKehadiranGuru = idKehadiranGuru;
    }

    public boolean isPresensi() {
        return presensi;
    }

    public void setPresensi(boolean presensi) {
        this.presensi = presensi;
    }

    public int getIdHistoriMengajar() {
        return idHistoriMengajar;
    }

    public void setIdHistoriMengajar(int idHistoriMengajar) {
        this.idHistoriMengajar = idHistoriMengajar;
    }

    public int getIdKebaktian() {
        return idKebaktian;
    }

    public void setIdKebaktian(int idKebaktian) {
        this.idKebaktian = idKebaktian;
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

    public int getIdKelasPerTahun() {
        return idKelasPerTahun;
    }

    public void setIdKelasPerTahun(int idKelasPerTahun) {
        this.idKelasPerTahun = idKelasPerTahun;
    }


    public int getIdTahunAjaran() {
        return idTahunAjaran;
    }

    public void setIdTahunAjaran(int idTahunAjaran) {
        this.idTahunAjaran = idTahunAjaran;
    }

    public String getTahunAjaran() {
        return tahunAjaran;
    }

    public void setTahunAjaran(String tahunAjaran) {
        this.tahunAjaran = tahunAjaran;
    }

    public String getKebaktian() {
        return kebaktian;
    }

    public void setKebaktian(String kebaktian) {
        this.kebaktian = kebaktian;
    }

    public Date getTglKebaktian() {
        return tglKebaktian;
    }

    public void setTglKebaktian(Date tglKebaktian) {
        this.tglKebaktian = tglKebaktian;
    }
}