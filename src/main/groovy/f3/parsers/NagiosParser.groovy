package f3.parsers

import f3.IMetricParser
import f3.Metric
import org.grails.web.json.JSONObject

import java.text.SimpleDateFormat

class NagiosParser implements IMetricParser {

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+'0000")

    @Override
    Metric parse(JSONObject data) {
        def record = data._source as JSONObject
        new Metric(
                name: record.service,
                instance: record.host,
                time: dateFormat.parse(record.agg_end_time),
                value: record."avg_${record.service}"
        )
    }

    @Override
    Map getQuery(Date date) {
        [
                size : 10000,
                sort : [[agg_end_time: [order: 'asc', unmapped_type: 'boolean']]],
                query: [bool: [must: [[match_all: [:]], [range: [agg_end_time: [gt: dateFormat.format(date)]]]]]]
        ]
    }
}
