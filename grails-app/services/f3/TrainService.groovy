package f3


import grails.gorm.transactions.Transactional

@Transactional
class TrainService {

    def DLModelService

    def train() {
        try {
            DLModelService.train()
        } catch (ignored) {
            DLModelService.retrain()
        }
    }
}
