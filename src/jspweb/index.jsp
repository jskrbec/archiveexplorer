<%@page import="java.util.HashMap"%>
<%@page import="db.TopByYear"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.sql.Date"%>
<%@page import="db.DBConnection"%>
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
th,li {
	color: #3634A6;
	cursor: pointer;
}
tr {
	color: #A19FFF;
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
<title>Archive Explorer</title>
<script type="text/javascript" src="jquery-1.5.min.js"></script>
<script src="history.js"></script>
</head>
<body>
<div id="frameDiv" style="float:none;width:1001px;margin-left:auto;margin-right:auto;">
	<%
        //System.out.println("ae session: " + session.getId());
        if (session.getAttribute("conn") == null)
        {
            DBConnection dbc = new DBConnection("index.jsp", request.getRemoteAddr());
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
	<div id="searchSou1Div" style="width: 1000px; height: 40px; border: 0px; background-color: #3634A6;text-align:center;vertical-align: middle;">
		<span style="align:center;height: 100%;font-family:Courier;font-size:40px;color: #FCFCFF;font-weight: bold;">ARCHIVE EXPLORER</span>
	</div>
	<div id="searchDiv" style="width: 1000px; height: 280px; border: 0px; background-color: #EBEBFF;">
		<div style="width: 10px; height: 280px; border: 0px; background-color: #FCFCFF;">
		</div>
		<div id="formDiv" style="width: 400px; height: 100%; border: 0px; border-bottom: 2px #ff0000;background-color: #FCFCFF;">
			<div style="width: 100%; height: 9%; border: 0px;"></div>
			<form id="searchForm" style="width: 100%; height: 90%; border: 0px;">
			    <span style="font-weight: bold;">Search by</span>
				<div id="searchAllDiv" style="width: 100%; height: 20px; border: 0px;">
					<div style="width: 40px; height: 100%; border: 0px;"></div>
					<div style="width: 80px; height: 100%; border: 0px;"> all fields:</div>
					<div style="width: 100px;">
						<input type="text" name="searchText" id="searchText" />
					</div>
				</div>
				<div id="searchPersonDiv" style="height: 20px; border: 0px;">
				    <div style="width: 40px; height: 100%; border: 0px;"></div>
					<div style="width: 80px; border: 0px;"> person:</div>
					<div style="width: 100px;">
						<input type="text" name="searchPerson" id="searchPerson" />
					</div>
				</div>
				<div id="searchOrgDiv" style="height: 20px; border: 0px;">
				    <div style="width: 40px; height: 100%; border: 0px;"></div>
					<div style="width: 80px; border: 0px;"> organization:</div>
					<div style="width: 100px;">
						<input type="text" name="searchOrgan" id="searchOrgan" />
					</div>
				</div>
				<div id="searchLocDiv" style="height: 20px; border: 0px;">
				    <div style="width: 40px; height: 100%; border: 0px;"></div>
					<div style="width: 80px; border: 0px;"> location:</div>
					<div style="width: 100px;">
						<input type="text" name="searchLocat" id="searchLocat" />
					</div>
				</div>
				<div id="searchEntDiv" style="height: 20px; border: 0px;">
					<div style="width: 40px; height: 100%; border: 0px;"></div>
					<div style="width: 80px; border: 0px;"> entity:</div>
					<div style="width: 100px;">
						<input type="text" name="searchEntity" id="searchEntity" />
					</div>
				</div>
				<div id=searchKwDiv "" style="height: 20px; border: 0px;">
					<div style="width: 40px; height: 100%; border: 0px;"></div>
					<div style="width: 80px; border: 0px;"> keyword:</div>
					<div style="width: 100px;">
						<input type="text" name="searchKeyword" id="searchKeyword" />
					</div>
				</div>
				<div id="searchAutDiv" style="height: 20px; border: 0px;">
					<div style="width: 40px; height: 100%; border: 0px;"></div>
					<div style="width: 80px; border: 0px;"> author:</div>
					<div style="width: 100px;">
						<input type="text" name="searchAuthor" id="searchAuthor">
					</div>
				</div>
				<div id="searchYearDiv" style="height: 20px; border: 0px;">
					<div style="width: 40px; height: 100%; border: 0px;"></div>
					<div style="width: 80px; border: 0px;"> date:</div>
					<div style="width: 100px">
						<input type="date" autocomplete="on" name="searchDate" id="searchDate">
					</div>
				</div>
				<div id="searchCatDiv" style="height: 20px; border: 0px;">
					<div style="width: 40px; height: 100%; border: 0px;"></div>
					<div style="width: 80px; border: 0px;"> category:</div>
					<div style="width: 100px;">
						<input type="text" name="searchCategory" id="searchCategory" />
					</div>
				</div>
				<div style="float: right; width: 100%; height: 9%; border-right: 20px; right: 10px;">
					<input style="float: right;" type="submit" value="Search" />
				</div>
			</form>
		</div>
		<div style="width: 30px; height: 100%; border: 0px;background-color: #FCFCFF"></div>
		<div style="width: 30px; height: 100%; border: 0px; background-color: #EBEBFF;"></div>
		<div id="allCatDiv"
			style="width: 49%; height: 100%; border: 0px; background-color: #EBEBFF;">
			<div style="width: 100%; height: 4%; background-color: #EBEBFF;"></div>
			<div style="width: 100%; height: 10%; background-color: #EBEBFF;">
				<span style="color: #3634A6; cursor: pointer"
					onClick="document.location='category.jsp'"><b>Main
						categories:</b>
				</span>
			</div>
			<div style="width: 30%; height: 80%; background-color: #EBEBFF;">
				<ul id="mainCatList" style="list-style-type: none;background-color: #EBEBFF;">
					<%
                CategoriesList catList = new CategoriesList(conn);
                catList.getCategoriesByParentId(0);
                int size = catList.size();
                int count = 0;
                for (Category c : catList)
                {
                	%><li><span id="<%=c.getId()%>"
						onClick="document.location='category.jsp?&id=<%=c.getId()%>'"
						style="cursor: pointer"><%=c.getName()%></span>
					</li>
					<%
                    if ((size > 12) && (count == (size/2)))
                    {
                    	%>
				</ul>
			</div>
			<div style="width: 40%; height: 95%; background-color: #EBEBFF;">
				<ul style="list-style-type: none; padding: 0px;background-color: #EBEBFF;">
					<%
                    }
                	count++;
                }
                %>
				</ul>
			</div>
		</div>
	</div>
	<div class="div" id="mainDiv" style="width: 1000px; height: auto; border-top-width: 2px; border-top-color: #ff0000;">
	<%
                    int maxYear = 2007;
                    int minYear = 1987;
                    TopByYear top = new TopByYear(conn);
                    top.loadData();
                    HashMap<Integer,ArrayList<Entity>> peopleByYear =  top.getPeopleList();
                    HashMap<Integer,ArrayList<Entity>> locationsByYear =  top.getLocationList();
                    HashMap<Integer,ArrayList<Entity>> organizationsByYear =  top.getEntitiesList();
                    HashMap<Integer,ArrayList<Keyword>> keywordsByYear =  top.getKeywordsList();
                    HashMap<Integer,ArrayList<Author>> authorsByYear =  top.getAuthorsList();
        %>
        <div style="width: 1000px; height: 30px; border: 0px; background-color: #3634A6;color:#FCFCFF;">
            <div style="width: 10px; height: 30px; border: 0px;background-color: #3634A6;"></div>
            <span style="font-weight: bold;"><b> Year </b></span>
            <span id="range"><%=minYear%> onClick="document.location='year.jsp?&year=<%=minYear%>'"</span>
            <input type="range" min="<%=minYear%>" max="<%=maxYear%>" value="<%=minYear%>" step="1" id="slider" style="width:90%" onchange="showValue(this.value)"/>
        </div>
        <div style="width: 1000px; height: 10px; border: 0px; "></div>
		<div style="width: 10px;height:500px;">
        </div>
		<div id="peopleMainDiv" style="width: 24%;height:auto;">
			<span style="color: #3634A6; cursor: pointer" onClick="document.location='entity.jsp'"><b>People</b></span>
			     <ul id="people" style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">                 
				</ul>
				
		</div>
		<div id="entitiesMainDiv" style="width: 24%;height:auto;">
		<span style="color: #3634A6;"><b>Locations</b></span>
                 <ul id="locations" style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
                 </ul>
                 <span style="color: #3634A6; cursor: pointer" onClick="document.location='entity.jsp'"><b>Organizations</b></span>
                 <ul id="organizations"
                    style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
                 </ul>
        </div>
		<div id="keywordsMainDiv" style="width: 24%;height:auto;">
			    <span style="color: #3634A6;"><b>Keywords</b></span>
				<ul id="keywords"
					style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
				</ul>
		</div>
		<div id="authorsMainDiv" style="width: 24%;height:auto;">
			<span style="color: #3634A6;"><b>Authors</b></span>
				<ul id="authors"
					style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
				</ul>
		</div>
	</div>
 <script>
var year = new Array();
<%for (Integer year : peopleByYear.keySet())
  {
    %>var people = new Array();<%
    int i =0;
    for (Entity entIdName : peopleByYear.get(year))
    {
    	if (i < 15)
        %>
          people.push({'id':'<%=entIdName.getId()%>','name':'<%=entIdName.getName()%>'});
        <%
        i++;
    }
    %>
       year.push({'year':'<%=year%>','people':people});
    <%
  }%>
var peopleByYears = new Array();

for (y in year)
{
    var thisYear =year[y].year; 
    var prs = year[y].people;
    var ul = "";
    for (i in prs)
    {
        ul = ul.concat("<li><span id=\"e" + prs[i].id + "\" onClick=\"document.location=\'entity.jsp?&entity=" + prs[i].name + "&id=" + prs[i].id + "\'\" style=\"cursor: pointer\">" + prs[i].name + "</span></li>");
    }
    peopleByYears.push({'year':thisYear,'ent':ul});
}

year = new Array();
<%for (Integer year : locationsByYear.keySet())
  {
    %>var entities = new Array();<%
    int i=0;
    for (Entity entIdName : locationsByYear.get(year))
    {
    	if (i < 15)
    	{
        %>
          entities.push({'id':'<%=entIdName.getId()%>','name':'<%=entIdName.getName()%>'});
        <%
    	}
    	i++;
    }
    %>
       year.push({'year':'<%=year%>','entities':entities});
    <%
  }%>
var locationsByYears = new Array();
for (y in year)
{
    var thisYear =year[y].year; 
    var ents = year[y].entities;
    var ul = "";
    for (i in ents)
    {
        ul = ul.concat("<li><span id=\"e" + ents[i].id + "\" onClick=\"document.location=\'entity.jsp?&entity=" + ents[i].name + "&id=" + ents[i].id + "\'\" style=\"cursor: pointer\">" + ents[i].name + "</span></li>");
    }
    locationsByYears.push({'year':thisYear,'ent':ul});
}
year = new Array();
<%for (Integer year : organizationsByYear.keySet())
  {
	%>var entities = new Array();<%
	for (Entity entIdName : organizationsByYear.get(year))
    {
		%>
		  entities.push({'id':'<%=entIdName.getId()%>','name':'<%=entIdName.getName()%>'});
		<%
    }
	%>
	   year.push({'year':'<%=year%>','entities':entities});
	<%
  }%>
var entitiesByYears = new Array();
for (y in year)
{
    var thisYear =year[y].year; 
    var ents = year[y].entities;
    var ul = "";
    for (i in ents)
    {
        ul = ul.concat("<li><span id=\"e" + ents[i].id + "\" onClick=\"document.location=\'entity.jsp?&entity=" + ents[i].name + "&id=" + ents[i].id + "\'\" style=\"cursor: pointer\">" + ents[i].name + "</span></li>");
    }
    entitiesByYears.push({'year':thisYear,'ent':ul});
}
year = new Array();
<%for (Integer year : keywordsByYear.keySet())
  {
    %>var keywords = new Array();<%
    for (Keyword kwIdName : keywordsByYear.get(year))
    {
        %>
          keywords.push({'id':'<%=kwIdName.getId()%>','name':'<%=kwIdName.getName()%>'});
        <%
    }
    %>
       year.push({'year':'<%=year%>','keywords':keywords});
    <%
  }%>
var keywordsByYears = new Array();
for (y in year)
{
    var thisYear =year[y].year; 
    var kws = year[y].keywords;
    var ul = "";
    for (i in kws)
    {
        ul = ul.concat("<li><span id=\"k" + kws[i].id + "\" onClick=\"document.location=\'keyword.jsp?&kw=" + kws[i].name + "&id=" + kws[i].id + "\'\" style=\"cursor: pointer\">" + kws[i].name + "</span></li>");
    }
    keywordsByYears.push({'year':thisYear,'kw':ul});
}
year = new Array();
<%for (Integer year : authorsByYear.keySet())
  {
    %>var authors = new Array();<%
    for (Author auIdName : authorsByYear.get(year))
    {
        %>
          authors.push({'id':'<%=auIdName.getId()%>','name':'<%=auIdName.getName()%>'});
        <%
    }
    %>
       year.push({'year':'<%=year%>','authors':authors});
    <%
  }%>
var authorsByYears = new Array();
for (y in year)
{
    var thisYear =year[y].year; 
    var auts = year[y].authors;
    var ul = "";
    for (i in auts)
    {
        ul = ul.concat("<li><span id=\"a" + auts[i].id + "\" onClick=\"document.location=\'author.jsp?&id=" + auts[i].id + "\'\" style=\"cursor: pointer\">" + auts[i].name + "</span></li>");
    }
    authorsByYears.push({'year':thisYear,'aut':ul});
}

function showValue(newYear)
{
    document.getElementById("range").innerHTML=newYear;
    document.getElementById("range").onclick=function(){
        document.location='year.jsp?&year=' + newYear;
    }
    for (x in peopleByYears)
    {
        if (peopleByYears[x].year == newYear)
        {
            document.getElementById("people").innerHTML = peopleByYears[x].ent;
        }
    }
    for (x in locationsByYears)
    {
        if (locationsByYears[x].year == newYear)
        {
            document.getElementById("locations").innerHTML = locationsByYears[x].ent;
        }
    } 
    for (x in entitiesByYears)
    {
        if (entitiesByYears[x].year == newYear)
        {
            document.getElementById("organizations").innerHTML = entitiesByYears[x].ent;
        }
    }
    for (y in keywordsByYears)
    {
        if (keywordsByYears[y].year == newYear)
        {
            document.getElementById("keywords").innerHTML = keywordsByYears[y].kw;
        }
    }
    for (z in authorsByYears)
    {
        if (authorsByYears[z].year == newYear)
        {
            document.getElementById("authors").innerHTML = authorsByYears[z].aut;
        }
    }
}
window.onload = function () {
    showValue(<%=minYear%>);
}
</script>
</div>
</body>

<script>    

    $("#searchForm").submit(function() {
        //alert('blablabla');
            var params = "";
            if ($("#searchText").val())
            {
                params = params + '&text=' + $("#searchText").val();
            }
            if ($("#searchPerson").val())
            {
                params = params + '&person=' + $("#searchPerson").val();
            }
            if ($("#searchOrgan").val())
            {
                params = params + '&org=' + $("#searchOrgan").val();
            }
            if ($("#searchLocat").val())
            {
                params = params + '&loc=' + $("#searchLocat").val();
            }
            if ($("#searchEntity").val())
            {
                params = params + '&entity=' + $("#searchEntity").val();
            }
            if ($("#searchKeyword").val())
            {
                params = params + '&kw=' + $("#searchKeyword").val();
            }
            if ($("#searchAuthor").val())
            {
                params = params + '&author=' + $("#searchAuthor").val();
            }
            if ($("#searchDate").val())
            {
                params = params + '&date=' + $("#searchDate").val();
            }
            if ($("#searchCategory").val())
            {
                params = params + '&cat=' + $("#searchCategory").val();
            }
            document.location = 'results.jsp?' + params;
                return false;
        });     
 </script>
 
 <% 
if (request.getRemoteAddr().startsWith("66.249."))
{
    conn.close();
}
%>
</html>