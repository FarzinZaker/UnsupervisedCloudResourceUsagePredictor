package f3

class MonitoringController {

    def predictService

    def display() {
        [data: predictService.safePredict()]
    }
}
