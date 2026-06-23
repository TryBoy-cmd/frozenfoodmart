package com.toko.frozen.model;

public class PembelianItem {
    private final String nama;
    private final Integer jumlah;
    private final Integer subtotal;

    public PembelianItem(String nama, Integer jumlah, Integer subtotal) {
        this.nama = nama;
        this.jumlah = jumlah;
        this.subtotal = subtotal;
    }

    public String getNama() {
        return nama;
    }

    public Integer getJumlah() {
        return jumlah;
    }

    public Integer getSubtotal() {
        return subtotal;
    }
}
