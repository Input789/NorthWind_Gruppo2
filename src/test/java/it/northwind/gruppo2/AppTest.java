package it.northwind.gruppo2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void greetingReturnsExpectedMessage() {
        assertEquals("Hello from NorthWind Gruppo 2", App.greeting());
    }
}
