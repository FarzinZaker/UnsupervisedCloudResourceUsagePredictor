package f3

class TrainJob {
    static triggers = {
       simple repeatInterval:  15 * 60 * 1000l//, startDelay: 6 * 60 * 60 * 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def trainService

    def execute() {
        trainService.train()
    }
}
