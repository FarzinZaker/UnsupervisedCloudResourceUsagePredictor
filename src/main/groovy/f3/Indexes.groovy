package f3

class Indexes {

    public static String Nagios = 'nagios'
    public static String MetricBeat = 'metricbeat'
    public static String WinLogBeat = 'winlogbeat'
    public static String AuditBeat = 'auditbeat'
    public static String CloudWatch = 'cloudwatch'
    public static String LDAP = 'ldap'
    public static String SysLog = 'syslog'
    public static String FortiGate = 'fortigate'
    public static String Inventory = 'inventory'
    public static String DLQ = 'dlq'
    public static String BAS = 'bas'
    public static String Metric = 'metric'
    public static String Billing = 'billing'
    public static String FileBeat = 'filebeat'
    public static String Kennedy = 'kennedy'
    public static String Spark = 'spark'
    public static String Heartbeat = 'heartbeat'
    public static String Slow = 'slow'

    public static List<String> ActiveIndexes = [Nagios, CloudWatch]
//    public static List<String> ActiveIndexes = [MetricBeat]
}
