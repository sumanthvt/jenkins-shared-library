import org.example.utils.DeployUtils

def call(String appName, String version, String targetEnv) {
    if (!appName || !version || !targetEnv) {
        error "Missing required parameters: appName, version, targetEnv"
    }

    def deployUtils = new DeployUtils(this, targetEnv)
    echo "Starting deployment of ${appName}:${version} to ${targetEnv}"

    def result = deployUtils.deploy(appName, version)
    if (result != "SUCCESS") {
        error "Deployment failed for ${appName}:${version} on ${targetEnv}"
    }

    echo "Deployment of ${appName}:${version} to ${targetEnv} completed successfully"
    return result
}