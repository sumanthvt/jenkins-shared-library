import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*
import com.lesfurets.jenkins.unit.BasePipelineTest
import org.example.utils.DeployUtils

class DeployAppTest extends BasePipelineTest {
    def deployAppScript
    def mockDeployUtils
    def logs

    @Before
    void setUp() throws Exception {
        super.setUp()

        // Load the pipeline step
        deployAppScript = loadScript("vars/deployApp.groovy")

        // Initialize log capture list
        logs = []

        // Capture echo messages into logs
        helper.registerAllowedMethod("echo", [String]) { msg -> logs << msg }

        // Mock error() to throw RuntimeException
        helper.registerAllowedMethod("error", [String]) { msg -> throw new RuntimeException(msg) }

        // Mock DeployUtils constructor
        mockDeployUtils = [:]
        helper.registerAllowedMethod("org.example.utils.DeployUtils", [Object, String]) { script, env ->
            return mockDeployUtils
        }
    }

    @Test
    void testSuccessfulDeployment() {
        // deploy() returns SUCCESS
        mockDeployUtils.deploy = { app, version -> "SUCCESS" }

        def result = deployAppScript.call("myApp", "1.0.0", "dev")

        assertEquals("SUCCESS", result)
        assertTrue(logs.contains("Starting deployment of myApp:1.0.0 to dev"))
        assertTrue(logs.contains("Deployment of myApp:1.0.0 to dev completed successfully"))
    }

    @Test
    void testDeploymentFailure() {
        // deploy() returns FAILED to simulate failure
        mockDeployUtils.deploy = { app, version -> "FAILED" }

        boolean exceptionThrown = false
        try {
            deployAppScript.call("myApp", "1.0.0", "dev")
        } catch (RuntimeException e) {
            exceptionThrown = true
            assertTrue(e.message.contains("Deployment failed"))
        }

        assertTrue("Expected RuntimeException for failed deployment", exceptionThrown)
        assertTrue(logs.contains("Starting deployment of myApp:1.0.0 to dev"))
        // The "completed successfully" message should NOT be in logs
        assertFalse(logs.any { it.contains("completed successfully") })
    }

    @Test
    void testMissingParams() {
        boolean exceptionThrown = false
        try {
            deployAppScript.call(null, "1.0.0", "dev")
        } catch (RuntimeException e) {
            exceptionThrown = true
            assertTrue(e.message.contains("Missing required parameters"))
        }

        assertTrue("Expected RuntimeException for missing parameters", exceptionThrown)
        // No success logs should be present
        assertFalse(logs.any { it.contains("completed successfully") })
    }
}