package me.nilushan.timetables_be;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class TimetablesBeApplicationTests {

    @Test
    void printHash() {
        String hash = new BCryptPasswordEncoder().encode("admin123");
        System.out.println("BCRYPT_ADMIN123=" + hash);
    }

}
