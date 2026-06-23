package com.toko.frozen.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.toko.frozen.model.PembelianItem;
import com.toko.frozen.model.Produk;
import com.toko.frozen.repository.ProdukRepository;

@Controller
@RequestMapping("/")
public class ProdukController {

    private final ProdukRepository repo;

    public ProdukController(ProdukRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String index(@RequestParam(value = "cari", required = false) String cari, Model model) {
        if (cari != null && !cari.isEmpty()) {
            model.addAttribute("daftarProduk", repo.findByNamaContainingIgnoreCase(cari));
        } else {
            model.addAttribute("daftarProduk", repo.findAll());
        }
        model.addAttribute("totalPendapatan", repo.getTotalPendapatan());
        model.addAttribute("produkBaru", new Produk());
        return "index";
    }

    @PostMapping("/simpan")
    public String simpan(@ModelAttribute("produkBaru") Produk produk) {
        Objects.requireNonNull(produk, "produk must not be null");
        if (produk.getJumlahTerjual() == null) produk.setJumlahTerjual(0);
        repo.save(produk);
        return "redirect:/";
    }

    @GetMapping("/hapus/{id}")
    public String hapus(@PathVariable("id") long id) {
        repo.deleteById(id);
        return "redirect:/";
    }

    @PostMapping("/beli")
    public String beli(@RequestParam("id") long id, @RequestParam("jumlah") int jumlah, Model model) {
        Produk p = repo.findById(id).orElse(null);

        if (p == null) {
            model.addAttribute("gagal", "Barang tidak ditemukan!");
            model.addAttribute("daftarProduk", repo.findAll());
            model.addAttribute("produkBaru", new Produk());
            return "index";
        }

        if (jumlah <= 0) {
            model.addAttribute("gagal", "Jumlah pembelian harus lebih dari 0!");
            model.addAttribute("daftarProduk", repo.findAll());
            model.addAttribute("produkBaru", new Produk());
            return "index";
        }

        if (jumlah <= p.getStok()) {
            int total = jumlah * p.getHarga();
            long diskon = 0;
            if (total > 200000) diskon = total * 20 / 100;
            else if (total > 100000) diskon = total * 10 / 100;
            long bayar = total - diskon;

            p.setStok(p.getStok() - jumlah);
            if (p.getJumlahTerjual() == null) p.setJumlahTerjual(jumlah);
            else p.setJumlahTerjual(p.getJumlahTerjual() + jumlah);
            repo.save(p);

            model.addAttribute("sukses", true);
            model.addAttribute("strukItems", List.of(new PembelianItem(p.getNama(), jumlah, total)));
            model.addAttribute("strukSubtotal", total);
            model.addAttribute("strukDiskon", diskon);
            model.addAttribute("strukTotal", bayar);
        } else {
            model.addAttribute("gagal", "Stok tidak mencukupi!");
        }

        model.addAttribute("daftarProduk", repo.findAll());
        model.addAttribute("produkBaru", new Produk());
        return "index";
    }

    @PostMapping("/beli-multiple")
    public String beliMultiple(@RequestParam("id") List<Long> ids,
                               @RequestParam("jumlah") List<Integer> jumlahs,
                               Model model) {
        if (ids == null || jumlahs == null || ids.size() != jumlahs.size()) {
            model.addAttribute("gagal", "Terjadi kesalahan input pembelian.");
            model.addAttribute("daftarProduk", repo.findAll());
            model.addAttribute("produkBaru", new Produk());
            return "index";
        }

        List<PembelianItem> items = new ArrayList<>();
        List<Produk> produkUntukSimpan = new ArrayList<>();
        List<Integer> jumlahUntukSimpan = new ArrayList<>();
        long total = 0;

        for (int i = 0; i < ids.size(); i++) {
            Integer jumlah = jumlahs.get(i);
            if (jumlah == null || jumlah <= 0) {
                continue;
            }

            Produk p = repo.findById(ids.get(i)).orElse(null);
            if (p == null) {
                model.addAttribute("gagal", "Barang dengan ID " + ids.get(i) + " tidak ditemukan!");
                model.addAttribute("daftarProduk", repo.findAll());
                model.addAttribute("produkBaru", new Produk());
                return "index";
            }

            if (jumlah > p.getStok()) {
                model.addAttribute("gagal", "Stok tidak mencukupi untuk produk " + p.getNama() + "!");
                model.addAttribute("daftarProduk", repo.findAll());
                model.addAttribute("produkBaru", new Produk());
                return "index";
            }

            int subtotal = jumlah * p.getHarga();
            items.add(new PembelianItem(p.getNama(), jumlah, subtotal));
            produkUntukSimpan.add(p);
            jumlahUntukSimpan.add(jumlah);
            total += subtotal;
        }

        if (items.isEmpty()) {
            model.addAttribute("gagal", "Pilih sedikitnya satu produk dengan jumlah lebih dari 0.");
            model.addAttribute("daftarProduk", repo.findAll());
            model.addAttribute("produkBaru", new Produk());
            return "index";
        }

        long diskon = 0;
        if (total > 200000) diskon = total * 20 / 100;
        else if (total > 100000) diskon = total * 10 / 100;
        long bayar = total - diskon;

        for (int i = 0; i < produkUntukSimpan.size(); i++) {
            Produk p = produkUntukSimpan.get(i);
            int jumlah = jumlahUntukSimpan.get(i);
            p.setStok(p.getStok() - jumlah);
            if (p.getJumlahTerjual() == null) p.setJumlahTerjual(jumlah);
            else p.setJumlahTerjual(p.getJumlahTerjual() + jumlah);
        }
        repo.saveAll(produkUntukSimpan);

        model.addAttribute("sukses", true);
        model.addAttribute("strukItems", items);
        model.addAttribute("strukSubtotal", total);
        model.addAttribute("strukDiskon", diskon);
        model.addAttribute("strukTotal", bayar);
        model.addAttribute("daftarProduk", repo.findAll());
        model.addAttribute("produkBaru", new Produk());
        return "index";
    }
}
