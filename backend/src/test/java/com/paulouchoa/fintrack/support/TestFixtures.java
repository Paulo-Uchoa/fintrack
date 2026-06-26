package com.paulouchoa.fintrack.support;

import com.paulouchoa.fintrack.user.Role;
import com.paulouchoa.fintrack.user.User;

public final class TestFixtures {

    private TestFixtures() {
    }

    public static User user() {
        return new User("Test User", "test@example.com", "hashed-password", Role.USER);
    }
}
