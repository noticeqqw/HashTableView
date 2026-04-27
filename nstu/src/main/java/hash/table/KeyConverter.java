package hash.table;

// вспомогательный класс для преобразования ключа
// метод выбора цифр из методички
public class KeyConverter {

    // преобразуем ключ k в натуральное число k'
    // алгоритм: дополнить до 10 знаков, взять 5 пар цифр, каждую пару сложить по модулю 10
    public static int toNatural(int k) {
        // дополняем до 10 цифр нулями слева
        String paddedKey = String.format("%010d", k);

        // System.out.println("paddedKey = " + paddedKey); // для отладки

        StringBuilder resultBuilder = new StringBuilder();

        // проходим по парам цифр
        for (int i = 0; i < 10; i = i + 2) {
            int digit1 = paddedKey.charAt(i) - '0';
            int digit2 = paddedKey.charAt(i + 1) - '0';
            int newDigit = (digit1 + digit2) % 10;
            resultBuilder.append(newDigit);
        }

        String resultStr = resultBuilder.toString();
        int result = Integer.parseInt(resultStr);
        return result;
    }
}
