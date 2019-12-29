package f3.parsers

import f3.IMetricParser
import f3.Metric
import grails.converters.JSON
import org.grails.web.json.JSONObject

import java.text.SimpleDateFormat

class CloudWatchParser implements IMetricParser {

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    @Override
    Metric parse(JSONObject data) {
        def record = data._source
        new Metric(
                name: record.metric_name,
                instance: record.resource_id,
                time: dateFormat.parse(record.timestamp),
                value: record.average
        )
    }

    @Override
    Map getQuery(Date date) {
        [
                size : 10000,
                sort : [[timestamp: [order: 'asc', unmapped_type: 'boolean']]],
                query: [bool: [must: [[match_all: [:]], [range: [timestamp: [gt: dateFormat.format(date)]]]]]]
        ]
    }
}
