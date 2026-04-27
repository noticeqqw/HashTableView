package hash.table;

public class KeyConverter {

    /**
     * Преобразование k → k' методом выбора цифр с модификацией.
     *
     * Алгоритм (методичка, разд. 5.1.1):
     *   1. Дополнить k нулями слева до 10 знаков.
     *   2. Разбить на 5 пар цифр: (d0,d1), (d2,d3), (d4,d5), (d6,d7), (d8,d9).
     *   3. Каждая пара → (di + di+1) mod 10 — выбранная цифра «модифицируется»
     *      суммой пропущенной цифры.
     *   4. Результат — 5-значное число k'.
     *
     * Пример: k=305040201 → "0305040201"
     *   (0+3)%10=3, (0+5)%10=5, (0+4)%10=4, (0+2)%10=2, (0+1)%10=1 → k'=35421
     */
    public static int toNatural(int k) {
        String padded = String.format("%010d", k);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 10; i += 2) {
            int d1 = padded.charAt(i)     - '0';
            int d2 = padded.charAt(i + 1) - '0';
            result.append((d1 + d2) % 10);
        }
        return Integer.parseInt(result.toString());
    }
}
