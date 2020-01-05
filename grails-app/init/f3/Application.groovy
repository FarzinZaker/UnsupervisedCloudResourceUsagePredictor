package f3

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import grails.converters.JSON
import groovy.transform.CompileStatic
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.Environment
import org.springframework.core.env.MapPropertySource

//@CompileStatic
class Application extends GrailsAutoConfiguration implements EnvironmentAware {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @Override
    void setEnvironment(Environment env) {
        def environment = env as ConfigurableEnvironment
        def path = environment.getProperty('configuration').toString()
        def file = new File(path)

        if (file.exists()) {
            ConfigObject config = new ConfigSlurper().parse(file.text)
            def propertySource = new MapPropertySource(grails.util.Environment.getCurrent().name, config.toSorted())
            environment.propertySources.addFirst(propertySource)
        }
    }
}