package hash.table;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Введите ёмкость таблицы (количество элементов): ");

        int n = readInt(sc);
        HashTable<String> table = new HashTable<>(n, 0.5);
        System.out.println("Создана таблица размером m = " + table.size());

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt(sc);
            switch (choice) {
                case 1 -> {
                    System.out.print("Ключ (целое число): ");
                    int k = readInt(sc);
                    System.out.print("Данные (строка): ");
                    String data = sc.next();
                    boolean ok = table.insert(k, data);
                    System.out.println(ok ? "Вставлено." : "Не вставлено (дубликат или таблица полна).");
                    printDiag(table);
                }
                case 2 -> {
                    System.out.print("Ключ для поиска: ");
                    int k = readInt(sc);
                    String result = table.search(k);
                    System.out.println(result != null ? "Найдено: " + result : "Не найдено.");
                    printDiag(table);
                }
                case 3 -> {
                    System.out.print("Ключ для удаления: ");
                    int k = readInt(sc);
                    boolean deleted = table.delete(k);
                    System.out.println(deleted ? "Удалено." : "Не найдено.");
                    printDiag(table);
                }
                case 4  -> table.print();
                case 5  -> System.out.println("Размер таблицы m = " + table.size());
                case 6  -> System.out.println("Количество элементов = " + table.count());
                case 7  -> System.out.println("Таблица " + (table.isEmpty() ? "пуста." : "не пуста."));
                case 8  -> System.out.printf("Коэффициент заполнения α = %.4f%n", table.loadFactor());
                case 9  -> { table.clear(); System.out.println("Таблица очищена."); }
                case 10 -> {
                    System.out.println("Обход итератором:");
                    int idx = 0;
                    for (HashTableIterator<Integer, String> it = table.begin();
                         it.notEquals(table.end());
                         it = it.next()) {
                        System.out.println("  [" + idx++ + "] " + it.getValue());
                    }
                    if (idx == 0) System.out.println("  (пусто)");
                }
                case 11 -> printDiag(table);
                case 12 -> HashTableTests.chiSquareTest(table.size());
                case 13 -> HashTableTests.complexityTest();
                case 0  -> running = false;
                default -> System.out.println("Неверный выбор.");
            }
        }
        System.out.println("Выход.");
        sc.close();
    }

    private static void printMenu() {
        System.out.print("""
                 1. Вставить элемент
                 2. Найти элемент
                 3. Удалить элемент
                 4. Структура таблицы
                 5. Размер таблицы
                 6. Количество элементов
                 7. Проверить пустоту таблицы
                 8. Коэффициент заполнения
                 9. Очистить таблицу
                10. Обход итератором 
                11. Последние операции
                12. Тест равномерности χ²
                13. Тест трудоёмкости
                 0. Выход
                Выбор: """);
    }

    private static void printDiag(HashTable<String> table) {
        System.out.printf("  k=%d  k'=%d  h(k')=%d  зондирований=%d%n",
                table.getLastKeyOriginal(),
                table.getLastKeyPrime(),
                table.getLastHashIndex(),
                table.getLastProbeCount());
    }

    private static int readInt(Scanner sc) {
        while (true) {
            try {
                return sc.nextInt();
            } catch (InputMismatchException e) {
                sc.nextLine();
                System.out.print("Ошибка ввода. Введите целое число: ");
            }
        }
    }
}
