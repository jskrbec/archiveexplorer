<%@page import="db.DBConnection"%>
<%@page import="java.util.Calendar"%>
<%@page import="db.Article"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.LinkedHashMap"%>
<%@page import="db.ArticleList"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Connection"%>
<html>
<style>
body,html,table {
	font-family: arial, sans-serif;
	font-size: small;
}

#entDiv,#kwDiv,#autDiv {
	font-family: arial, sans-serif;
	font-size: 12px;
}

div {
	border: 0px solid #8A89BF;
	color: #3634A6;
	background-color: #EBEBFF;
}

table {
	color: #3634A6;
	background-color: #EBEBFF;
}
</style>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script type="text/javascript" src="raphael-min.js"></script>
<script type="text/javascript" src="popup.js"></script>
<script type="text/javascript" src="../jquery-1.5.min.js"></script>
<title>Year</title>
</head>
<body>
	<div
		style="width: 1000px; height: 10px; border: 0px; background-color: #DBDBFF;">
	</div>
	<div id="searchSou1Div"
		style="width: 1000px; height: 20px; border: 0px; background-color: #DBDBFF;">
		<span id="newSearch" onClick="document.location='.'"
			style="cursor: pointer">New Search</span>
	</div>
	<div
		style="width: 1000px; height: 10px; border: 0px; background-color: #DBDBFF;">
	</div>
	<%
        if (session.getAttribute("conn") == null)
        {
        	DBConnection dbc = new DBConnection("yearGraph.jsp", request.getRemoteAddr());
            Connection c = dbc.getConnection();
          session.setAttribute("conn",c);
        }
        Connection conn = (Connection)session.getAttribute("conn");
        
        int sourceId = -1;
        if (session.getAttribute("chooseCorpus") != null)
        {
            sourceId = Integer.parseInt((String)session.getAttribute("chooseCorpus"));
        }
        System.out.println("ids: " + request.getParameter("ids"));
        String ids = request.getParameter("ids");
        ids = (ids == null || ids.isEmpty()) ? null : (ids.contains(",") ? ids : null);
        if (ids == null || ids.isEmpty())
        {
            %>
	<div id="searchDiv"
		style="width: 1000px; height: 602px; border: 0px; background-color: #EBEBFF;">
		<span id="newSearch" onClick="document.location='.'"
			style="cursor: pointer">No search results found</span>
	</div>
	<%
        }
        else
       {
        %>
	<div id="pageDiv" style="width: 1000px; border: 0px;"></div>
	<div id="raphaelGraphDiv"
		style="vertical-align: top; float: left; width: 100%;"></div>
	<%
           ArticleList artList = new ArticleList(conn);
           artList.getByIds(ids);
           int yearMin = Integer.MAX_VALUE;
           int yearMax = 0;
           for (Article a : artList)
           {
        	   Calendar c = Calendar.getInstance();
               c.setTime(a.getPublishDate());
               int year = c.get(Calendar.YEAR); 
               if (year > yearMax)
               {
            	   yearMax = year;
               }
               if (year < yearMin)
               {
            	   yearMin = year;
               }
           }
           
           LinkedHashMap<String,ArrayList<Integer>> months = new LinkedHashMap<String,ArrayList<Integer>>();
           int yearmaxTemp = yearMax > 0 ? yearMax : yearMin;
           
           for (int yearTemp = yearMin; yearTemp <= yearmaxTemp ; yearTemp++)
           {
               for (int m = 1; m <=12; m++)
               {
                    ArrayList<Integer> articleIds = new ArrayList<Integer>();
                    months.put(m + "." + yearTemp, articleIds);
               }
           }
           for (Article a : artList)
           {
               String month;
               Calendar c = Calendar.getInstance();
               c.setTime(a.getPublishDate());
               int year = c.get(Calendar.YEAR); 
               month = c.get(Calendar.MONTH) + "." + year;
               if (months.keySet().contains(month))
               {
                   months.get(month).add(a.getId());
               }
           }
           %>

	<script type="text/javascript">
                Raphael.fn.drawGrid = function (x, y, w, h, wv, hv, color) {
                    color = color || "#000";
                    var path = ["M", Math.round(x) + .5, Math.round(y) + .5, "L", Math.round(x + w) + .5, Math.round(y) + .5, Math.round(x + w) + .5, Math.round(y + h) + .5, Math.round(x) + .5, Math.round(y + h) + .5, Math.round(x) + .5, Math.round(y) + .5],
                    rowHeight = h / hv,
                    columnWidth = w / wv;
                    for (var i = 1; i < hv; i++) {
                        path = path.concat(["M", Math.round(x) + .5, Math.round(y + i * rowHeight) + .5, "H", Math.round(x + w) + .5]);
                    }
                    for (i = 1; i < wv; i++) {
                        path = path.concat(["M", Math.round(x + i * columnWidth) + .5, Math.round(y) + .5, "V", Math.round(y + h) + .5]);
                    }
                    return this.path(path.join(",")).attr({stroke: color});
                };

                window.onload = function () {
                    function getAnchors(p1x, p1y, p2x, p2y, p3x, p3y) {
                        var l1 = (p2x - p1x) / 2,
                        l2 = (p3x - p2x) / 2,
                        a = Math.atan((p2x - p1x) / Math.abs(p2y - p1y)),
                        b = Math.atan((p3x - p2x) / Math.abs(p2y - p3y));
                        a = p1y < p2y ? Math.PI - a : a;
                        b = p3y < p2y ? Math.PI - b : b;
                        var alpha = Math.PI / 2 - ((a + b) % (Math.PI * 2)) / 2,
                        dx1 = l1 * Math.sin(alpha + a),
                        dy1 = l1 * Math.cos(alpha + a),
                        dx2 = l2 * Math.sin(alpha + b),
                        dy2 = l2 * Math.cos(alpha + b);
                        return {
                            x1: p2x - dx1,
                            y1: p2y + dy1,
                            x2: p2x + dx2,
                            y2: p2y + dy2
                            };
                        }
                        // Grab the data
                        var labels = [], //x os
                        data = []; //vrednosti na grafu
                        var idString = [];
                        <%
                        for (String month : months.keySet())
                        {
                            %>
                               labels.push(<%=month%>);
                                data.push(<%=months.get(month).size()%>);
                               <%
                               String hits = "";
                               for (Integer hit : months.get(month))
                               {
                                   hits = hits + "," + hit;
                               }
                               hits = hits.isEmpty() ? hits : hits.substring(1);
                               System.out.println("hits: " + hits);
                               %>
                               idString.push('<%=hits%>');
                            <%
                        }
                        %>
    
                        // Draw
                        var width = 800,
                        height = 250,
                        leftgutter = 30,
                        bottomgutter = 20,
                        topgutter = 20,
                        colorhue = .6 || Math.random(),
                        color = "hsb(" + [colorhue, .5, 1] + ")",
                        r = Raphael("raphaelGraphDiv", width, height),
                        txt = {font: '12px Helvetica, Arial', fill: "#3634A6"},
                        txt1 = {font: '10px Helvetica, Arial', fill: "#fff"},
                        txt2 = {font: '12px Helvetica, Arial', fill: "#000"},
                        X = (width - leftgutter) / labels.length,
                        max = Math.max.apply(Math, data),
                        Y = (height - bottomgutter - topgutter) / max;
                    r.drawGrid(leftgutter + X * .5 + .5, topgutter + .5, width - leftgutter - X, height - topgutter - bottomgutter, 10, 10, "#333");
                    var path = r.path().attr({stroke: "#3634A6", "stroke-width": 4, "stroke-linejoin": "round"}),
                        bgp = r.path().attr({stroke: "none", opacity: .3, fill: "#3634A6"}),
                        label = r.set(),
                        is_label_visible = false,
                        leave_timer,
                        blanket = r.set();
                    label.push(r.text(60, 12, "24 hits").attr(txt));
                    label.push(r.text(60, 27, "22 bla").attr(txt1).attr({fill: "#F4F4FF"}));
                    label.hide();
                    var frame = r.popup(100, 100, label, "right").attr({fill: "#000", stroke: "#3634A6", "stroke-width": 2, "fill-opacity": .3}).hide();
                
                    var p, bgpp;
                    for (var i = 0, ii = labels.length; i < ii; i++) {
                        var y = Math.round(height - bottomgutter - Y * data[i]),
                            x = Math.round(leftgutter + X * (i + .5)),
                            t = r.text(x, height - 6, labels[i]).attr(txt).toBack();
                        if (!i) {
                            p = ["M", x, y, "C", x, y];
                            bgpp = ["M", leftgutter + X * .5, height - bottomgutter, "L", x, y, "C", x, y];
                        }
                        if (i && i < ii - 1) {
                            var Y0 = Math.round(height - bottomgutter - Y * data[i - 1]),
                                X0 = Math.round(leftgutter + X * (i - .5)),
                                Y2 = Math.round(height - bottomgutter - Y * data[i + 1]),
                                X2 = Math.round(leftgutter + X * (i + 1.5));
                            var a = getAnchors(X0, Y0, x, y, X2, Y2);
                            p = p.concat([a.x1, a.y1, x, y, a.x2, a.y2]);
                            bgpp = bgpp.concat([a.x1, a.y1, x, y, a.x2, a.y2]);
                        }
                        var dot = r.circle(x, y, 4).attr({fill: "#000", stroke: "#3634A6", "stroke-width": 2});
                        blanket.push(r.rect(leftgutter + X * i, 0, X, height - bottomgutter).attr({stroke: "none", fill: "#fff", opacity: 0}));
                        var rect = blanket[blanket.length - 1];
                        (function (x, y, data, idString, lbl, dot) {
                            var timer, i = 0;
                            rect.hover(function () {
                                clearTimeout(leave_timer);
                                var side = "right";
                                if (x + frame.getBBox().width > width) {
                                    side = "left";
                                }
                                var ppp = r.popup(x, y, label, side, 1);
                                frame.show().stop().animate({path: ppp.path}, 200 * is_label_visible);
                                label[0].attr({text: data + " hit" + (data == 1 ? "" : "s")}).show().stop().animateWith(frame, {translation: [ppp.dx, ppp.dy]}, 200 * is_label_visible); 
                                label[1].attr({text: lbl + " "}).show().stop().animateWith(frame, {translation: [ppp.dx, ppp.dy]}, 200 * is_label_visible);
                                dot.attr("r", 6);
                                dot.click(function(event){document.location = 'docAtlas.jsp?&ids=' + idString});
                                is_label_visible = true;
                            }, function () {
                                dot.attr("r", 4);
                                leave_timer = setTimeout(function () {
                                    frame.hide();
                                    label[0].hide();
                                    label[1].hide();
                                    is_label_visible = false;
                                }, 1);
                            })
                            .click(function(event){document.location = 'docAtlas.jsp?&ids=' + idString});;
                        })(x, y, data[i], idString[i], labels[i], dot);
                    }
                    p = p.concat([x, y, x, y]);
                    bgpp = bgpp.concat([x, y, x, y, "L", x, height - bottomgutter, "z"]);
                    path.attr({path: p});
                    bgp.attr({path: bgpp});
                    frame.toFront();
                    label[0].toFront();
                    label[1].toFront();
                    blanket.toFront();
                };
           </script>
	<%}%>
	
	<% 
if (request.getRemoteAddr().startsWith("66.249."))
{
    conn.close();
}
%>
</body>
</html>