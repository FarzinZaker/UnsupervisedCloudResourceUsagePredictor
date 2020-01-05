package f3

import grails.converters.JSON

class APIController {

    def DLModelService

    def predict() {
        def data = DLModelService.safePredict()
        if (params.resource)
            data = data.findAll { it.resource == params.resource?.trim() }
        if (params.metric)
            data = data.findAll { it.metric == params.metric?.trim() }
        if (!params.type)
            params.type = 'next'
        if (params.type != 'all')
            data = data.collect { [resource: it.resource, metric: it.metric, value: it."${params.type}"] }
        render(data as JSON)
    }
}
