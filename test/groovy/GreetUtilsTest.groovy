import org.junit.Test
import static org.junit.Assert.*
import org.example.utils.GreetUtils

class GreetUtilsTest {

    @Test
    void testGreeting() {
        assertEquals("Hello, Sumanth!", GreetUtils.buildGreeting("sumanth"))
    }

    @Test(expected = IllegalArgumentException)
    void testEmptyNameFails() {
        GreetUtils.buildGreeting("")
    }
}