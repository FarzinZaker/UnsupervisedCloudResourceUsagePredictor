package f3

import grails.converters.JSON
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONElement

@Transactional
class QueryService {

    GrailsApplication grailsApplication

    private String getServerUrl() {
        grailsApplication.config["elasticsearch.url"]?.toString()
    }

    def readData(String index, Map data) {
        JSON.parse(post("$serverUrl/$index-*/_search", data)).hits.hits as JSONArray
    }

    List listIndexCategories() {
        listIndexes().collect { it.split('-').find() }.unique()
    }

    List listIndexes() {
        get("$serverUrl/_cat/indices").split('\n').collect {
            it.split(' ')?.findAll { it }[2]
        }.findAll { !it.startsWith('.') }
    }

    private JSONElement execute(String url, Map data = null) {
        def result = data ? post(url, data) : get(url)
        JSON.parse(result)
    }

    private static String get(String url) {
        // GET
        def get = new URL(url).openConnection()
        def getRC = get.getResponseCode()
        if (getRC.equals(200))
            return get.getInputStream().getText()
        null
    }

    private String post(String url, Map data) {
        def post = new URL(url).openConnection()
        post.setRequestMethod("POST")
        post.setDoOutput(true)
        post.setRequestProperty("Content-Type", "application/json")
        post.getOutputStream().write((data as JSON).toString().getBytes("UTF-8"))
        def postRC = post.getResponseCode()
        if (postRC.equals(200))
            return post.getInputStream().getText()

        null
    }
}
