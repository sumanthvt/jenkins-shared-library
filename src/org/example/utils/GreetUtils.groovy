package org.example.utils

class GreetUtils {

    static String buildGreeting(String name) {
        if (!name?.trim()) {
            throw new IllegalArgumentException("Name must not be empty")
        }
        return "Hello, ${name.capitalize()}!"
    }
}