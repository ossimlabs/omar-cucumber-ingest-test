package omar.cucumber.ogc.wms

class WMSGetCapabilities
{
    def wmsGetCapabilitiesResult

    WMSGetCapabilities() {}

    WMSGetCapabilities(wmsServer, version)
    {

        String wmsUrl = "${wmsServer}/getCapabilities?service=WMS&version=${version}&request=GetCapabilities"
        XmlSlurper parser = new XmlSlurper()

        if (version == "1.1.1")
        {
            parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        }

        wmsGetCapabilitiesResult = parser.parse(wmsUrl)
    }

    String getResult()
    {
        wmsGetCapabilitiesResult
    }
}
