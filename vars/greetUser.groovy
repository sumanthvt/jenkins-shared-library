import org.example.utils.GreetUtils

def call(String name) {
    echo "Preparing to greet ${name}"
    def greeting = GreetUtils.buildGreeting(name)
    echo greeting
    return greeting
}