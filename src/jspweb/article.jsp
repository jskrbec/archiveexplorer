<%@page import="db.DBConnection"%>
<%@page import="java.util.Calendar"%>
<%@page import="db.Article"%>
<%@page import="db.Source"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="db.Author"%>
<%@page import="db.AuthorList"%>
<%@page import="db.Keyword"%>
<%@page import="db.KeywordList"%>
<%@page import="db.Entity"%>
<%@page import="db.EntityList"%>
<%@page import="java.util.ArrayList"%>
<%@page import="db.Category"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Connection"%>
<%@page import="db.CategoriesList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <style>
	    body,html,table {
		   font-family: arial, sans-serif;
		   font-size: small;
	    }
		.articleItem {
			border: 0.5px solid #8A89BF;
			padding: 3px;
			cursor: pointer;
			color: #3634A6;
			margin: 4px;
			border-radius: 10px;
		}
		#resultsDiv {
			color: #8A89BF;
			border: 0px solid #000000;
			height: 100%;
			width: 100%;
			display: none;
		}
		
		th,li {
			color: #3634A6;
			cursor: pointer;
		}
		
		#positionName,#positionNameSrc {
			color: #8A89BF;
		}
		
		tr {
			color: #A19FFF;
		}
		
		#articleDiv,#articleDivContent,.article {
			color: #3634A6;
		}
		
		.headDiv,ul {
			color: #3634A6;
			font-weight: bold;
			background-color: #EBEBFF;
		}
		div {
			background-color: #F4F4FF;
			color: #A19FFF;
			vertical-align: top;
			float: left;
			width: 100%;
			height: 100%;
			border: 0px;
		}
		
		input {
			background-color: #F4F4FF;
			color: #A19FFF;
		}
		
		span {
			cursor: pointer;
		}
    </style>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%
            if (session.getAttribute("conn") == null)
            {
                DBConnection dbc = new DBConnection("article.jsp", request.getRemoteAddr());
                Connection c = dbc.getConnection();
                session.setAttribute("conn",c);
            }
            Connection conn = (Connection)session.getAttribute("conn");
            Integer id = Integer.parseInt((String)request.getParameter("id"));
          
            Article article = new Article(conn, id);
            article.readArticleFromDB();
            String title = article.getTitle();
        %>
        <title>AE - <%=title%></title>
        <script type="text/javascript" src="../jquery-1.5.min.js"></script>
        <script src="../history.js"></script>
    </head>
<body>
<div id="frameDiv" style="float:none;width:1001px;margin-left:auto;margin-right:auto;">
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
	<div class="div" id="mainDiv"
		style="width: 1000px; height: 100%; border-top-width: 2px; border-top-color: #ff0000; background-color: #EBEBFF;">
		<div id="mainLeftDiv"
			style="width: 790px; height: 100%; border: 0px; margin: 4px;">
			<div id="articleDiv" style="background-color: #F4F4FF; width: 778px;">
				<div id="articleDivContent" style="width: 90%; height: 90%;">
					<h1><%=title%></h1>
					<sub><%=article.getPublishDate().toString()%></sub> <br />
					<p>
						<b><%=article.getLeadParagraph()%></b>
					</p>
					<%=article.getText()%>
				</div>
			</div>
		</div>
		<div id="rightDiv"
			style="width: 190px; height: 100%; float: right; background-color: #EBEBFF;">
			<div class="headDiv" onClick="document.location='entity.jsp'">Entities</div>
			<div id="entitiesDiv" style="background-color: #EBEBFF;">
				<ul id="entities"
					style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
					<%
                EntityList entList = new EntityList(conn);
                entList.getEntitiesByArticleIds(id.toString(),null,20);
                if (entList.isEmpty())
                {
                    %><li><i>No entities exist in DB</i>
					</li>
					<%
                }
                else
                {
                	for (Entity e : entList)
                	{
                		%><li><span id="<%=e.getId()%>"
						onClick="document.location='entity.jsp?&entity=<%=e.getName()%>&id=<%=e.getId()%>'"
						style="cursor: pointer"><%=e.getName()%></span>
					</li>
					<%
                	}
                }
                %>
				</ul>
			</div>
			<div class="headDiv" style="background-color: #EBEBFF;">Keywords</div>
			<div id="keywordsDiv" style="background-color: #EBEBFF;">
				<ul id="keywords"
					style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
					<%
                KeywordList kwList = new KeywordList(conn);
                kwList.getKeywordsByArticleIds(id.toString(),20);
                if (kwList.isEmpty())
                {
                    %><i>No keywords exist in DB</i>
					<%
                }
                else
                {

                    for (Keyword k : kwList)
                    {
                        %><li><span id="<%=k.getId()%>"
						onClick="document.location='keyword.jsp?&kw=<%=k.getName()%>&id=<%=k.getId()%>'"
						style="cursor: pointer"><%=k.getName()%></span>
					</li>
					<%
                    }
                }
                %>
				</ul>
			</div>
			<div class="headDiv">Years</div>
			<div id="yearsDiv" style="background-color: #EBEBFF;">
				<ul id="years"
					style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
					<%
					Calendar c = Calendar.getInstance();
					c.setTime(article.getPublishDate());
					int year = c.get(Calendar.YEAR); 
                    %><li><span id="<%=year%>"
						onClick="document.location='year.jsp?&year=<%=year%>'"
						style="cursor: pointer"><%=year%></span>
					</li>
					<%
               
                %>
				</ul>
			</div>
			<div class="headDiv">Authors</div>
			<div id="authorsDiv" style="background-color: #EBEBFF;">
				<ul id="authors"
					style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
					<%
                AuthorList auList = new AuthorList(conn);
                auList.getAuthorsByArticleIds(id.toString(),null);
                if (auList.isEmpty())
                {
                    %><i>No authors exist in DB</i>
					<%
                }
                else
                {
                    for (Author a : auList)
                    {
                        %><li><span id="<%=a.getId()%>"
						onClick="document.location='author.jsp?&id=<%=a.getId()%>'"
						style="cursor: pointer"><%=a.getName()%></span>
					</li>
					<%
                    }
                }
                %>
				</ul>
			</div>
		</div>
	</div>

	<div id="ac">
		<%
        String appletText = article.getTitle() + " " + article.getLeadParagraph() + " " + article.getText(); 
        appletText = appletText.replace("<p>","");
        appletText = appletText.replace("</p>","");
        %>
		<applet name="wordle" mayscript="mayscript"
			code="wordle.WordleApplet.class" codebase="http://wordle.appspot.com"
			archive="/j/v1373/wordle.jar" width="100%" height="600">
			<param id="wordlText" name="text" value="<%=appletText%>" />
			<param name="java_arguments" value="-Xmx256m -Xms64m">
			Your browser doesn\'t seem to understand the APPLET tag. You need to
			install and enable the <a href="http://java.com/">Java</a> plugin.
		</applet>
	</div>
</div>
</body>
<script type="text/javascript">
    function backToResults()
    {
    // Prepare
        var History = window.History;
        History.back();
    }
</script>
<% 
if (request.getRemoteAddr().startsWith("66.249."))
{
	conn.close();
}
%>
</html>