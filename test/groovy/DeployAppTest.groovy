import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*
import com.lesfurets.jenkins.unit.BasePipelineTest
import org.example.utils.DeployUtils

class DeployAppTest extends BasePipelineTest {

    def deployAppScript
    DeployUtils mockDeployUtils  // <- Use real DeployUtils instance

    @Before
    void setUp() throws Exception {
        super.setUp()

        // Load the shared library step under test
        deployAppScript = loadScript("vars/deployApp.groovy")

        // Mock Jenkins steps
        helper.registerAllowedMethod("echo", [String]) { msg -> println msg }
        helper.registerAllowedMethod("error", [String]) { msg -> throw new RuntimeException(msg) }

        // --- KEY CHANGE ---
        // Create a real DeployUtils instance (env can be dummy for now)
        mockDeployUtils = new DeployUtils(null, "dev")

        // Register DeployUtils constructor to always return the mock
        helper.registerAllowedMethod("org.example.utils.DeployUtils", [Object, String]) { script, env ->
            // Update the env inside mock if needed
            mockDeployUtils.env = env
            return mockDeployUtils
        }
    }

    @Test
    void testSuccessfulDeployment() {
        // --- KEY CHANGE ---
        // Override deploy() to simulate SUCCESS
        mockDeployUtils.metaClass.deploy = { String app, String version -> "SUCCESS" }

        def result = deployAppScript.call("myApp", "1.0.0", "dev")
        assertEquals("SUCCESS", result)
    }

    @Test
    void testDeploymentFailure() {
        // --- KEY CHANGE ---
        // Override deploy() to simulate FAILURE
        mockDeployUtils.metaClass.deploy = { String app, String version -> "FAILED" }

        try {
            deployAppScript.call("myApp", "1.0.0", "dev")
            fail("Expected exception not thrown")  // will be skipped if error() is triggered
        } catch (RuntimeException e) {
            // Check that the script triggered the error correctly
            assertTrue(e.message.contains("Deployment failed"))
        }
    }

    @Test
    void testMissingParams() {
        try {
            deployAppScript.call(null, "1.0.0", "dev")
            fail("Expected exception not thrown")
        } catch (RuntimeException e) {
            assertTrue(e.message.contains("Missing required parameters"))
        }
    }
}