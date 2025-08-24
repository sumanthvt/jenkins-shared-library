import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*
import com.lesfurets.jenkins.unit.BasePipelineTest
import org.example.utils.DeployUtils

class DeployAppTest extends BasePipelineTest {
    def deployAppScript
    def mockDeployUtils

    @Before
    void setUp() throws Exception {
        super.setUp()

        // Load the shared library step under test
        deployAppScript = loadScript("vars/deployApp.groovy")

        // Mock Jenkins pipeline steps
        helper.registerAllowedMethod("echo", [String]) { msg -> println msg }
        helper.registerAllowedMethod("error", [String]) { msg -> throw new RuntimeException(msg) }

        // Mock DeployUtils instance methods
        mockDeployUtils = [
            deploy: { app, version -> "MOCKED" }  // default mock
        ]

        // Mock DeployUtils constructor
        helper.registerAllowedMethod("org.example.utils.DeployUtils", [Object, String]) { script, env ->
            return mockDeployUtils
        }
    }

    @Test
    void testSuccessfulDeployment() {
        // Override deploy method to return SUCCESS
        mockDeployUtils.deploy = { app, version -> "SUCCESS" }

        def result = deployAppScript.call("myApp", "1.0.0", "dev")
        assertEquals("SUCCESS", result)
    }

    @Test
    void testDeploymentFailure() {
        // Override deploy method to simulate failure
        mockDeployUtils.deploy = { app, version -> "FAILED" }

        try {
            deployAppScript.call("myApp", "1.0.0", "dev")
            fail("Expected exception not thrown")
        } catch (Exception e) {
            assertTrue(e.message.contains("Deployment failed"))
        }
    }

    @Test
    void testMissingParams() {
        try {
            deployAppScript.call(null, "1.0.0", "dev")
            fail("Expected exception not thrown")
        } catch (Exception e) {
            assertTrue(e.message.contains("Missing required parameters"))
        }
    }
}