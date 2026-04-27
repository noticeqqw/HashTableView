package hash.table;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите ёмкость таблицы (количество элементов): ");
        int n = readInt(scanner);

        // создаём таблицу с коэффициентом заполнения 0.5
        HashTable<String> table = new HashTable<>(n, 0.5);
        System.out.println("Создана таблица размером m = " + table.size());

        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt(scanner);

            switch (choice) {
                case 1:
                    // вставить элемент
                    System.out.print("Ключ (целое число): ");
                    int key = readInt(scanner);
                    System.out.print("Данные (строка): ");
                    String data = scanner.next();
                    boolean inserted = table.insert(key, data);
                    if (inserted) {
                        System.out.println("Вставлено.");
                    } else {
                        System.out.println("Не вставлено (дубликат или таблица полна).");
                    }
                    printDiag(table);
                    break;

                case 2:
                    // поиск элемента
                    System.out.print("Ключ для поиска: ");
                    int searchKey = readInt(scanner);
                    String result = table.search(searchKey);
                    if (result != null) {
                        System.out.println("Найдено: " + result);
                    } else {
                        System.out.println("Не найдено.");
                    }
                    printDiag(table);
                    break;

                case 3:
                    // удалить элемент
                    System.out.print("Ключ для удаления: ");
                    int deleteKey = readInt(scanner);
                    boolean deleted = table.delete(deleteKey);
                    if (deleted) {
                        System.out.println("Удалено.");
                    } else {
                        System.out.println("Не найдено.");
                    }
                    printDiag(table);
                    break;

                case 4:
                    // вывести структуру таблицы
                    table.print();
                    break;

                case 5:
                    // размер таблицы
                    System.out.println("Размер таблицы m = " + table.size());
                    break;

                case 6:
                    // количество элементов
                    System.out.println("Количество элементов = " + table.count());
                    break;

                case 7:
                    // проверить пустоту
                    if (table.isEmpty()) {
                        System.out.println("Таблица пуста.");
                    } else {
                        System.out.println("Таблица не пуста.");
                    }
                    break;

                case 8:
                    // коэффициент заполнения
                    System.out.printf("Коэффициент заполнения α = %.4f%n", table.loadFactor());
                    break;

                case 9:
                    // очистить таблицу
                    table.clear();
                    System.out.println("Таблица очищена.");
                    break;

                case 10:
                    // обход итератором
                    System.out.println("Обход итератором:");
                    int index = 0;
                    HashTableIterator<Integer, String> it = table.begin();
                    HashTableIterator<Integer, String> end = table.end();
                    while (it.notEquals(end)) {
                        System.out.println("  [" + index + "] " + it.getValue());
                        index++;
                        it = it.next();
                    }
                    if (index == 0) {
                        System.out.println("  (пусто)");
                    }
                    break;

                case 11:
                    // диагностика последней операции
                    printDiag(table);
                    break;

                case 12:
                    // тест χ²
                    HashTableTests.chiSquareTest(table.size());
                    break;

                case 13:
                    // тест трудоёмкости
                    HashTableTests.complexityTest();
                    break;

                case 0:
                    running = false;
                    break;

                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
                    break;
            }
        }

        System.out.println("Выход.");
        scanner.close();
    }

    // выводим меню
    private static void printMenu() {
        System.out.println();
        System.out.println("--- МЕНЮ ---");
        System.out.println(" 1. Вставить элемент");
        System.out.println(" 2. Найти элемент");
        System.out.println(" 3. Удалить элемент");
        System.out.println(" 4. Структура таблицы");
        System.out.println(" 5. Размер таблицы");
        System.out.println(" 6. Количество элементов");
        System.out.println(" 7. Проверить пустоту таблицы");
        System.out.println(" 8. Коэффициент заполнения");
        System.out.println(" 9. Очистить таблицу");
        System.out.println("10. Обход итератором");
        System.out.println("11. Последние операции");
        System.out.println("12. Тест равномерности χ²");
        System.out.println("13. Тест трудоёмкости");
        System.out.println(" 0. Выход");
        System.out.print("Выбор: ");
    }

    // вывод диагностической информации о последней операции
    private static void printDiag(HashTable<String> table) {
        System.out.printf("  k=%d  k'=%d  h(k')=%d  зондирований=%d%n",
                table.getLastKeyOriginal(),
                table.getLastKeyPrime(),
                table.getLastHashIndex(),
                table.getLastProbeCount());
    }

    // безопасное чтение целого числа - повторяем запрос если ввели не то
    private static int readInt(Scanner scanner) {
        while (true) {
            try {
                int value = scanner.nextInt();
                return value;
            } catch (InputMismatchException e) {
                scanner.nextLine(); // очищаем буфер
                System.out.print("Ошибка ввода. Введите целое число: ");
            }
        }
    }
}
