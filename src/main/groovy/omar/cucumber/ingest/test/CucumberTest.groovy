package omar.cucumber.ingest.test

import omar.cucumber.ingest.test.SecurityManagerCheck

class CucumberTest {
    public void startTest() {
        String main_path = 'omar/cucumber/step_definitions'
        String resource_path = 'src/main/resources'
        String ingestTags = "@local_staging_service, @staging_service, @direct_s3_staging_service"
        String jsonFile = "ingest.json"

        if (System.getProperty("mainPath"))
        {
            main_path = System.getProperty("mainPath")
        }

        if (System.getProperty("resourcePath"))
        {
            resource_path = System.getProperty("resourcePath")
        }

        if (System.getenv("multi") == "true")
        {
            ingestTags = "@staging_multi_ingest_service"
            jsonFile = "ingestMultipleImages.json"
        }

        String[] arguments = [
                "--tags",
                "${ingestTags}",
                "--tags",
                "~@C2S",
                '--plugin', "json:src/main/groovy/omar/webapp/reports/json/${jsonFile}",
                '--plugin', "html:src/main/groovy/omar/webapp/reports/html",
                '--plugin', "pretty",
                '--glue', main_path,
                resource_path]

        SecurityManagerCheck secManager = new SecurityManagerCheck()
        System.setSecurityManager(secManager)
        
        try {
            cucumber.api.cli.Main.main(arguments)
        } catch (SecurityException e) {
            //put what you want to happen when exit is called
        }
    }
}