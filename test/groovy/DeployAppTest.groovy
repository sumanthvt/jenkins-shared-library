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

        // Mock echo to print
        helper.registerAllowedMethod("echo", [String]) { String msg -> println msg }

        // Mock DeployUtils
        mockDeployUtils = GroovyMock(DeployUtils, global: true)
        DeployUtils.&new >> { script, env -> return mockDeployUtils }
    }

    @Test
    void testSuccessfulDeployment() {
        mockDeployUtils.deploy(_, _) >> "SUCCESS"

        def result = deployAppScript.call("myApp", "1.0.0", "dev")
        assertEquals("SUCCESS", result)
    }

    @Test
    void testDeploymentFailure() {
        mockDeployUtils.deploy(_, _) >> "FAILED"

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