package f3

import org.grails.web.json.JSONObject

interface IMetricParser {
    Metric parse(JSONObject data)
    Map getQuery(Date date)
}