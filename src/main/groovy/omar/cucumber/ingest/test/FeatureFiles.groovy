package omar.cucumber.ingest.test

import omar.cucumber.config.CucumberConfig
import groovy.text.*

class FeatureFiles {
    public void create() {
        def config = CucumberConfig.config
        config.featureTemplateFiles.templates.each{ files ->
            List list = []
            File templateFile = new File("${files.getValue().template_file}")
            File featureFile =  new File("${files.getValue().feature_file}")
            
            config.image_files.each{ imagesList -> 
                if(files.getValue().data == imagesList.getKey().toString())
                {
                    imagesList.getValue().images.each{ imageData ->
                        imageData.each { imageInfo ->
                            list.push(imageInfo.getValue().image_id)
                        }
                    }
                
                }             
            }

            HashMap imageList = ["files":list]
            SimpleTemplateEngine engine = new SimpleTemplateEngine()
            Template template = engine.createTemplate(templateFile)
            Writable writable = template.make(imageList)
            featureFile.write(writable.toString())
        }
    }
}