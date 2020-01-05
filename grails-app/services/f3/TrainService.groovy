package f3

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory

@Transactional
class TrainService {

    def DLModelService

    def train() {
        try {
            DLModelService.train()
        } catch (exception) {
            println exception.message
            Thread.sleep(5000)
        }
    }
}
