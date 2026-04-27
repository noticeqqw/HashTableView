package hash.table;

import java.util.Random;

// тесты для хэш-таблицы
// тест равномерности и тест трудоёмкости
public class HashTableTests {

    // тест равномерности χ² (хи-квадрат)
    // проверяем что хэш-функция распределяет ключи равномерно
    public static void chiSquareTest(int m) {
        // по условию P = 20 * m
        int P = 20 * m;
        int numberOfRuns = 7;
        double totalChi = 0;

        System.out.println();
        System.out.printf("Тест χ² (m=%d, P=%d, прогонов=%d)%n", m, P, numberOfRuns);

        Random random = new Random();

        for (int run = 0; run < numberOfRuns; run++) {
            // массив частот попаданий в каждую ячейку
            int[] frequency = new int[m];

            // генерируем P случайных ключей
            for (int i = 0; i < P; i++) {
                int key = random.nextInt(1_000_000_001);
                int keyPrime = KeyConverter.toNatural(key);
                int cell = keyPrime % m;
                frequency[cell]++;
            }

            // считаем хи-квадрат
            double expectedFrequency = (double) P / m;
            double chiSquare = 0;

            for (int i = 0; i < m; i++) {
                double diff = frequency[i] - expectedFrequency;
                chiSquare += (diff * diff) / expectedFrequency;
            }

            totalChi += chiSquare;
            System.out.printf("  Прогон %d: χ² = %.2f%n", run + 1, chiSquare);
        }

        double avgChi = totalChi / numberOfRuns;

        // ожидаемый диапазон: m ± sqrt(m)
        double lowerBound = m - Math.sqrt(m);
        double upperBound = m + Math.sqrt(m);

        String verdict;
        if (avgChi >= lowerBound && avgChi <= upperBound) {
            verdict = "НОРМА";
        } else {
            verdict = "ВНЕ ИНТЕРВАЛА";
        }

        System.out.printf("Среднее χ² = %.2f  (ожидаемый интервал [%.2f, %.2f]) — %s%n",
                avgChi, lowerBound, upperBound, verdict);
    }

    // тест трудоёмкости - смотрим сколько зондирований нужно при разных α
    public static void complexityTest() {
        System.out.println();

        int n = 1000; // количество элементов для теста

        System.out.printf("%-6s | %-16s | %-18s | %-18s | %-20s%n",
                "α", "Вставка (эксп)", "Поиск усп.(эксп)", "Поиск усп.(теор)", "Поиск неусп.(теор)");
        System.out.println("-".repeat(90));

        Random random = new Random(42); // фиксированный seed для воспроизводимости

        // проверяем α от 0.1 до 0.9
        for (int ai = 1; ai <= 9; ai++) {
            double alpha = ai / 10.0;

            HashTable<String> hashTable = new HashTable<>(n, alpha);
            int m = hashTable.size();

            // сколько элементов нужно вставить
            int targetCount = (int) Math.round(alpha * m);

            // вставляем элементы и запоминаем ключи
            int[] insertedKeys = new int[targetCount];
            int insertedCount = 0;

            while (insertedCount < targetCount) {
                int key = random.nextInt(1_000_000_001);
                boolean success = hashTable.insert(key, "x");
                if (success) {
                    insertedKeys[insertedCount] = key;
                    insertedCount++;
                }
            }

            // считаем среднее число зондирований при успешном поиске
            long totalSearchProbes = 0;
            for (int i = 0; i < insertedCount; i++) {
                hashTable.search(insertedKeys[i]);
                totalSearchProbes += hashTable.getLastProbeCount();
            }
            double avgSuccessfulSearch = (double) totalSearchProbes / insertedCount;

            // считаем среднее число зондирований при неуспешном поиске
            int missCount = 0;
            long totalMissProbes = 0;
            int maxTries = targetCount * 10;
            int tries = 0;

            while (missCount < targetCount && tries < maxTries) {
                tries++;
                int key = random.nextInt(1_000_000_001);
                String found = hashTable.search(key);
                if (found == null) {
                    totalMissProbes += hashTable.getLastProbeCount();
                    missCount++;
                }
            }

            // теоретические значения для линейного зондирования
            double realAlpha = (double) insertedCount / m;
            double theorySuccessful = 0.5 * (1.0 + 1.0 / (1.0 - realAlpha));
            double theoryUnsuccessful = 0.5 * (1.0 + 1.0 / ((1.0 - realAlpha) * (1.0 - realAlpha)));

            System.out.printf("%-6.2f | %-16.2f | %-18.2f | %-18.2f | %-20.2f%n",
                    alpha, avgSuccessfulSearch, avgSuccessfulSearch, theorySuccessful, theoryUnsuccessful);
        }
    }
}
