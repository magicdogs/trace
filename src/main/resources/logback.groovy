import ch.qos.logback.classic.Level
import ch.qos.logback.classic.PatternLayout
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.ThresholdFilter
import com.ppm.trace.trace.DynamicThresholdUserFilter
import net.logstash.logback.composite.loggingevent.LoggingEventJsonProviders
import net.logstash.logback.composite.loggingevent.LoggingEventPatternJsonProvider
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder

import static ch.qos.logback.classic.Level.*

def DEFAULT_META_PATH = "META-INF/app.properties"
def PROJECT_ARTIFACT_ID = "artifactId"
def PROJECT_GROUP_NAME = "devGroupName"
def SYSTEM_OS_NAME = "os.name"

def ENV_LOG_PATH = "logPath"
def ENV_LOG_LAYOUT = "logLayout"
def ENV_LOG_LEVEL = "logLevel"
def ENV_FILE_NAME = "fileName" 

jmxConfigurator()

def DEFAULT_ROOT_LOGGER_LEVEL = INFO

def sysProperties = System.properties
def classLoader = getClass().getClassLoader()


def fileName = "server"
if(sysProperties.containsKey(ENV_FILE_NAME)){
    fileName = String.valueOf(sysProperties.get(ENV_FILE_NAME))
}

def logPath = ""
if(sysProperties.containsKey(ENV_LOG_PATH)){
    logPath = String.valueOf(sysProperties.get(ENV_LOG_PATH))
}else{
    def osName = String.valueOf(sysProperties.get(SYSTEM_OS_NAME)).toLowerCase()
    if (osName.contains("windows")){
        logPath = "d:/data/log-center"
    } else {
        logPath = "/data/log-center"
    }
}

turboFilter(com.ppm.trace.trace.DynamicThresholdUserFilter)

if(sysProperties.containsKey(ENV_LOG_LEVEL)){
    DEFAULT_ROOT_LOGGER_LEVEL = Level.toLevel(String.valueOf(sysProperties.get(ENV_LOG_LEVEL)))
}

def sourceUri = classLoader.getResource(DEFAULT_META_PATH)
def appProperties = new Properties()
appProperties.load(sourceUri.openStream())

def appName = appProperties.get(PROJECT_ARTIFACT_ID)
def devGroupName = appProperties.get(PROJECT_GROUP_NAME)

def patternTemplate
if(sysProperties.containsKey(ENV_LOG_LAYOUT)){
    patternTemplate = String.valueOf(sysProperties.get(ENV_LOG_LAYOUT))
}else{
    patternTemplate = "[%date{ISO8601}] [%level] %logger{80} %thread [%X{TRACE_ID}] ${devGroupName} ${appName} - %msg%n"
}

//println(fileName)
//println(logPath)
//println(DEFAULT_ROOT_LOGGER_LEVEL)
//println(patternTemplate)
//System.exit(0)

appender("fileAppender", RollingFileAppender) {
    file = "${logPath}/${appName}/${fileName}.log"
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${logPath}/${appName}/${fileName}.%d{yyyy-MM-dd}.log"
        maxHistory = 30
    }
    encoder(PatternLayoutEncoder) {
        pattern = patternTemplate
    }
}


appender("STDOUT",ConsoleAppender){
    encoder(PatternLayoutEncoder){
        pattern = patternTemplate
    }
}
//jsonEncoder.providers.addProvider(jsonProviders)
//jsonProviders.pattern = "{ \"class\": \"%logger{40}\", \"rest\": \"%message\" }"

/*appender("STDOUT", ConsoleAppender) {
    encoder(LoggingEventCompositeJsonEncoder) {
        providers(LoggingEventJsonProviders){
            pattern(LoggingEventPatternJsonProvider){
                pattern = "{ \"class\": \"%logger{40}\", \"msg\": \"%message\" ,\"host\":\"%X{req.remoteHost}\"}"
            }
        }
    }
}*/


/*logger("org.springframework", WARN)
logger("org.spring", WARN)
logger("org.hibernate", WARN)
logger("io.grpc.netty", OFF)
logger("org.eclipse.jetty", WARN)
logger("jndi", WARN)
logger("redis.clients", WARN)
logger("application", WARN)
logger("springfox.documentation", WARN)
logger("com.netflix", WARN)
logger("com.ppmoney.ppmon.celebi", INFO)
logger("org.reflections", WARN)
logger("io.grpc.internal.SerializingExecutor", OFF)
logger("org.apache", WARN)
logger("io.grpc.internal.ClientCallImpl", OFF)
logger("org.springframework.amqp.rabbit", ERROR)*/
root(DEFAULT_ROOT_LOGGER_LEVEL, ["STDOUT","fileAppender"])

