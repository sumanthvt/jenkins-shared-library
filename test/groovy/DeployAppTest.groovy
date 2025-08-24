import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*
import com.lesfurets.jenkins.unit.BasePipelineTest
import org.example.utils.DeployUtils

class DeployAppTest extends BasePipelineTest {

    def deployAppScript

    @Before
    void setUp() throws Exception {
        super.setUp()

        // Load the shared library step under test
        deployAppScript = loadScript("vars/deployApp.groovy")

        // Mock echo to print to console
        helper.registerAllowedMethod("echo", [String]) { msg -> println msg }

        // Mock error step to throw RuntimeException
        helper.registerAllowedMethod("error", [String]) { msg -> throw new RuntimeException(msg) }
    }

    @Test
    void testSuccessfulDeployment() {
        // Inject a mock DeployUtils
        def mockDeployUtils = [deploy: { app, version -> "SUCCESS" }]

        def result = deployAppScript.call("myApp", "1.0.0", "dev", mockDeployUtils)
        assertEquals("SUCCESS", result)
    }

    @Test
    void testDeploymentFailure() {
        // Inject a mock DeployUtils that returns FAILED
        def mockDeployUtils = [deploy: { app, version -> "FAILED" }]

        boolean exceptionThrown = false
        try {
            deployAppScript.call("myApp", "1.0.0", "dev", mockDeployUtils)
        } catch (RuntimeException e) {
            exceptionThrown = e.message.contains("Deployment failed")
        }

        assertTrue("Expected RuntimeException for failed deployment", exceptionThrown)
    }

    @Test
    void testMissingParams() {
        boolean exceptionThrown = false
        try {
            deployAppScript.call(null, "1.0.0", "dev")
        } catch (RuntimeException e) {
            exceptionThrown = e.message.contains("Missing required parameters")
            exceptionThrown = true
        }
        assertTrue("Expected RuntimeException for missing parameters", exceptionThrown)
    }
}