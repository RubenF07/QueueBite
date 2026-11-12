package model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OrderItemTest {

    @Test
    public void testValidItem() {
        OrderItem item = new OrderItem("Burger", 2);
        assertEquals("Burger", item.getName());
        assertEquals(2, item.getQuantity());
    }

    @Test
    public void testNameIsTrimmed() {
        OrderItem item = new OrderItem("  Fries  ", 1);
        assertEquals("Fries", item.getName());
    }

    @Test
    public void testNameWithDigitsAndLettersAllowed() {
        OrderItem item = new OrderItem("7Up", 1);
        assertEquals("7Up", item.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBlankNameRejected() {
        new OrderItem("   ", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyNameRejected() {
        new OrderItem("", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullNameRejected() {
        new OrderItem(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDigitOnlyNameRejected() {
        new OrderItem("123", 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZeroQuantityRejected() {
        new OrderItem("Burger", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeQuantityRejected() {
        new OrderItem("Burger", -3);
    }
}
