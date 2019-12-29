package f3


import f3.parsers.CloudWatchParser

import f3.parsers.NagiosParser

class ParserFactory {
    static IMetricParser getParser(String index) {
        switch (index) {
            case Indexes.CloudWatch:
                return new CloudWatchParser()
            case Indexes.Nagios:
                return new NagiosParser()
            default:
                throw new UnsupportedOperationException("No parser class is implemented for index: $index")
        }
    }
}
