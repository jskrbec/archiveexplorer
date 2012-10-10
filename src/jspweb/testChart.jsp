<%@page import="java.sql.Connection"%>
<%@page import="db.DBConnection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<style>
body,html,table {
    font-family: arial, sans-serif;
    font-size: small;
}
th,li {
    color: #3634A6;
    cursor: pointer;
}
.headDiv,ul {
    color: #3634A6;
    font-weight: bold;
    background-color: #FCFCFF;
}
div {
background-color: #FCFCFF;
    color: #A19FFF;
    vertical-align: top;
    float: left;
    width: 100%;
    height: 100%;
    border: 0px;
}
span {
    cursor: pointer;
}
</style>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Archive Explorer</title>
<script type="text/javascript" src="jquery-1.5.min.js"></script>
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
   <script type="text/javascript">
      google.load('visualization', '1', {packages: ['corechart']});
    </script>
    <script type="text/javascript">
    $.ajax({
                                        type: 'POST',
                                        url: 'graph',
                                        data: {bla:''},
                                        success: function(data) {
                                            //$('#loading').remove();
                                            alert('success');
                                        },
                                        dataType:'json'
                                    });
      function drawVisualization() {
        // Some raw data (not necessarily accurate)
        var data = google.visualization.arrayToDataTable([
          ['Month', 'Bolivia', 'Ecuador', 'Madagascar', 'Papua New Guinea', 'Rwanda', 'Average'],
          ['January',  0,      938,         522,             998,           450,      614.6],
          ['February',  135,      1120,        599,             1268,          288,      682],
          ['March',  157,      1167,        587,             807,           397,      623],
          ['April',  139,      1110,        615,             968,           215,      609.4],
          ['May',  136,      691,         629,             1026,          366,      569.6],
          ['June',  136,      691,         629,             1026,          366,      569.6],
          ['July',  136,      691,         629,             1026,          366,      569.6],
          ['August',  136,      691,         629,             1026,          366,      569.6],
          ['September',  136,      691,         629,             1026,          366,      569.6],
          ['October',  136,      691,         629,             1026,          366,      569.6],
          ['November',  136,      691,         629,             1026,          366,      569.6],
          ['December',  136,      691,         629,             1026,          366,      569.6]
        ]);

        var options = {
          title : 'Most common keywords by year',
          vAxis: {title: "No of articles"},
          hAxis: {title: "Month"},
          isStacked: true,
          width: 800,
          height: 400
        };

        var chart = new google.visualization.AreaChart(document.getElementById('chart_div'));
        chart.draw(data, options);
      }
      google.setOnLoadCallback(drawVisualization);
    </script>
    <script type="text/javascript">
      google.load("visualization", "1", {packages:["treemap"]});
      google.setOnLoadCallback(drawChart);
      function drawChart() {
        // Create and populate the data table.
        var data = google.visualization.arrayToDataTable([
          ['Month', 'Parent', 'Market trade volume (size)', 'Market increase/decrease (color)'],
          ['Global',    null,                 0,                               0],
          ['January',  'Global',             0,                               0],
          ['February', 'Global',             0,                               0],
          ['March',    'Global',             0,                               0],
          ['April',    'Global',             0,                               0],
          ['May',      'Global',             0,                               0],
          ['June',     'Global',             0,                               0],
          ['July',     'Global',             0,                               0],
          ['August',   'Global',             0,                               0],
          ['September','Global',             0,                               0],
          ['October',  'Global',             0,                               0],
          ['November', 'Global',             0,                               0],
          ['December', 'Global',             0,                               0],
          ['Brazil',    'January',            11,                              10],
          ['USA',       'January',            52,                              31],
          ['Mexico',    'February',            24,                              12],
          ['Canada',    'February',            16,                              -23],
          ['France',    'March',             42,                              -11],
          ['Germany',   'March',             31,                              -2],
          ['Sweden',    'April',             22,                              -13],
          ['Italy',     'April',             17,                              4],
          ['UK',        'May',             21,                              -5],
          ['China',     'May',               36,                              4],
          ['Japan',     'June',               20,                              -12],
          ['India',     'June',               40,                              63],
          ['Laos',      'July',               4,                               34],
          ['Mongolia',  'July',               1,                               -5],
          ['Israel',    'August',               12,                              24],
          ['Iran',      'August',               18,                              13],
          ['Pakistan',  'September',               11,                              -52],
          ['Egypt',     'September',             21,                              0],
          ['S. Africa', 'October',             30,                              43],
          ['Sudan',     'November',             12,                              2],
          ['Congo',     'December',             10,                              12],
          ['Zair',      'December',             8,                               10]
        ]);

        // Create and draw the visualization.
        var tree = new google.visualization.TreeMap(document.getElementById('chart_div1'));
        tree.draw(data, {
          minColor: '#f00',
          midColor: '#ddd',
          maxColor: '#0d0',
          headerHeight: 15,
          fontColor: 'black',
          showScale: true});
        }
    </script>
</head>
<body>
    <div id="frameDiv" style="float:none;width:1001px;margin-left:auto;margin-right:auto;">
        <%
        //System.out.println("ae session: " + session.getId());
        if (session.getAttribute("conn") == null)
        {
            DBConnection dbc = new DBConnection("test.jsp", request.getRemoteAddr());
            Connection c = dbc.getConnection();
          session.setAttribute("conn",c);
        }
        Connection conn = (Connection)session.getAttribute("conn");
        
          if (request.getParameter("source") != null)
          {
              session.setAttribute("chooseCorpus",request.getParameter("source"));
          }
          int sourceId = -1;
          
          if (session.getAttribute("chooseCorpus") != null)
          {
              sourceId = Integer.parseInt((String)session.getAttribute("chooseCorpus"));
          }
        %>
         <div id="chart_div"></div>
         <div id="chart_div1"></div>
    </div>
</body>