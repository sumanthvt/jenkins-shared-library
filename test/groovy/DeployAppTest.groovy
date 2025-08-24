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

        // Mock echo step
        helper.registerAllowedMethod("echo", [String]) { msg -> println msg }

        // Mock error step to throw RuntimeException
        helper.registerAllowedMethod("error", [String]) { msg -> throw new RuntimeException(msg) }

        // --- CHANGE: Mock DeployUtils as a real Groovy object with deploy() method ---
        mockDeployUtils = new Object() {
            def deploy(String app, String version) {
                return "FAILED"  // default, will override per test
            }
        }

        helper.registerAllowedMethod("org.example.utils.DeployUtils", [Object, String]) { script, env ->
            return mockDeployUtils
        }
    }

    @Test
    void testSuccessfulDeployment() {
        // Override deploy() to return SUCCESS for this test
        mockDeployUtils.metaClass.deploy = { String app, String version -> "SUCCESS" }

        def result = deployAppScript.call("myApp", "1.0.0", "dev")
        assertEquals("SUCCESS", result)
    }

    @Test
    void testDeploymentFailure() {
        // Override deploy() to return FAILED to trigger error()
        mockDeployUtils.metaClass.deploy = { String app, String version -> "FAILED" }

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