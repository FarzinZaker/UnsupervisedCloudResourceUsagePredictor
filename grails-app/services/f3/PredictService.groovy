package f3

import grails.gorm.transactions.Transactional

@Transactional
class PredictService {

    def DLModelService

    def predic(){
        DLModelService.safePredict()
    }

    List<Map> getLatest() {
        def list = Metric.createCriteria().list {
            eq('consumed', false)
            eq('consumed', false)
            projections {
                groupProperty 'instance'
                groupProperty 'name'
                avg('value')
                order('time', 'desc')
            }
        }

        list = list.collect {
            def order = MetricOrder.findByInstanceAndName(it[0], it[1])
            if (!order) {
                order = new MetricOrder(instance: it[0], name: it[1])
                order.save(flush: true)
            }
            [order.id, order.instance, order.name, it[2]]
        }.sort { it[0] }

        def input = []
        list.each {
            input << [
                    index   : it[0],
                    instance: it[1],
                    metric  : it[2],
                    value   : it[3]
            ]
        }

        input
    }
}
