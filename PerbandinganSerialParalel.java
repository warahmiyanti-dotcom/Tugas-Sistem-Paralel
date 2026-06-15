import java.util.Random;

class SumThread extends Thread {

    private int[] data;
    private int start;
    private int end;
    private long partialSum;

    public SumThread(int[] data, int start, int end) {
        this.data = data;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {

        partialSum = 0;

        for (int i = start; i < end; i++) {
            partialSum += data[i];
        }
    }

    public long getPartialSum() {
        return partialSum;
    }
}

public class PerbandinganSerialParalel {

    public static void main(String[] args) throws InterruptedException {

        int jumlahThread = 4;       //Menggunakan 4 thread untuk pengujian paralel

        int[] ukuranData = {        //Berbagai ukuran data untuk pengujian
                1_000_000,
                10_000_000,
                50_000_000,
                100_000_000
        };

        long[] dataSerial = new long[ukuranData.length];
        long[] dataParalel = new long[ukuranData.length];

        System.out.println("==============================================================");
        System.out.println("PENJUMLAHAN DATA BESAR SECARA SERIAL DAN PARALEL");
        System.out.println("==============================================================");
        System.out.println("Jumlah Thread : " + jumlahThread);
        System.out.println();

        for (int no = 0; no < ukuranData.length; no++) {

            int n = ukuranData[no];

            System.out.println("--------------------------------------------------------------");
            System.out.println("Pengujian ke-" + (no + 1));
            System.out.println("Ukuran Data : " + String.format("%,d", n));
            System.out.println("--------------------------------------------------------------");

            // Membuat array berisi angka acak
            int[] data = new int[n];

            Random random = new Random();

            for (int i = 0; i < n; i++) {
                data[i] = random.nextInt(100) + 1;
            }

            // ==========================
            // SERIAL
            // ==========================
            long startSerial = System.currentTimeMillis();

            long totalSerial = 0;

            for (int i = 0; i < data.length; i++) {
                totalSerial += data[i];
            }

            long waktuSerial =
                    System.currentTimeMillis() - startSerial;

            // ==========================
            // PARALEL
            // ==========================
            SumThread[] threads =
                    new SumThread[jumlahThread];

            int bagian = n / jumlahThread;

            long startParalel =
                    System.currentTimeMillis();

            for (int i = 0; i < jumlahThread; i++) {

                int start = i * bagian;

                int end;

                if (i == jumlahThread - 1) {
                    end = n;
                } else {
                    end = start + bagian;
                }

                threads[i] =
                        new SumThread(data, start, end);

                threads[i].start();
            }

            long totalParalel = 0;

            for (int i = 0; i < jumlahThread; i++) {

                threads[i].join();

                totalParalel +=
                        threads[i].getPartialSum();
            }

            long waktuParalel =
                    System.currentTimeMillis() - startParalel;

            // Simpan waktu untuk grafik
            dataSerial[no] = waktuSerial;
            dataParalel[no] = waktuParalel;

            // Validasi hasil
            String status =
                    (totalSerial == totalParalel)
                            ? "VALID"
                            : "ERROR";

            double speedup =
                    (double) waktuSerial / waktuParalel;

            // Output hasil
            System.out.println("HASIL SERIAL");
            System.out.println("Total Penjumlahan : " + totalSerial);
            System.out.println("Waktu Eksekusi    : " + waktuSerial + " ms");
            System.out.println();

            System.out.println("HASIL PARALEL");
            System.out.println("Total Penjumlahan : " + totalParalel);
            System.out.println("Waktu Eksekusi    : " + waktuParalel + " ms");
            System.out.println();

            System.out.println("ANALISIS");
            System.out.println("Status            : " + status);
            System.out.printf("Speedup           : %.2fx%n", speedup);

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
        System.out.println("==============================================================");
        System.out.println("GRAFIK PERBANDINGAN WAKTU EKSEKUSI");
        System.out.println("==============================================================");

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

        System.out.println();
        System.out.println("==============================================================");
        System.out.println("SELESAI");
        System.out.println("==============================================================");
    }

    // Fungsi membuat grafik batang sederhana
    public static void cetakBar(long nilai) {

        int panjang = (int) (nilai / 2);

        if (panjang < 1) {
            panjang = 1;
        }

        for (int i = 0; i < panjang; i++) {
            System.out.print("█");
        }
    }
}