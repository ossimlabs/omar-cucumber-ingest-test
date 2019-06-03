package omar.cucumber.config

import groovy.text.*
import groovy.json.JsonSlurper

class CucumberConfig
{
    static def config

    static def getConfig()
    {
        if (!config)
        {
            init()
        }

        config
    }

    private static void init()
    {
        def resourceFile

        if (System.env.CUCUMBER_CONFIG_LOCATION)
        {
            File testForLocal = new File(System.env.CUCUMBER_CONFIG_LOCATION)
            if (!testForLocal.exists())
            {
                resourceFile = new URL(System.env.CUCUMBER_CONFIG_LOCATION)
            }
            else
            {
                resourceFile = testForLocal.toURL()
            }
        }
        else
        {
            resourceFile = new File("cucumber-config.groovy");
            if (resourceFile.exists())
            {
                resourceFile = resourceFile.toURL()
            }
            else
            {
                resourceFile = null
            }
            if (!resourceFile)
            {
                resourceFile = new File("src/main/resources/cucumber-config.groovy").toURI().toURL()
            }
        }

        if (resourceFile)
        {
            def slurper = new ConfigSlurper()
            config = slurper.parse(resourceFile)
        }
    }
}
