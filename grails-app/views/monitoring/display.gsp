<%--
  Created by IntelliJ IDEA.
  User: Farzin
  Date: 2019-12-28
  Time: 6:40 p.m.
--%>

<%@ page import="org.apache.commons.lang.StringUtils" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Monitor</title>
</head>

<body>
<table id="dataTable">
    <thead>
    <tr>
        <th>Instance</th>
        <th>Metric</th>
        <th>Current</th>
        <th>Next</th>
        <th>Change</th>
    </tr>
    </thead>
    <tbody>
    <g:each in="${data}" var="record">
        <tr>
            <td>${record.instance}</td>
            <td>${StringUtils.join(
                    StringUtils.splitByCharacterTypeCamelCase(record.metric),
                    ' '
            )}</td>
            <td>${record.current}</td>
            <td>${record.next}</td>
            <g:set var="change" value="${record.next.toDouble() - record.current.toDouble()}"/>
            <td>
                <g:if test="${change < 0}">
                    <i class="fa fa-arrow-up"></i>
                    <g:formatNumber number="${Math.abs(change)}" format="0.##"/>
                </g:if>
                <g:if test="${change > 0}">
                    <i class="fa fa-arrow-down"></i>
                    <g:formatNumber number="${Math.abs(change)}" format="0.##"/>
                </g:if>
                <g:if test="${change == 0}">
                    -
                </g:if>
            </td>
        </tr>
    </g:each>
    </tbody>
</table>
<script>
    $(document).ready(function () {
        var groupColumn = 0;
        var table = $('#dataTable').DataTable({
            "columnDefs": [
                {"visible": false, "targets": groupColumn}
            ],
            "order": [[groupColumn, 'asc']],
            "displayLength": 24,
            "drawCallback": function (settings) {
                var api = this.api();
                var rows = api.rows({page: 'current'}).nodes();
                var last = null;

                api.column(groupColumn, {page: 'current'}).data().each(function (group, i) {
                    if (last !== group) {
                        $(rows).eq(i).before(
                            '<tr class="group"><td colspan="5">' + group + '</td></tr>'
                        );

                        last = group;
                    }
                });
            }
        });

        // Order by the grouping
        $('#dataTable tbody').on('click', 'tr.group', function () {
            var currentOrder = table.order()[0];
            if (currentOrder[0] === groupColumn && currentOrder[1] === 'asc') {
                table.order([groupColumn, 'desc']).draw();
            } else {
                table.order([groupColumn, 'asc']).draw();
            }
        });
    });
</script>
</body>
</html>