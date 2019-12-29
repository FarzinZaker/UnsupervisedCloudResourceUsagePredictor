package f3

import groovy.time.TimeCategory
import org.springframework.context.annotation.Import

class ImportJob {
    static triggers = {
        simple repeatInterval: 5000l // execute job once in 5 seconds
    }

    static concurrent = false

    def importService

    def execute() {
        Indexes.ActiveIndexes.each { index ->
            def status = ImportStatus.findByName(index)
            if (!status) {
                def startDate = new Date()
                use(TimeCategory) {
                    startDate = startDate - 10.years
                }
                status = new ImportStatus(name: index, date: startDate).save()
            }
            def newDate = importService.load(index, status.date)
            ImportStatus.withNewTransaction {
                status = ImportStatus.findByName(index)
                status.date = newDate
                if (status.isDirty())
                    status.save(flush: true)
            }
        }
    }
}
