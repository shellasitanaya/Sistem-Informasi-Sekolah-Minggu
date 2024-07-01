package com.example.pbo_sekolahminggu.beans.transactional.data;

import java.sql.Date;

public class KehadiranAnak {
    private int idKehadiranAnak;
    private boolean presensi = false;  //hadir/tidak default tidak hadir
    //foreign keys
    private int idHistoriKelasAnak;
    private int idKebaktian;

    //non-database-attributes
    private String namaAnak;
    private String nis;
    private String kelas;
    private String tahunAjaran;
    private String kebaktian;
    private Date tglKebaktian;
    private int maxKehadiran;

    // constructor
    public KehadiranAnak(){

    }

    // getter setter
    public int getIdHistoriKelasAnak() {
        return idHistoriKelasAnak;
    }

    public void setIdHistoriKelasAnak(int idHistoriKelasAnak) {
        this.idHistoriKelasAnak = idHistoriKelasAnak;
    }

    public int getIdKebaktian() {
        return idKebaktian;
    }

    public void setIdKebaktian(int idKebaktian) {
        this.idKebaktian = idKebaktian;
    }

    public String getNamaAnak() {
        return namaAnak;
    }

    public void setNamaAnak(String namaAnak) {
        this.namaAnak = namaAnak;
    }

    public String getNis() {
        return nis;
    }

    public void setNis(String nis) {
        this.nis = nis;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
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

    public int getIdKehadiranAnak() {
        return idKehadiranAnak;
    }

    public void setIdKehadiranAnak(int idKehadiranAnak) {
        this.idKehadiranAnak = idKehadiranAnak;
    }

    public boolean isPresensi() {
        return presensi;
    }

    public void setPresensi(boolean presensi) {
        this.presensi = presensi;
    }

    public int getMaxKehadiran() {
        return maxKehadiran;
    }

    public void setMaxKehadiran(int maxKehadiran) {
        this.maxKehadiran = maxKehadiran;
    }

}
