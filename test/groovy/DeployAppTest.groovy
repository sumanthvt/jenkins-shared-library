import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*
import com.lesfurets.jenkins.unit.BasePipelineTest

class DeployAppTest extends BasePipelineTest {
    def deployAppScript

    @Before
    void setUp() throws Exception {
        super.setUp()
        deployAppScript = loadScript("vars/deployApp.groovy")

        // Mock Jenkins steps
        helper.registerAllowedMethod("echo", [String]) { msg -> println msg }
        helper.registerAllowedMethod("error", [String]) { msg -> throw new RuntimeException(msg) }
    }

    @Test
    void testSuccessfulDeployment() {
        def mockUtils = [deploy: { app, version -> "SUCCESS" }]
        def result = deployAppScript.call("myApp", "1.0.0", "dev", mockUtils)
        assertEquals("SUCCESS", result)
    }

    @Test
    void testDeploymentFailure() {
        def mockUtils = [deploy: { app, version -> "FAILED" }]
        boolean exceptionThrown = false
        try {
            deployAppScript.call("myApp", "1.0.0", "dev", mockUtils)
        } catch (RuntimeException e) {
            exceptionThrown = true
            assertTrue(e.message.contains("Deployment failed"))
        }
        assertTrue("Expected RuntimeException for failed deployment", exceptionThrown)
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
    }
}