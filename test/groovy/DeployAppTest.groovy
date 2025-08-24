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

        // Load shared library step
        deployAppScript = loadScript("vars/deployApp.groovy")

        // Mock echo
        helper.registerAllowedMethod("echo", [String]) { msg -> println msg }

        // Mock DeployUtils constructor
        mockDeployUtils = [
            deploy: { app, version -> return "MOCKED" }
        ]

        helper.registerAllowedMethod("org.example.utils.DeployUtils", [Object, String]) { script, env ->
            return mockDeployUtils
        }
    }

    @Test
    void testSuccessfulDeployment() {
        mockDeployUtils.deploy = { app, version -> "SUCCESS" }
        def result = deployAppScript.call("myApp", "1.0.0", "dev")
        assertEquals("SUCCESS", result)
    }

    @Test
    void testDeploymentFailure() {
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