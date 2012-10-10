<%@page import="db.Author"%>
<%@page import="db.Entity"%>
<%@page import="db.Article"%>
<%@page import="db.Category"%>
<%@page import="jsppages.JspCategory"%>
<%@page import="java.sql.Connection"%>
<%@page import="db.DBConnection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<style>
body,html,table {
    font-family: arial, sans-serif;
    font-size: small;
}
body{
    overflow:auto;
}

#resultsDiv {
    color: #8A89BF;
    border: 0px solid #000000;
    overflow: auto;
    height:auto;
}

#positionName,#positionNameSrc {
    color: #8A89BF;
}

#articleDiv,#articleDivContent,.article {
    color: #3634A6;
}

div {
    color: #000000;
    vertical-align: top;
    float: left;
    width: 100%;
    height: 100%;
    border: 0px;
}
</style>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <%
        if (session.getAttribute("conn") == null)
        {
            DBConnection dbc = new DBConnection("keyword.jsp", request.getRemoteAddr());
            Connection c = dbc.getConnection();
          session.setAttribute("conn",c);
        }
        Connection conn = (Connection)session.getAttribute("conn");
        String id = request.getParameter("id");
        
        JspCategory pageInfo = new JspCategory(id,conn);
    %>
<title><%=pageInfo.getName()%></title>
</head>
<body>
    <div id="frameDiv" style="float:none;width:1001px;margin-left:auto;margin-right:auto;">
        <div style="width: 1000px; height: 10px; border: 0px; background-color: #DBDBFF;">
        </div>
        <div id="searchSou1Div" style="width: 1000px; height: 20px; border: 0px; background-color: #DBDBFF;">
            <span id="newSearch" onClick="document.location='.'" style="cursor: pointer;margin: 10px;">New Search</span> 
        </div>
        <div style="width: 1000px; height: 10px; border: 0px; background-color: #DBDBFF;">
        </div>
        <div id="nameDiv" style="vertical-align: top; float: left; width: 100%; height: 50px; border: 0px;color: #3634A6;background-color: #FFFFFF;margin-top: 1px;">
            <sub>Category</sub>
            <h4 style="margin: 0"> <%=pageInfo.getParentsLine()%> </h4>
            <h2 style="margin: 0; margin-left: 20px;"><%=pageInfo.getName().replace("_"," ")%></h2>
        </div>
        <div class="div" id="mainDiv" style="width: 1000px; height: auto; border-top-width: 2px; border-top-color: #ff0000;">
            <div id="mainLeftDiv" style="width: 780px; height: auto; margin-top: 5px;background-color: #FFFFFF;">
                <div style="vertical-align: top; float: left;width:780px;height:auto; background-color: #EBEBFF;">
                    <div style="width:100%; height:10px;background-color: #FFFFFF;"></div>
                </div>
            <div id="categoriesDiv" style="vertical-align: top; float: left;display:block;width:760px;height:auto;background-color: #FCFCFF;">
                <%
                int offsetY = 550;
                if (!pageInfo.getChildren().isEmpty())
                {
                   %><div id="subcat1Div" style="vertical-align: top; float: left; width: 100%; height: 10px;"><span style="color: #3634A6; cursor: pointer"><b>Sub-categories:</b></span></div>
                    <div id="subcat1Div" style="vertical-align: top; float: left; width: 50%; height: 100%;"><ul>
                    <%
                    int halfSize = (pageInfo.getChildren().size() / 2)+1;
                    offsetY = (halfSize > 7) ? offsetY + (halfSize * 12) : (offsetY + pageInfo.getChildren().size() * 12);
                    int i = 0;
                    for (Category c : pageInfo.getChildren())
                    { 
                    	   if (i > 7 && i == (halfSize))
                    	   {
                		      %></ul></div><div id="subcat1Div" style="vertical-align: top; float: left; width: 50%; height: 100%;"><ul><%
                		   }
                    	   i++;
                           %><li><span id="<%=c.getId()%>" onClick="document.location='category.jsp?&id= <%=c.getId()%>'" style="cursor: pointer"><%=c.getName().replace("_"," ")%></span></li><%;
                    }
                }
                %>
                </ul></div>
            </div>
            <div style="width:100%; height:10px;background-color: #FFFFFF;">
            </div>
            <div style="width:760px; height:10px;background-color: #EBEBFF;"></div>
            <div style="width:100%; height:10px;background-color: #FFFFFF;"></div>
            <div id="searchDiv" style="width: 780px; height: 280px; border: 0px;background-color: #FFFFFF;">
                <div id="flashContent" style="width: 780px;height: 260px; border: 0px; background-color: #EBEBFF;">
                    <p>To view this page ensure that Adobe Flash Player version 10.0.0 or greater is installed.</p>
                    <script type="text/javascript"> 
                        var pageHost = ((document.location.protocol == "https:") ? "https://" : "http://"); 
                        document.write("<a href='http://www.adobe.com/go/getflashplayer'><img src='" 
                            + pageHost + "www.adobe.com/images/shared/download_buttons/get_flash_player.gif' alt='Get Adobe Flash player' /></a>" ); 
                    </script>
                </div>
                <noscript>
                    <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="760px" height="260" id="FlexMultiSearchPoint">
                        <param name="movie" value="FlexMultiSearchPoint.swf" />
                        <param name="quality" value="high" />
                        <param name="bgcolor" value="#EBEBFF" />
                        <param name="allowScriptAccess" value="sameDomain" />
                        <param name="allowFullScreen" value="true" />
                        <!--[if !IE]>-->
                        <object type="application/x-shockwave-flash" data="FlexMultiSearchPoint.swf" width="780" height="260">
                            <param name="quality" value="high" />
                            <param name="bgcolor" value="#EBEBFF" />
                            <param name="allowScriptAccess" value="sameDomain" />
                            <param name="allowFullScreen" value="true" />
                            <!--<![endif]-->
                            <!--[if gte IE 6]>-->
                            <p>Either scripts and active content are not permitted to run or Adobe Flash Player version 10.0.0 or greater is not installed.</p>
                            <!--<![endif]-->
                            <a href="http://www.adobe.com/go/getflashplayer"> 
                                <img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="Get Adobe Flash Player" /> 
                            </a>
                            <!--[if !IE]>-->
                        </object>
                        <!--<![endif]-->
                    </object>
                </noscript>
            </div>
            <div id="resultsDiv" style="width: 780px;height: 260px; border: 0px;">
                <%
                String authorIds = "";
                int i =0;
                for (Article a : pageInfo.getArticles())
                {
                    %>
                    <div class="articleItem" id="article<%=a.getId()%>" style="position:absolute;display:block;width:750px;height:auto;top:<%=650 + 150*i%>px;cursor: pointer;background-color: #FCFCFF;" onclick="document.location='article.jsp?&id=<%=a.getId()%>'">
                        <h3 style="margin: 0"><%=a.getTitle()%></h3>
                        <sub><h4 style="margin: 0"><%=a.getAuthorString()%><br/>publish date=<%=a.getPublishDate()%></h4></sub>
                        <p style="margin: 0"><%=a.getLeadParagraph()%></p>
                    </div>
                    <%
                    i++;
                }
                %>
            </div>
        </div>
        <div id="rightDiv" style="width: 210px; height: 100%; float: right; background-color: #FFFFFF;margin-top: 5px;">
            <div id="relCatDiv" style="background-color: #FCFCFF;height:auto">
                <%
                if (!pageInfo.getRelCat().isEmpty())
                {
                %>
               <span style="color: #3634A6; cursor: pointer"><b>Related categories</b></span>
               <ul id="people" style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
                    <%
                    for (Category c : pageInfo.getRelCat())
                    {
                        %><li><span id="<%=c.getId()%>" onClick="document.location='category.jsp?&id=<%=c.getId()%>'" style="cursor: pointer"><%=c.getName().replace("_"," ")%></span></li><%
                    }
                }
                %>
                </ul>
            </div>
            <div id="peopleDiv" style="background-color: #FCFCFF;height:auto">
                <%
                if (!pageInfo.getPeople().isEmpty())
                {
                %>
               <span style="color: #3634A6; cursor: pointer" onClick="document.location='entitiesGraph.jsp?&ids=<%=pageInfo.getArticleIdsString()%>'"><b>People</b></span>
               <ul id="people" style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
                    <%
                    for (Entity e : pageInfo.getPeople())
                    {
                        %><li><span id="<%=e.getId()%>" onClick="document.location='entity.jsp?&entity=<%=e.getName()%>&id=<%=e.getId()%>'" style="cursor: pointer"><%=e.getName()%></span></li><%
                    }
                }
                %>
                </ul>
            </div>
            <div id="locationDiv" style="background-color: #FCFCFF;height:auto">
                <%
                if (!pageInfo.getLocations().isEmpty())
                {
                %>
               <span style="color: #3634A6; cursor: pointer" onClick="document.location='entitiesGraph.jsp?&ids=<%=pageInfo.getArticleIdsString()%>'"><b>Locations</b></span>
               <ul id="locations" style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
                   <% for (Entity e : pageInfo.getLocations())
                    {
                        %><li><span id="<%=e.getId()%>" onClick="document.location='entity.jsp?&entity=<%=e.getName()%>&id=<%=e.getId()%>'" style="cursor: pointer"><%=e.getName()%></span></li><%
                    }
                }
                %>
                </ul>
            </div>
            <div id="organizationsDiv" style="background-color: #FCFCFF;height:auto">
                <%
                if (!pageInfo.getOrganizations().isEmpty())
                {
                %>
               <span style="color: #3634A6; cursor: pointer" onClick="document.location='entitiesGraph.jsp?&ids=<%=pageInfo.getArticleIdsString()%>'"><b>Organizations</b></span>
               <ul id="organizations" style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
                    <%
                    for (Entity e : pageInfo.getOrganizations())
                    {
                        %><li><span id="<%=e.getId()%>" onClick="document.location='entity.jsp?&entity=<%=e.getName()%>&id=<%=e.getId()%>'" style="cursor: pointer"><%=e.getName()%></span></li><%
                    }
                }
                %>
                </ul>
            </div>
            <div id="yearsDiv" style="background-color: #FCFCFF;height:auto;">
            <span style="color: #3634A6;"><b>Years</b></span>
                <ul id="keywords" style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
                    <%
                    for (Integer year : pageInfo.getYears())
                    {
                        %><li><span id="<%=year%>"onClick="document.location='year.jsp?&year=<%=year%>'"style="cursor: pointer"><%=year%></span></li><%
                    }
                %>
                </ul>
            </div>
            <div id="authorsDiv" style="background-color: #FCFCFF;height:auto">
                <%
            if (!pageInfo.getAuthors().isEmpty())
            {
                %>
                <span style="color: #3634A6;"><b>Authors</b></span>
                <ul id="authors" style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
                <%
                for (Author a : pageInfo.getAuthors())
                {
                    %><li><span id="<%=a.getId()%>" onClick="document.location='author.jsp?&id=<%=a.getId()%>'" style="cursor: pointer"><%=a.getName()%></span></li><%
                }
                %>
            </ul>
            <%
            }
                %>
            </div>
        </div>
    </div>
    </div>
</body>
<script type="text/javascript" src="swfobject.js"></script>
<script type="text/javascript">
            <!-- For version detection, set to min. required Flash Player version, or 0 (or 0.0.0), for no version detection. --> 
            var swfVersionStr = "10.0.0";
            <!-- To use express install, set to playerProductInstall.swf, otherwise the empty string. -->
            var xiSwfUrlStr = "playerProductInstall.swf";
            var flashvars = {};
            <%
            String articlesXML = "";
            for(Article a : pageInfo.getArticles())
            {
                articlesXML = articlesXML + "," + a.getId() + "," + a.getSearchScore();
            }
            articlesXML = articlesXML.substring(1);
            %>
            flashvars.dataURL = "getXml?ids=<%=articlesXML%>";
            var params = {};
            params.quality = "high";
            params.bgcolor = "#EBEBFF";
            params.allowscriptaccess = "sameDomain";
            params.allowfullscreen = "true";
            var attributes = {};
            attributes.id = "FlexMultiSearchPoint";
            attributes.name = "FlexMultiSearchPoint";
            attributes.align = "middle";
            swfobject.embedSWF(
                "FlexMultiSearchPoint.swf", "flashContent", 
                "758", "260", 
                swfVersionStr, xiSwfUrlStr, 
                flashvars, params, attributes);
            <!-- JavaScript enabled so display the flashContent div in case it is not replaced with a swf object. -->
            swfobject.createCSS("#flashContent", "display:block;text-align:left;");
        </script>
<script type="text/javascript">
            // Provides the proper address for the movie depending on browser
            function getFlashMovie(movieName) {
                var isIE = navigator.appName.indexOf("Microsoft") != -1;
                return (isIE) ? window[movieName] : document[movieName];
            }
            
            // order function gets called by .swf
            function order(csv){
                arr = [];
                arr = csv.split(",");

                var yStart = <%=offsetY%>;
                var item;
                //console.log('csv: ' + csv);
               
                var yCur = yStart;
                // first hide all
                for(var i = 0; i<arr.length; i++) {
                    item = document.getElementById("article"+arr[i]);
                    item.style.display="none";
                }   
                // display top
                var offset = 0;
                var numShown = 100;
                for(i = 0; i<arr.length; i++) {
                    item = document.getElementById("article"+arr[i]);
                    if (offset <= i && i < numShown + offset) {
                        item.style.display="block";
                        item.style.top = yCur + "px";
                        //console.log(item.id + ': ' + item.offsetHeight);
                        yCur+=item.offsetHeight + 10;
                    }
                }
                yCur+=15;
            }
        </script>
</html>