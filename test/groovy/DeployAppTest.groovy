// File: src/test/groovy/DeployAppTest.groovy
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

        // Mock echo to print messages
        helper.registerAllowedMethod("echo", [String]) { msg -> println msg }

        // Mock error to throw RuntimeException
        helper.registerAllowedMethod("error", [String]) { String msg -> throw new RuntimeException(msg) }

        // Mock DeployUtils instance methods
        mockDeployUtils = [
            deploy: { app, version -> "MOCKED" }
        ]

        // Mock DeployUtils constructor
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
    }

    @Test
    void testDeploymentFailure() {
        // deploy() returns FAILED to simulate deployment failure
        mockDeployUtils.deploy = { app, version -> "FAILED" }

        try {
            deployAppScript.call("myApp", "1.0.0", "dev")
            fail("Expected exception not thrown")
        } catch (RuntimeException e) {
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