import org.example.utils.DeployUtils

def call(String appName, String version, String targetEnv, deployUtils = null) {
    if (!appName || !version || !targetEnv) {
        error "Missing required parameters: appName, version, targetEnv"
    }

    // Use injected deployUtils or create a new one
    def utils = deployUtils ?: new DeployUtils(this, targetEnv)
    echo "Starting deployment of ${appName}:${version} to ${targetEnv}"

    def result = utils.deploy(appName, version)
    if (result != "SUCCESS") {
        error "Deployment failed for ${appName}:${version} on ${targetEnv}"
    }

    echo "Deployment of ${appName}:${version} to ${targetEnv} completed successfully"
    return result
}