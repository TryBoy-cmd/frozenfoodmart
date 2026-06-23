package com.toko.frozen.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.toko.frozen.model.Produk;

public interface ProdukRepository extends JpaRepository<Produk, Long> {
    List<Produk> findByNamaContainingIgnoreCase(String nama);

    @Query("SELECT COALESCE(SUM(p.harga * p.jumlahTerjual), 0) FROM Produk p")
    Long getTotalPendapatan();
}