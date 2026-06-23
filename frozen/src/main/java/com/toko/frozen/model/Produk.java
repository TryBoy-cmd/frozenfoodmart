package com.toko.frozen.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "produk")
public class Produk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nama;
    private Integer harga;
    private Integer stok;
    private Integer jumlahTerjual;

    public Produk() {
        this.jumlahTerjual = 0;
    }

    // Getter dan Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public Integer getHarga() { return harga; }
    public void setHarga(Integer harga) { this.harga = harga; }
    public Integer getStok() { return stok; }
    public void setStok(Integer stok) { this.stok = stok; }
    public Integer getJumlahTerjual() { return jumlahTerjual; }
    public void setJumlahTerjual(Integer jumlahTerjual) { this.jumlahTerjual = jumlahTerjual; }
}