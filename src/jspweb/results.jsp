<%@page import="db.MonitoringDB"%>
<%@page import="db.DBConnection"%>
<%@page import="db.Article"%>
<%@page import="db.ArticleList"%>
<%@page import="java.sql.Date"%>
<%@page import="db.SearchArticles"%>
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
body{
    overflow:auto;
}
.articleItem {
	border: 0.5px solid #8A89BF;
	padding: 3px;
	cursor: pointer;
	color: #3634A6;
	margin: 4px;
	border-radius: 10px;
	background-color: #F4F4FF;
}

#resultsDiv {
	color: #8A89BF;
	border: 0px solid #000000;
	background-color: #F4F4FF;
	overflow: auto;
	height:auto;
}

th,li {
	color: #3634A6;
	cursor: pointer;
}

#positionName,#positionNameSrc {
	color: #8A89BF;
}

#articleDiv,#articleDivContent,.article {
	color: #3634A6;
	background-color: #F4F4FF;
}

div {
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
<title>Archive Explorer - search results</title>
<script type="text/javascript" src="jquery-1.5.min.js"></script>
</head>
<body>
<div id="frameDiv" style="float:none;width:1001px;margin-left:auto;margin-right:auto;">
	<% MonitoringDB m_monitoringDB = new MonitoringDB("results.jsp");
        if (session.getAttribute("conn") == null)
        {
        	DBConnection dbc = new DBConnection("results.jsp", request.getRemoteAddr());
            Connection c = dbc.getConnection();
          session.setAttribute("conn",c);
        }
        Connection conn = (Connection)session.getAttribute("conn");
        
          String dateString = request.getParameter("date");
          Date date = (dateString != null && !dateString.isEmpty()) ? Date.valueOf(dateString) : null;
          String s_text = request.getParameter("text");
          String s_entity = request.getParameter("entity");
          String s_person = request.getParameter("person");
          String s_org = request.getParameter("org");
          String s_loc = request.getParameter("loc");
          String s_kw = request.getParameter("kw");
          String s_author = request.getParameter("author");
          String s_cat = request.getParameter("cat");
          
          SearchArticles searchArticles = new SearchArticles(
        		  s_text,
        		  s_entity,
        		  s_person,
        		  s_org,
        		  s_loc,
        		  s_kw,
        		  s_author,
        		  s_cat,
        		  date,
        		  conn);
          long timeStart = System.currentTimeMillis();
          searchArticles.search();
          long timeStop = System.currentTimeMillis();
          m_monitoringDB.queryTime(timeStart, timeStop, "searchArticles.search()",0);
          ArticleList articles = new ArticleList(conn);
          articles = searchArticles.getArticles();
          System.out.println("search articles: " + articles.size());
          String articleIdsString = "";
          if (articles == null || articles.isEmpty())
          {
        	  articleIdsString =  null;
        	  %>
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
	<div id="searchDiv"
		style="width: 1000px; height: 602px; border: 0px; background-color: #EBEBFF;">
		<span id="newSearch" onClick="document.location='.'"
			style="cursor: pointer">No search results found</span>
	</div>
	<%
          }
          else
          {
        	  for (Article a : articles)
        	  {
                  articleIdsString = articleIdsString + "," + a.getId();
              }
        	  articleIdsString = articleIdsString.substring(1);
        	  
        	  int sourceId = -1;
          
        	  if (session.getAttribute("chooseCorpus") != null)
              {
        		  sourceId = Integer.parseInt((String)session.getAttribute("chooseCorpus"));
              }
        %>
	<div
		style="width: 1000px; height: 10px; border: 0px; background-color: #DBDBFF;">
	</div>
	<div id="searchSou1Div"
		style="width: 1000px; height: 20px; border: 0px; background-color: #DBDBFF;">
		<div style="width: auto; height: 100%; border: 0px; background-color: #DBDBFF;"><span id="newSearch" onClick="document.location='.'" style="cursor: pointer">New Search</span></div> 
		<div style="width: 30px; height: 100%; border: 0px; background-color: #DBDBFF;"></div>
		<span onclick="document.location='docAtlas.jsp?&ids=<%=session.getId()%>'">Document Atlas</span>
			<%
			session.removeAttribute("aidmap");
			session.setAttribute("articleIdsString",articleIdsString);
			%>
	</div>
	<div
		style="width: 1000px; height: 10px; border: 0px; background-color: #DBDBFF;">
	</div>
	<%
    String searchParams = "";
    if (s_text != null && !s_text.isEmpty())
    {
    	searchParams = s_text;
    }
    if (s_entity != null && !s_entity.isEmpty())
    {
        searchParams = searchParams.isEmpty() ? s_entity : searchParams + ", " + s_entity;
    }
    if (s_person != null && !s_person.isEmpty())
    {
        searchParams = searchParams.isEmpty() ? s_person : searchParams + ", " + s_person;
    }
    if (s_org != null && !s_org.isEmpty())
    {
        searchParams = searchParams.isEmpty() ? s_org : searchParams + ", " + s_org;
    }
    if (s_loc != null && !s_loc.isEmpty())
    {
        searchParams = searchParams.isEmpty() ? s_loc : searchParams + ", " + s_loc;
    }
    if (s_kw != null && !s_kw.isEmpty())
    {
        searchParams = searchParams.isEmpty() ?  s_kw : searchParams + ", " + s_kw;
    }
    if (s_author != null && !s_author.isEmpty())
    {
        searchParams = searchParams.isEmpty() ? s_author : searchParams + ", " + s_author;
    }
    if (s_cat != null && !s_cat.isEmpty())
    {
        searchParams = searchParams.isEmpty() ? s_cat : searchParams + ", " + s_cat;
    }
    if (date != null)
    {
        searchParams = searchParams.isEmpty() ? date.toString() : searchParams + ", " + date;
    }
    String articlesSize = articles.size() > 99 ? "> 100" : "" + articles.size();
    %>
	<div
		style="width: 1000px; height: 20px; border: 0px; background-color: #DBDBFF;"><%=articlesSize%>
		search results for: <%=searchParams %>
	</div>
	<div
		style="width: 1000px; height: 10px; border: 0px; background-color: #DBDBFF;">
	</div>
	<div id="searchDiv"
		style="width: 1000px; height: 280px; border: 0px;">
		<div id="flashContent"
			style="width: 1000px;height: 260px; border: 0px; background-color: #EBEBFF;">
			<p>To view this page ensure that Adobe Flash Player version
				10.0.0 or greater is installed.</p>
			<script type="text/javascript"> 
                var pageHost = ((document.location.protocol == "https:") ? "https://" : "http://"); 
                document.write("<a href='http://www.adobe.com/go/getflashplayer'><img src='" 
                                + pageHost + "www.adobe.com/images/shared/download_buttons/get_flash_player.gif' alt='Get Adobe Flash player' /></a>" ); 
            </script>
		</div>

		<noscript>
			<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
				width="1000px" height="260" id="FlexMultiSearchPoint">
				<param name="movie" value="FlexMultiSearchPoint.swf" />
				<param name="quality" value="high" />
				<param name="bgcolor" value="#EBEBFF" />
				<param name="allowScriptAccess" value="sameDomain" />
				<param name="allowFullScreen" value="true" />
				<!--[if !IE]>-->
				<object type="application/x-shockwave-flash"
					data="FlexMultiSearchPoint.swf" width="1000" height="260">
					<param name="quality" value="high" />
					<param name="bgcolor" value="#EBEBFF" />
					<param name="allowScriptAccess" value="sameDomain" />
					<param name="allowFullScreen" value="true" />
					<!--<![endif]-->
					<!--[if gte IE 6]>-->
					<p>Either scripts and active content are not permitted to run
						or Adobe Flash Player version 10.0.0 or greater is not installed.
					</p>
					<!--<![endif]-->
					<a href="http://www.adobe.com/go/getflashplayer"> <img
						src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif"
						alt="Get Adobe Flash Player" /> </a>
					<!--[if !IE]>-->
				</object>
				<!--<![endif]-->
			</object>
		</noscript>
	</div>
	<div class="div" id="mainDiv" 
		style="width: 1000px; height: auto; border-top-width: 2px; border-top-color: #ff0000;">
		<div id="mainLeftDiv" style="width: 790px; height: auto; margin: 4px;background-color: #EBEBFF;">
			<div id="resultsDiv">
				<%
            int i=0;
				String authorIds = "";
            for (Article a : articles)
            {
            	String authorsString = "";
            	for (Author author : a.getAuthors(conn))
            	{
            		authorsString = authorsString + ", " + author.getName();
            		authorIds = authorIds + ", " + author.getId();
            	}
            	authorsString = authorsString.isEmpty() ? authorsString : authorsString.substring(1);
            	%>
				<div class="articleItem" id="article<%=a.getId()%>"
					style="position:absolute;display:block;width:750px;height:auto;top:<%=650 + 150*i%>px;cursor: pointer;"
					onclick="document.location='article.jsp?&id=<%=a.getId()%>'">
					<h3 style="margin: 0"><%=a.getTitle()%></h3>
					<sub><h4 style="margin: 0"><%=authorsString%><br />publish
							date=<%=a.getPublishDate()%></h4>
					</sub>
					<p style="margin: 0"><%=a.getLeadParagraph()%></p>
				</div>
				<%
            	i++;
            }
            authorIds = authorIds.isEmpty() ? null : authorIds.substring(1);
            %>
			</div>
		</div>
		<div id="rightDiv"
			style="width: 190px; height: 100%; float: right; background-color: #EBEBFF;">
			<div id="categoriesDiv" style="background-color: #EBEBFF;height:auto">
                <span style="color: #3634A6; cursor: pointer"
                    onClick="document.location='category.jsp'"><b>Categories</b>
                </span>
                <ul id="mainCatList"
                    style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
                    <%
                CategoriesList catList = new CategoriesList(conn);
                catList.getCategoriesByArticleIds(articleIdsString,20);
                int size = catList.size();
                int count = 0;
                for (Category c : catList)
                {
                    %><li><span id="<%=c.getId()%>"
                        onClick="document.location='category.jsp?&id=<%=c.getId()%>'"
                        style="cursor: pointer"><%=c.getName()%></span>
                    </li>
                    <%
                }
                %>
                </ul>
            </div>
			<div id="entitiesDiv" style="background-color: #EBEBFF;height:auto">
			   <span style="color: #3634A6; cursor: pointer"
                    onClick="document.location='entitiesGraph.jsp?&ids=<%=articleIdsString%>'"><b>Entities</b></span>
			   <ul id="entities"
					style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
					<%
                EntityList entList = new EntityList(conn);
                entList.getEntitiesByArticleIds(articleIdsString,null,20);
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
			<div id="keywordsDiv" style="background-color: #EBEBFF;height:auto;">
			<span style="color: #3634A6;"><b>Keywords</b></span>
				<ul id="keywords"
					style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
					<%
                KeywordList kwList = new KeywordList(conn);
                kwList.getKeywordsByArticleIds(articleIdsString,20);
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
			<div id="yearsDiv" style="background-color: #EBEBFF;height:auto">
				<span style="color: #3634A6;" onClick="document.location='yearGraph.jsp?&ids=<%=articleIdsString%>'"><b>Years</b></span>
				<ul id="years"
					style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
					<%
					ArrayList<Integer> years = new ArrayList<Integer>();
					years = articles.getAllYears();
					
					for (Integer year : years)
					{
                        %>
                        <li><span id="<%=year%>"onClick="document.location='year.jsp?&year=<%=year%>'"style="cursor: pointer"><%=year%></span>
                        </li>
                        <%
                    }
                    %>
				</ul>
			</div>
			<div id="authorsDiv" style="background-color: #EBEBFF;height:auto">
			     <span style="color: #3634A6;"><b>Authors</b></span>
				<%
                AuthorList auList = new AuthorList(conn);
                auList.getAuthorsByArticleIds(articleIdsString,authorIds);
                if (auList.isEmpty())
                {
                    %><span style="font-style: italic;">No authors exist in DB</span>
				<%
                }
                else
                {
                	%><ul id="authors"
					style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
					<%
                    for (Author a : auList)
                    {
                        %><li><span id="<%=a.getId()%>"
						onClick="document.location='author.jsp?&id=<%=a.getId()%>'"
						style="cursor: pointer"><%=a.getName()%></span>
					</li>
					<%
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
            for(Article a : articles)
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
                "1000", "260", 
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

                var yStart = 360;
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
<%} %>

<% 
if (request.getRemoteAddr().startsWith("66.249."))
{
    conn.close();
}
%>
</html>