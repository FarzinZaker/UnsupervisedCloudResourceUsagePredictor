package f3

class Metric {

    String name
    String instance
    Date time
    Double value
    String index
    Boolean consumed = false

    static mapping = {
        index column: 'index_name'
        time index: 'index_time'
    }

    static constraints = {
    }
}
