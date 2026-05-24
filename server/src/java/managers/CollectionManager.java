package managers;


import structs.MusicBand;
import structs.Studio;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс для управления коллекцией на стороне сервера.
 */
public class CollectionManager {
    private static int nextId = 0;
    private static ZonedDateTime dateInitialization = null;
    private static final Set<MusicBand> set = Collections.synchronizedSet(new HashSet<>());

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


    public static String info() {
        return "Тип: %s\n" + set.getClass() + "\n" +
                "Дата инициализации: %s\n" + CollectionManager.getDateInitialization().format(DateTimeFormatter.ISO_DATE_TIME) + "\n" +
                "Количество элементов в коллекции: %d\n" + set.size() + "\n";
    }

    /**
     * Проверяем наличие объекта с таким id в коллекции
     *
     * @param id который нужно проверить
     * @return true или false (есть или нет такого id)
     */

    public static boolean checkId(int id) {
        synchronized (set) {
            return set.stream().anyMatch(band -> band.getId() == id);
        }
    }

    /**
     * Обновление объекта с заданным id
     *
     * @param band новый объект
     */

    public static void update(MusicBand band, int id) {
        synchronized (set) {
            removeByID(id);
            set.add(band);
        }
    }

    /**
     * Удалить из коллекции один элемент, значение поля numberOfParticipants которого эквивалентно заданному
     *
     * @param number параметр для сравнения
     * @return id удаленного объекта или -1 если таких нет
     */

    public static int removeAnyByNumberOfParticipants(Integer number) {
        synchronized (set) {
            MusicBand band = set.stream().filter(b -> b.getNumberOfParticipants().equals(number)).findFirst().orElse(null);
            if (band != null) {
                set.remove(band);
                return band.getId();
            } else {
                return -1;
            }
        }
    }

    /**
     * Вывести количество элементов, значение поля studio которых больше заданного
     *
     * @param studio параметр для сравнения
     * @return количество подходящих элементов
     */

    public static long countGreaterThanStudio(Studio studio) {
        synchronized (set) {
            Studio.ComparatorStudio studioComparator = new Studio.ComparatorStudio();
            return set.stream()
                    .filter(band -> band.getStudio() != null)
                    .filter(band -> studioComparator.compare(band.getStudio(), studio) > 0)
                    .count();
        }
    }

    /**
     * Вывести в стандартный поток вывода все элементы коллекции в строковом представлении
     */
    public static void showAll() {
        synchronized (set) {
            if (set.isEmpty()) {
                System.out.println("Коллекция пуста");
            } else {
                for (MusicBand band : set) {
                    System.out.println(band);
                }
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
        synchronized (set) {
            boolean flag = set.stream().noneMatch(el -> el.compareTo(band) <= 0);
            if (flag) {
                set.add(band);
            }
            return flag;
        }
    }

    public static long removeLower(int id) {
        synchronized (set) {
            long count = set.stream().filter(b -> b.getId() < id).count();
            set.removeIf(band -> band.getId() < id);
            return count;
        }
    }

    public static MusicBand copyWithGeneratedId(MusicBand source, int id) {
        MusicBand.MusicBandBuilder builder = MusicBand.builder()
                .id(id)
                .coordinates(source.getCoordinates())
                .creationDate(source.getCreationDate())
                .genre(source.getGenre())
                .studio(source.getStudio());
        try {
            builder.name(source.getName());
            builder.numberOfParticipants(source.getNumberOfParticipants());
            return builder.build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Не удалось построить объект MusicBand: " + e.getMessage(), e);
        }
    }

    public static String showAllElements() {
        synchronized (set) {
            if (set.isEmpty()) {
                return "Коллекция пуста";
            }
            return set.stream()
                    .map(MusicBand::toString)
                    .collect(Collectors.joining("\n"));
        }
    }

    public static String printDescendingText() {
        synchronized (set) {
            if (set.isEmpty()) {
                return "Коллекция пуста";
            }
            return set.stream()
                    .sorted(Comparator.reverseOrder())
                    .map(MusicBand::toString)
                    .collect(Collectors.joining("\n"));
        }
    }
}
