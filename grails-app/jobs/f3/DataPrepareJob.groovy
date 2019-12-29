package f3

class DataPrepareJob {
    static triggers = {
        simple repeatInterval: 1000l // execute job once in 5 seconds
    }

    static concurrent = false

    def dataPrepareService

    def execute() {
        def index = 'dataPrepare'
        if (ImportStatus.findByNameNotEqual(index)?.version < 2) {
            Thread.sleep(60 * 1000l)
            return
        }

        def status = ImportStatus.findByName(index)
        if (!status) {
            Date startDate = Metric.executeQuery("select min(time) from Metric where consumed = false").find()
//            println startDate
            status = new ImportStatus(name: index, date: startDate).save(flush: true)
        }

        def newDate = dataPrepareService.prepareTrainingData(status.date)

        ImportStatus.withNewTransaction {
            status = ImportStatus.findByName(index)
            status.date = newDate
            if (status.isDirty())
                status.save(flush: true)
        }
    }
}
