package com.si20b.crud;

public class data_mahasiswa {

    //Deklarasi Variable
    private String nim;
    private String nama;
    private String orma;
    private String jabatan;
    private String gender;
    private String tglaktif;
    private String gambar;
    private String key;

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getOrma() {
        return orma;
    }

    public void setOrma(String orma) {
        this.orma = orma;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTglaktif() {
        return tglaktif;
    }

    public void setTglaktif(String tglaktif) {
        this.tglaktif = tglaktif;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public data_mahasiswa() {
    }

    public data_mahasiswa(String nim, String nama, String orma, String jabatan, String gender,
                          String tglaktif, String gambar) {
        this.nim = nim;
        this.nama = nama;
        this.orma = orma;
        this.jabatan = jabatan;
        this.gender = gender;
        this.tglaktif = tglaktif;
        this.gambar = gambar;
    }
}
