package hash.table;

import java.util.Random;

public class HashTableTests {

    public static void chiSquareTest(int m) {
        int P = 20 * m;
        Random rng = new Random();
        int runs = 7;
        double sumChi = 0;

        System.out.printf("%nТест χ² (m=%d, P=%d, прогонов=%d)%n", m, P, runs);
        for (int r = 0; r < runs; r++) {
            int[] freq = new int[m];
            for (int i = 0; i < P; i++) {
                int k = rng.nextInt(1_000_000_001);
                int kp = KeyConverter.toNatural(k);
                freq[kp % m]++;
            }
            double chi = 0;
            double expected = (double) P / m;
            for (int i = 0; i < m; i++) {
                double d = freq[i] - expected;
                chi += d * d / expected;
            }
            sumChi += chi;
            System.out.printf("  Прогон %d: χ² = %.2f%n", r + 1, chi);
        }

        double avgChi = sumChi / runs;
        double lo = m - Math.sqrt(m);
        double hi = m + Math.sqrt(m);
        System.out.printf("Среднее χ² = %.2f  (ожидаемый интервал [%.2f, %.2f]) — %s%n",
                avgChi, lo, hi, (avgChi >= lo && avgChi <= hi) ? "НОРМА" : "ВНЕ ИНТЕРВАЛА");
    }

    public static void complexityTest() {
        System.out.println();
        int n = 1000;
        System.out.printf("%-6s | %-16s | %-18s | %-18s | %-20s%n",
                "α", "Вставка (эксп)", "Поиск усп.(эксп)", "Поиск усп.(теор)", "Поиск неусп.(теор)");
        System.out.println("-".repeat(90));

        Random rng = new Random(42);

        for (int ai = 1; ai <= 9; ai++) {
            double alpha = ai / 10.0;
            HashTable<String> ht = new HashTable<>(n, alpha);
            int m = ht.size();
            int target = (int) Math.round(alpha * m);

            int[] keys = new int[target];
            int inserted = 0;
            while (inserted < target) {
                int k = rng.nextInt(1_000_000_001);
                if (ht.insert(k, "x")) keys[inserted++] = k;
            }

            long sumSearch = 0;
            for (int k : keys) {
                ht.search(k);
                sumSearch += ht.getLastProbeCount();
            }
            double expSucc = (double) sumSearch / target;

            int misses = 0;
            long sumMiss = 0;
            int tries = 0;
            while (misses < target && tries < target * 10) {
                tries++;
                int k = rng.nextInt(1_000_000_001);
                if (ht.search(k) == null) {
                    sumMiss += ht.getLastProbeCount();
                    misses++;
                }
            }

            double a = (double) inserted / m;
            double theorySucc = 0.5 * (1 + 1.0 / (1 - a));
            double theoryFail = 0.5 * (1 + 1.0 / ((1 - a) * (1 - a)));

            System.out.printf("%-6.2f | %-16.2f | %-18.2f | %-18.2f | %-20.2f%n",
                    alpha, expSucc, expSucc, theorySucc, theoryFail);
        }
    }
}
