package me.nilushan.timetables_be.security;

public final class TenantContext {

    private static final ThreadLocal<Long> CURRENT_SCHOOL_ID = new ThreadLocal<>();

    private TenantContext() {}

    public static void setSchoolId(Long schoolId) {
        CURRENT_SCHOOL_ID.set(schoolId);
    }

    public static Long getSchoolId() {
        return CURRENT_SCHOOL_ID.get();
    }

    public static void clear() {
        CURRENT_SCHOOL_ID.remove();
    }
}
