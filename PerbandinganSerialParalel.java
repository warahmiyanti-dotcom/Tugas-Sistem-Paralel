import java.util.Random;

// Kelas thread untuk menghitung jumlah sebagian (partial sum) dari array
class SumThread extends Thread {

    // Array data yang akan dijumlahkan
    private int[] data;

    // Indeks awal dan akhir bagian array yang akan diproses thread
    private int start;
    private int end;

    // Menyimpan hasil penjumlahan sebagian
    private long partialSum;

    // Konstruktor untuk menerima data dan batas indeks
    public SumThread(int[] data, int start, int end) {
        this.data = data;
        this.start = start;
        this.end = end;
    }

    // Method yang akan dijalankan ketika thread diaktifkan
    @Override
    public void run() {

        // Inisialisasi nilai awal penjumlahan
        partialSum = 0;

        // Menjumlahkan elemen array sesuai bagian yang diberikan
        for (int i = start; i < end; i++) {
            partialSum += data[i];
        }
    }

    // Mengembalikan hasil penjumlahan sebagian
    public long getPartialSum() {
        return partialSum;
    }
}

public class PerbandinganSerialParalel {

    public static void main(String[] args) throws InterruptedException {

        // Menggunakan 4 thread untuk proses paralel
        int jumlahThread = 4;

        // Ukuran data yang akan diuji
        int[] ukuranData = {
                1_000_000,
                10_000_000,
                50_000_000,
                100_000_000
        };

        // Menyimpan waktu eksekusi serial untuk setiap pengujian
        long[] dataSerial = new long[ukuranData.length];

        // Menyimpan waktu eksekusi paralel untuk setiap pengujian
        long[] dataParalel = new long[ukuranData.length];

        // Menampilkan judul program
        System.out.println("==============================================================");
        System.out.println("PENJUMLAHAN DATA BESAR SECARA SERIAL DAN PARALEL");
        System.out.println("==============================================================");
        System.out.println("Jumlah Thread : " + jumlahThread);
        System.out.println();

        // Melakukan pengujian untuk setiap ukuran data
        for (int no = 0; no < ukuranData.length; no++) {

            // Mengambil ukuran data saat ini
            int n = ukuranData[no];

            // Menampilkan informasi pengujian
            System.out.println("--------------------------------------------------------------");
            System.out.println("Pengujian ke-" + (no + 1));
            System.out.println("Ukuran Data : " + String.format("%,d", n));
            System.out.println("--------------------------------------------------------------");

            // Membuat array sesuai ukuran data
            int[] data = new int[n];

            // Objek Random untuk menghasilkan angka acak
            Random random = new Random();

            // Mengisi array dengan angka acak 1–100
            for (int i = 0; i < n; i++) {
                data[i] = random.nextInt(100) + 1;
            }

            // ==========================
            // SERIAL
            // ==========================

            // Mencatat waktu mulai proses serial
            long startSerial = System.currentTimeMillis();

            // Menyimpan total hasil penjumlahan serial
            long totalSerial = 0;

            // Menjumlahkan seluruh elemen array secara berurutan
            for (int i = 0; i < data.length; i++) {
                totalSerial += data[i];
            }

            // Menghitung waktu eksekusi serial
            long waktuSerial =
                    System.currentTimeMillis() - startSerial;

            // ==========================
            // PARALEL
            // ==========================

            // Membuat array objek thread
            SumThread[] threads =
                    new SumThread[jumlahThread];

            // Menentukan ukuran data yang diproses setiap thread
            int bagian = n / jumlahThread;

            // Mencatat waktu mulai proses paralel
            long startParalel =
                    System.currentTimeMillis();

            // Membuat dan menjalankan seluruh thread
            for (int i = 0; i < jumlahThread; i++) {

                // Menentukan indeks awal bagian data
                int start = i * bagian;

                int end;

                // Thread terakhir menangani sisa data yang belum terbagi
                if (i == jumlahThread - 1) {
                    end = n;
                } else {
                    end = start + bagian;
                }

                // Membuat objek thread
                threads[i] =
                        new SumThread(data, start, end);

                // Menjalankan thread
                threads[i].start();
            }

            // Menyimpan total hasil penjumlahan paralel
            long totalParalel = 0;

            // Menunggu seluruh thread selesai
            for (int i = 0; i < jumlahThread; i++) {

                // Sinkronisasi thread
                threads[i].join();

                // Menambahkan hasil penjumlahan masing-masing thread
                totalParalel +=
                        threads[i].getPartialSum();
            }

            // Menghitung waktu eksekusi paralel
            long waktuParalel =
                    System.currentTimeMillis() - startParalel;

            // Menyimpan waktu serial untuk kebutuhan grafik
            dataSerial[no] = waktuSerial;

            // Menyimpan waktu paralel untuk kebutuhan grafik
            dataParalel[no] = waktuParalel;

            // Memastikan hasil serial dan paralel sama
            String status =
                    (totalSerial == totalParalel)
                            ? "VALID"
                            : "ERROR";

            // Menghitung speedup (percepatan)
            double speedup =
                    (double) waktuSerial / waktuParalel;

            // Menampilkan hasil serial
            System.out.println("HASIL SERIAL");
            System.out.println("Total Penjumlahan : " + totalSerial);
            System.out.println("Waktu Eksekusi    : " + waktuSerial + " ms");
            System.out.println();

            // Menampilkan hasil paralel
            System.out.println("HASIL PARALEL");
            System.out.println("Total Penjumlahan : " + totalParalel);
            System.out.println("Waktu Eksekusi    : " + waktuParalel + " ms");
            System.out.println();

            // Menampilkan hasil analisis
            System.out.println("ANALISIS");
            System.out.println("Status            : " + status);
            System.out.printf("Speedup           : %.2fx%n", speedup);

            // Menentukan metode yang lebih cepat
            if (speedup > 1) {
                System.out.println("Metode Tercepat   : PARALEL");
            } else {
                System.out.println("Metode Tercepat   : SERIAL");
            }

            System.out.println();
        }

        // ==========================
        // GRAFIK HASIL PENGUJIAN
        // ==========================

        // Menampilkan grafik perbandingan waktu eksekusi
        System.out.println("==============================================================");
        System.out.println("GRAFIK PERBANDINGAN WAKTU EKSEKUSI");
        System.out.println("==============================================================");

        // Menampilkan grafik batang untuk setiap ukuran data
        for (int i = 0; i < ukuranData.length; i++) {

            System.out.println();
            System.out.println("Ukuran Data : "
                    + String.format("%,d", ukuranData[i]));

            System.out.print("Serial   | ");
            cetakBar(dataSerial[i]);
            System.out.println(" (" + dataSerial[i] + " ms)");

            System.out.print("Paralel  | ");
            cetakBar(dataParalel[i]);
            System.out.println(" (" + dataParalel[i] + " ms)");
        }

        // Menampilkan pesan akhir program
        System.out.println();
        System.out.println("==============================================================");
        System.out.println("SELESAI");
        System.out.println("==============================================================");
    }

    // Fungsi untuk membuat grafik batang sederhana menggunakan karakter █
    public static void cetakBar(long nilai) {

        // Menentukan panjang batang berdasarkan waktu eksekusi
        int panjang = (int) (nilai / 2);

        // Minimal panjang batang adalah 1 karakter
        if (panjang < 1) {
            panjang = 1;
        }

        // Mencetak karakter batang sesuai panjang yang dihitung
        for (int i = 0; i < panjang; i++) {
            System.out.print("█");
        }
    }
}
