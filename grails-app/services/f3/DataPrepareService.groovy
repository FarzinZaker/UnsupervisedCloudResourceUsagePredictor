package f3

import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory

@Transactional
class DataPrepareService {

    def DLModelService

    Date prepareTrainingData(Date date) {

        def startDate = date
        def endDate = startDate
        use(TimeCategory) {
            endDate = startDate + 15.minutes
        }

        if (endDate > new Date()) {
            Thread.sleep(60 * 1000l)
            return
        }

//        def nextDate = endDate
//        use(TimeCategory) {
//            nextDate = endDate + 15.minutes
//        }

        def metrics = Metric.executeQuery("SELECT m2 FROM Metric m2 WHERE id IN (SELECT max(m1.id) FROM Metric m1 WHERE m1.time < :date GROUP BY m1.instance, m1.name)", [date: endDate])
        DLModelService.prepareTrainingData(metrics)

//        def idList = Metric.executeQuery('select id from Metric m1 ' +
//                'where consumed = false and ' +
//                'time < :endDate and ' +
//                'exists(select 1 from Metric m2 ' +
//                'where m2.name = m1.name and ' +
//                'm2.instance = m2.instance and ' +
//                'm2.time >= :endDate ' +
//                'and m2.time < :nextDate) ', [endDate: endDate, nextDate: nextDate])
//        if (idList.size()){
//
//            def list = Metric.createCriteria().list {
//                lt('time', endDate)
//                eq('consumed', false)
//                projections {
//                    groupProperty 'instance'
//                    groupProperty 'name'
//                    avg('value')
//                }
//            }
//            list = list.collect {
//                def order = MetricOrder.findByInstanceAndName(it[0], it[1])
//                if (!order) {
//                    order = new MetricOrder(instance: it[0], name: it[1])
//                    order.save(flush: true)
//                }
//                [order.id, order.instance, order.name, it[2]]
//            }.sort { it[0] }
//
//            def input = []
//            list.each {
//                input << [
//                        index   : it[0],
//                        instance: it[1],
//                        metric  : it[2],
//                        value   : it[3]
//                ]
//            }
//
//            DLModelService.prepareTrainingData(input)
//
//            Metric.executeUpdate('update Metric set consumed = true where id in :idList', [idList: idList])
//        }

        endDate
    }
}
