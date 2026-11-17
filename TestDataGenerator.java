package com.parabank.utils;

import java.util.concurrent.atomic.AtomicInteger;

/** Builds unique registration data so tests never collide on username. */
public final class TestDataGenerator {

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private TestDataGenerator() {}

    public static String uniqueUsername() {
        return "qa" + (System.currentTimeMillis() % 100_000_000L) + COUNTER.incrementAndGet();
    }

    /** Immutable bag of registration values. */
    public static final class User {
        public final String firstName = "Deepika";
        public final String lastName = "Tester";
        public final String street = "12 MG Road";
        public final String city = "Bengaluru";
        public final String state = "Karnataka";
        public final String zip = "560001";
        public final String phone = "9876543210";
        public final String ssn = "123-45-6789";
        public final String username;
        public final String password;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static User newUser() {
        return new User(uniqueUsername(), "Passw0rd!");
    }
}
