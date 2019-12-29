package f3

class TrainJob {
    static triggers = {
        simple repeatInterval:  60 * 1000l//, startDelay: 5 * 60 * 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def trainService

    def execute() {
        trainService.train()
    }
}
