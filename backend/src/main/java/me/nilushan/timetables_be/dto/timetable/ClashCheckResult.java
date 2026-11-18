package me.nilushan.timetables_be.dto.timetable;

public record ClashCheckResult(
        boolean hasClash,
        String reason,
        String clashType
) {
    public static ClashCheckResult ok() {
        return new ClashCheckResult(false, null, null);
    }

    public static ClashCheckResult conflict(String clashType, String reason) {
        return new ClashCheckResult(true, reason, clashType);
    }
}
