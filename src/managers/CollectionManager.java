package managers;


import main.Main;
import structs.MusicBand;
import structs.Studio;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Класс для управления коллекцией {@link main.Main#musicBands}
 */
public class CollectionManager {
    private static int nextId = 0;
    private static ZonedDateTime dateInitialization = null;
    private static final HashSet<MusicBand> set = Main.musicBands;

    public static int getNextId() {
        return nextId;
    }

    public static void setNextId(int nextId) {
        if (nextId == 0) {
            if (set.isEmpty()) {
                CollectionManager.nextId = 1;
            } else {
                CollectionManager.nextId = Collections.max(set).getId() + 1;
            }
        } else {
            CollectionManager.nextId = nextId;
        }
    }

    public static void incrementNextId() {
        CollectionManager.nextId++;
    }

    public static void setDateInitialization(ZonedDateTime dateInitialization) {
        CollectionManager.dateInitialization = Objects.requireNonNullElseGet(dateInitialization, ZonedDateTime::now);
    }

    public static ZonedDateTime getDateInitialization() {
        return dateInitialization;
    }

    /**
     * Удаление объекта из коллекции по его id
     *
     * @param id id элемента, который удаляем
     */
    public static void removeByID(int id) {
        set.removeIf(band -> band.getId() == id);
    }

    /**
     * Очищает всю коллекцию
     */

    public static void clear() {
        set.clear();
    }

    /**
     * Добавляет элемент в коллекцию
     *
     * @param band элемент, который будет добавлен в коллекцию
     */
    public static void add(MusicBand band) {
        set.add(band);
        incrementNextId();
    }


    /**
     * Выводит информацию о коллекции (используется в {@link commands.Help})
     */
    public static void info() {
        System.out.printf("Тип: %s\n", set.getClass());
        System.out.printf("Дата инициализации: %s\n", CollectionManager.getDateInitialization().format(DateTimeFormatter.ISO_DATE_TIME));
        System.out.printf("Количество элементов в коллекции: %d\n", set.size());
    }

    /**
     * Проверяем наличие объекта с таким id в коллекции
     *
     * @param id который нужно проверить
     * @return true или false (есть или нет такого id)
     */

    public static boolean checkId(int id) {
        return set.stream().anyMatch(band -> band.getId() == id);
    }

    /**
     * Обновление объекта с заданным id
     *
     * @param band новый объект
     */

    public static void update(MusicBand band, int id) {
        removeByID(id);
        set.add(band);
    }

    /**
     * Удалить из коллекции один элемент, значение поля numberOfParticipants которого эквивалентно заданному
     *
     * @param number параметр для сравнения
     * @return id удаленного объекта или -1 если таких нет
     */

    public static int removeAnyByNumberOfParticipants(Integer number) {
        MusicBand band = set.stream().filter(b -> b.getNumberOfParticipants().equals(number)).findFirst().orElse(null);
        if (band != null) {
            set.remove(band);
            return band.getId();
        } else {
            return -1;
        }
    }

    /**
     * Вывести количество элементов, значение поля studio которых больше заданного
     *
     * @param studio параметр для сравнения
     * @return количество подходящих элементов
     */

    public static long countGreaterThanStudio(Studio studio) {
        long count = 0;
        Studio.ComparatorStudio studioComparator = new Studio.ComparatorStudio();
        for (MusicBand band : set) {
            if (band.getStudio() != null && studioComparator.compare(band.getStudio(), studio) > 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * Вывести в стандартный поток вывода все элементы коллекции в строковом представлении
     */
    public static void showAll() {
        if (set.isEmpty()) {
            System.out.println("Коллекция пуста");
        } else {
            for (MusicBand band : set) {
                System.out.println(band);
//                System.out.println();
            }
        }
    }

    /**
     * Вывести элементы коллекции в порядке убывания
     */

    public static void printDescending() {
        if (set.isEmpty()) {
            System.out.println("Коллекция пуста");
        } else {
            TreeSet<MusicBand> sortedSet = new TreeSet<>(Comparator.reverseOrder());
            sortedSet.addAll(set);
            for (MusicBand band : sortedSet) {
                System.out.println(band);
            }
        }
    }

    /**
     * Добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции
     *
     * @param band элемент, который хотим добавить
     * @return true или false (добавился элемент или нет)
     */

    public static boolean addIfMin(MusicBand band) {
        boolean flag = true;
        for (MusicBand el : set) {
            if (el.compareTo(band) <= 0) {
                flag = false;
                break;
            }
        }
        if (flag) {
            set.add(band);
        }
        return flag;
    }

    public static long removeLower(int id) {
        long count = set.stream().filter(b -> b.getId() < id).count();
        set.removeIf(band -> band.getId() < id);
        return count;
    }
}
