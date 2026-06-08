package managers;

import exceptions.InvalidValueFieldException;
import structs.MusicBand;
import structs.Studio;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager {
    private static ZonedDateTime dateInitialization = null;
    private static final Set<MusicBand> set = Collections.synchronizedSet(new HashSet<>());

    public static void init() {
        setDateInitialization(ZonedDateTime.now());
        List<MusicBand> fromDb = DBManager.getInstance().loadAllBands();
        set.addAll(fromDb);
    }

    public static void setDateInitialization(ZonedDateTime dateInitialization) {
        CollectionManager.dateInitialization = Objects.requireNonNullElseGet(dateInitialization, ZonedDateTime::now);
    }

    public static ZonedDateTime getDateInitialization() {
        return dateInitialization;
    }

    public static Set<MusicBand> getSet() {
        return set;
    }


    public static boolean updateWithDb(int id, MusicBand newBand, int ownerId) {
        synchronized (set) {
            MusicBand oldBand = set.stream()
                    .filter(b -> b.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (oldBand == null || oldBand.getOwnerId() != ownerId) {
                return false;
            }

            boolean dbUpdated = DBManager.getInstance().updateBandInDb(id, newBand, ownerId);

            if (dbUpdated) {
                set.remove(oldBand);
                MusicBand updatedBand = null;
                try {
                    updatedBand = MusicBand.builder()
                            .id(id)
                            .name(newBand.getName())
                            .coordinates(newBand.getCoordinates())
                            .creationDate(oldBand.getCreationDate())
                            .numberOfParticipants(newBand.getNumberOfParticipants())
                            .genre(newBand.getGenre())
                            .studio(newBand.getStudio())
                            .ownerId(ownerId)
                            .build();
                } catch (InvalidValueFieldException ignored) {
                }

                set.add(updatedBand);
                return true;
            }
        }
        return false;
    }

    public static long removeLowerWithDb(int maxId, int ownerId) {
        synchronized (set) {
            List<MusicBand> toRemove = set.stream()
                    .filter(b -> b.getId() < maxId && b.getOwnerId() == ownerId)
                    .toList();

            long count = 0;
            for (MusicBand band : toRemove) {
                if (DBManager.getInstance().deleteBand(band.getId(), ownerId)) {
                    set.remove(band);
                    count++;
                }
            }
            return count;
        }
    }

    public static int removeAnyByNumberOfParticipantsWithDb(int participantsCount, int ownerId) {
        synchronized (set) {
            MusicBand target = set.stream()
                    .filter(b -> b.getNumberOfParticipants() == participantsCount && b.getOwnerId() == ownerId)
                    .findFirst()
                    .orElse(null);

            if (target == null) {
                return -1;
            }

            if (DBManager.getInstance().deleteBand(target.getId(), ownerId)) {
                set.remove(target);
                return target.getId();
            }
        }
        return -1;
    }

    public static boolean removeByIDWithDb(int id, int ownerId) {
        synchronized (set) {
            MusicBand target = set.stream().filter(b -> b.getId() == id).findFirst().orElse(null);
            if (target == null || target.getOwnerId() != ownerId) {
                return false;
            }

            if (DBManager.getInstance().deleteBand(id, ownerId)) {
                set.remove(target);
                return true;
            }
        }
        return false;
    }

    public static int clearWithDb(int ownerId) {
        synchronized (set) {
            List<MusicBand> toRemove = set.stream()
                    .filter(b -> b.getOwnerId() == ownerId)
                    .toList();

            int count = 0;
            for (MusicBand b : toRemove) {
                if (DBManager.getInstance().deleteBand(b.getId(), ownerId)) {
                    set.remove(b);
                    count++;
                }
            }
            return count;
        }
    }

    public static boolean addIfMinWithDb(MusicBand incoming, int ownerId) {
        synchronized (set) {
            boolean isMin = set.isEmpty() || set.stream().min(java.util.Comparator.naturalOrder())
                    .map(minBand -> incoming.compareTo(minBand) < 0)
                    .orElse(true);

            if (!isMin) {
                return false;
            }

            int generatedId = DBManager.getInstance().insertBand(incoming, ownerId).getId();
            if (generatedId > 0) {
                MusicBand finalBand = null;
                try {
                    finalBand = MusicBand.builder()
                            .id(generatedId)
                            .name(incoming.getName())
                            .coordinates(incoming.getCoordinates())
                            .creationDate(new Date())
                            .numberOfParticipants(incoming.getNumberOfParticipants())
                            .genre(incoming.getGenre())
                            .studio(incoming.getStudio())
                            .ownerId(ownerId)
                            .build();
                } catch (InvalidValueFieldException ignored) {
                }

                set.add(finalBand);
                return true;
            }
        }
        return false;
    }

    public static long countGreaterThanStudio(String studioAddress) {
        synchronized (set) {
            if (studioAddress == null) return 0;
            return set.stream()
                    .filter(b -> b.getStudio() != null && b.getStudio().getAddress() != null)
                    .filter(b -> b.getStudio().getAddress().compareTo(studioAddress) > 0)
                    .count();
        }
    }

    public static int addWithDb(MusicBand incoming, int ownerId) throws InvalidValueFieldException {
        synchronized (set) {
            int generatedId = DBManager.getInstance().insertBand(incoming, ownerId).getId();

            if (generatedId > 0) {
                MusicBand finalBand = null;
                    finalBand = MusicBand.builder()
                            .id(generatedId)
                            .name(incoming.getName())
                            .coordinates(incoming.getCoordinates())
                            .creationDate(new Date())
                            .numberOfParticipants(incoming.getNumberOfParticipants())
                            .genre(incoming.getGenre())
                            .studio(incoming.getStudio())
                            .ownerId(ownerId)
                            .build();

                set.add(finalBand);
                return generatedId;
            }
        }
        return -1;
    }

    public static String info() {
        return "Тип коллекции: " + set.getClass().getName() + "\n" +
                "Дата инициализации: " + (dateInitialization != null ? dateInitialization.format(DateTimeFormatter.ISO_DATE_TIME) : "нет") + "\n" +
                "Количество элементов в коллекции: " + set.size() + "\n";
    }

    public static String showAllElements() {
        synchronized (set) {
            if (set.isEmpty()) return "Коллекция пуста";
            return set.stream().map(MusicBand::toString).collect(Collectors.joining("\n"));
        }
    }

    public static String printDescendingText() {
        synchronized (set) {
            if (set.isEmpty()) return "Коллекция пуста";
            return set.stream()
                    .sorted(Comparator.reverseOrder())
                    .map(MusicBand::toString)
                    .collect(Collectors.joining("\n"));
        }
    }
}