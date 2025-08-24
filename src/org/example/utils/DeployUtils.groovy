package org.example.utils

class DeployUtils implements Serializable {
    def script
    String env

    DeployUtils(script, String env) {
        this.script = script
        this.env = env
    }

    def deploy(String appName, String version) {
        script.echo "Deploying ${appName}:${version} to ${env}"
        // Here you would have actual deployment logic, for now return SUCCESS
        return "SUCCESS"
    }
}