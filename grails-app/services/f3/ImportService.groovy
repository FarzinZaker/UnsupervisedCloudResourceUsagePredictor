package f3

import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONObject

@Transactional
class ImportService {

    def queryService

    Date load(String index, Date date) {

        def lastDate = date
        def parser = ParserFactory.getParser(index)
        def list = queryService.readData(index, parser.getQuery(date))
        list.each { data ->
            def metric
            def newMetric = parser.parse(data as JSONObject)
            newMetric.index = index
            if (newMetric) {
                metric = newMetric.save()
                if (metric && metric.time > lastDate)
                    lastDate = metric.time
            }
        }
        lastDate
    }
}
