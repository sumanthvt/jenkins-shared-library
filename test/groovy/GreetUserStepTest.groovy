import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*

class GreetUserStepTest extends BasePipelineTest {

    @Before
    void setUp() {
        super.setUp()
        helper.registerAllowedMethod("echo", [String.class], { s -> println "ECHO: $s" })
    }

    @Test
    void testGreetUserStep() {
        def script = loadScript("vars/greetUser.groovy")
        def result = script.call("sumanth")

        printCallStack()

        assertEquals("Hello, Sumanth!", result)

        assertTrue helper.callStack.find { call ->
            call.methodName == "echo" && call.args[0].contains("Hello, Sumanth!")
        } != null

    }
}