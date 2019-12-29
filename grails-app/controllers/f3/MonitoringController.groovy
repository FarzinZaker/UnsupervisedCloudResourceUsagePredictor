package f3

class MonitoringController {

    def DLModelService

    def display() {
        [data: DLModelService.predict()]
    }
}
