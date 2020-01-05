dataSource {
    driverClassName = com.mysql.cj.jdbc.Driver
    dialect = org.hibernate.dialect.MySQL8Dialect
    dbCreate = 'update'
    url = 'jdbc:mysql://localhost/f3'
    username = ''
    password = ''
    pooled = true
    jmxExport = true
}

elasticsearch.url = 'https://vpc-fcs-mtsvc-cac1-dev-es-rqtpxcvnvahpgdxyktdr4hdkoq.ca-central-1.es.amazonaws.com'

model {
    path = 'd:/models/f3.model'
    normalizer = 'd:/models/f3.normalizer'
    dataFiles = 'd:/models/data/'
}