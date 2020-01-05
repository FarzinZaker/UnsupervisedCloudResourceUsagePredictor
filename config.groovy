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

elasticsearch.url = ''

model {
    path = 'd:/models/f3.model'
    normalizer = 'd:/models/f3.normalizer'
    dataFiles = 'd:/models/data/'
}