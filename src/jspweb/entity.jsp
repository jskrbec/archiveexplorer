<%@page import="db.Article"%>
<%@page import="java.util.ArrayList"%>
<%@page import="db.Author"%>
<%@page import="db.Keyword"%>
<%@page import="db.Entity"%>
<%@page import="documentatlas.ArticlePoints"%>
<%@page import="db.Category"%>
<%@page import="jsppages.JspEntity"%>
<%@page import="java.sql.Connection"%>
<%@page import="db.DBConnection"%>
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
    <%
        if (session.getAttribute("conn") == null)
        {
            DBConnection dbc = new DBConnection("entity.jsp", request.getRemoteAddr());
            Connection c = dbc.getConnection();
          session.setAttribute("conn",c);
        }
        Connection conn = (Connection)session.getAttribute("conn");
        
        String id = request.getParameter("id");
        JspEntity pageInfo = new JspEntity(id,conn);
    %>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><%=pageInfo.getDbrLink(true)%></title>
    <script type="text/javascript" src="jquery-1.5.min.js"></script>
    <script type="text/javascript" src="raphael-min.js"></script>
    <script src="docAtlas/docatlas.js"></script>
    <script src="docAtlas/seedrandom.js"></script>
    <script src="docAtlas/jquery.mousewheel.min.js"></script>
</head>
<body>
    <div id="frameDiv" style="float:none;width:1002px;margin-left:auto;margin-right:auto;">
        <div style="width: 1000px; height: 10px; border: 0px; background-color: #DBDBFF;">
        </div>
        <div id="searchSou1Div" style="width: 1000px; height: 20px; border: 0px; background-color: #DBDBFF;">
            <span id="newSearch" onClick="document.location='.'" style="cursor: pointer;margin: 10px;">New Search</span> 
        </div>
        <div style="width: 1000px; height: 10px; border: 0px; background-color: #DBDBFF;">
        </div>
        <div id="pageDiv" style="width: 1001px; border: 0px;">
            <div id="nameDiv" style="vertical-align: top; float: left; width: 100%; height: 50px; border: 0px;color: #3634A6;background-color: #FFFFFF;margin-top: 1px;">
                <sub><%=pageInfo.getType()%></sub>
                <h2 style="margin: 0; margin-left: 20px;"><%=pageInfo.getName()%> (<%=pageInfo.getDbrLink(true)%>)</h2>
            </div>
            <div class="div" id="mainDiv" style="width: 1000px; height: auto; border-top-width: 2px; border-top-color: #ff0000;">
                <div id="mainLeftDiv" style="width: 800px; height: auto; margin-top: 5px;background-color: #FCFCFF;">
                    <div id="resultsDiv" style="width: 800px;">
                        <div style="vertical-align: top; float: left;width:800px;height:auto; background-color: #EBEBFF;">
                            <div id="descriptionDiv" style="vertical-align: top; float: left;width:790px;height:auto;margin: 10px;background-color: #EBEBFF;">
                                <div style="height:100%; width:20%;"><img src="<%=pageInfo.getPicLink()%>"></div>
                                <div style="height:90%;width:70%;float: right;cursor:text">
                                    <span style="cursor:text"><%=pageInfo.getAbstract()%></span>
                                </div>
                            </div>
                            <div style="width:100%; height:10px;background-color: #EBEBFF;"></div>
                            <div id="todoDiv" style="vertical-align: top; float: left;display:block;width:750px;height:auto;">
                                <%=pageInfo.getAdditionalInfo()%>
                                <a href="http://en.wikipedia.org/wiki/<%=pageInfo.getDbrLink(false)%>">More</a>
                            </div>
                            <div style="width:100%; height:10px;background-color: #FFFFFF;"></div>
                        </div>
                        <div id="categoriesDiv" style="vertical-align: top; float: left;display:block;width:750px;height:auto;background-color: #FCFCFF;">
                            <span style="color: #3634A6; cursor: pointer"><b>Related categories:</b></span>
                            <ul>
                            <%
                            if (!pageInfo.getCategories().isEmpty())
                            {
                                for (Category c : pageInfo.getCategories())
                                { 
                                    String fullName = (c.getParentPath() == null ? "" : c.getParentPath()) + (c.getName() == null ? "" : "/" + c.getName());
                            %>
                            <li id="<%=c.getId()%>"onClick="document.location='category.jsp?&id= <%=c.getId()%>'" style="cursor: pointer"><%=fullName%></li>
                            <%;
                                }
                            }
                            %>
                            </ul>
                        </div>
                        <div style="width:100%; height:10px;background-color: #FFFFFF;">
                        </div>
                        <div id="docAtlasJSDiv" style="width: 600px; height: 600px; border: 0px; background-color: #EBEBFF; overflow: auto;float: left;" onload="loadDocAtlas()">
                            <div id="loading" style="width: 20px; height: 20px;float:none;margin-left:60%;margin-right:auto;margin-top:45%;margin-bottom:auto;">
                                <img id="loader" src="ajax-loader.gif"/>
                            </div>
                            <script type="text/javascript">                             
                            $(document).ready(function(){
                                loadDocAtlas();
                            });                                
                                function loadDocAtlas()
                                {
                                    var counter =0;
                                    var message="entity=<%=id%>";
                                    
                                    var articles = new Array();
                                    
                                    <%for (Article a: pageInfo.getArticles100())
                                    {%>
                                        var art = {id:<%=a.getId()%>,title:"<%=a.getTitle()%>",leadp:"<%=a.getLeadParagraph()%>"};
                                        articles[<%=a.getId()%>]=art;
                                        articles.push({id:<%=a.getId()%>,title:"<%=a.getTitle()%>",leadp:"<%=a.getLeadParagraph()%>"});
                                    <%}%>
	                                 $.ajax({
	                                    type: 'POST',
	                                    url: 'vizmap',
	                                    data: {bla:message},
	                                    success: function(data) {
	                                        $('#loading').remove();
	                                        runDocAtlas(data,counter,articles);
	                                    },
	                                    dataType:'json'
	                                });
                                }
                                
                                function runDocAtlas(data,count,articles)
                                {
	                                var keywords = new Array(); //coordinates of keywords in original size
	                                for (var i=0;i<data.kws.length;i++)
                                    {
                                       var object = data.kws[i];
                                       if (object != null)
                                       {
                                           keywords.push({x:object.x, y:object.y,word:object.name});
                                       }
                                    }    
	                                var documents = new Array();     //coordinates of articles in original size
	                                
	                                for (var i=0;i<data.points.length;i++)
	                                {
	                                   var object = data.points[i];
	                                   if (object != null)
	                                   {
	                                       var article = articles[object.name];
	                                       if (article!=null)
	                                       {
	                                           documents.push({x:object.x, y:object.y,aid:article.id, title:article.title, leadp:article.leadp});
	                                       }
	                                   }
	                                }
	                                var myatlas = {"keywords": keywords, "documents": documents};
	                                $(document).ready(function(){
	                                    var docAtlas = new DocumentAtlas('docAtlasJSDiv', myatlas, 600, 600, 
	                                        function(words){
	                                            $('#keywords').html('');
	                                            for(var i = 0; i < words.length; i++){
	                                                $('#keywords').append($('<div style="width: 190px;height: 20px;border: 0px;">' + words[i] + '</div>'))
	                                            }
	                                        },
	                                        function(article){
	                                            $('#content').html('');
	                                            $('#content').append($('<span style="width:700px; border: 0px;cursor: pointer" onClick="document.location=\'article.jsp?&id=' + article.aid + '\'"><b>' + article.title + '</b><br><br>' + article.leadp + '<br></span>'))
	                                        });  
	                                    });
                                    }
                        </script>
                    </div>
                    <div id="keywords" style="width: 200px; height: 600px;border: 0px; background-color: #EBEBFF;color: #3634A6;">
                    </div>
                    <div style="width: 800px; height: 10px;border: 0px; background-color: #FCFCFF;color: #3634A6;">
                    </div>
                    <div id="content" style="width: 800px; height: 200px;border: 0px; background-color: #FCFCFF;color: #3634A6;">
                    </div>
                </div>
            </div>
            <div id="rightDiv" style="width: 190px; height: 100%; float: right; background-color: #FFFFFF;margin-top: 5px;">
                <div id="entitiesDiv" style="background-color: #FCFCFF;height:auto">
                <span style="color: #3634A6; cursor: pointer" onClick="document.location='entitiesGraph.jsp?&ids=<%=pageInfo.getArticleIds()%>'"><b>Entities</b></span>
                <ul id="entities" style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
                    <%
                    if (!pageInfo.getEntities().isEmpty())
                    {
                        for (Entity e : pageInfo.getEntities())
                        {
                            %><li>
                                <span id="<%=e.getId()%>" onClick="document.location='entity.jsp?&entity=<%=e.getName()%>&id=<%=e.getId()%>'" style="cursor: pointer"><%=e.getName()%></span>
                            </li>
                            <%
                        }
                     }
                    %>
                </ul>
            </div>
            <div id="keywordsDiv" style="background-color: #FCFCFF;height:auto;">
                <span style="color: #3634A6;"><b>Keywords</b></span>
                <ul id="keywords" style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
                    <%
                    if (!pageInfo.getKeywords().isEmpty())
                    {
                        for (Keyword k : pageInfo.getKeywords())
                        {
                            %><li>
                                <span id="<%=k.getId()%>" onClick="document.location='keyword.jsp?&kw=<%=k.getName()%>&id=<%=k.getId()%>'" style="cursor: pointer"><%=k.getName()%></span>
                            </li>
                            <%
                        }
                    }
                    %>
                </ul>
            </div>
            <div id="yearsDiv" style="background-color: #FCFCFF;height:auto">
                <span style="color: #3634A6;" onClick="document.location='yearGraph.jsp?&ids=<%=pageInfo.getArticleIds()%>'"><b>Years</b></span>
                <ul id="years" style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
                    <%
                    if (!pageInfo.getYears().isEmpty())
                    {
                        for (Integer year : pageInfo.getYears())
                        {
                            %>
                            <li><span id="<%=year%>"onClick="document.location='year.jsp?&year=<%=year%>'"style="cursor: pointer"><%=year%></span></li>
                            <%
                        }
                    }
                    %>
                </ul>
            </div>
            <div id="authorsDiv" style="background-color: #FCFCFF;height:auto">
                 <span style="color: #3634A6;"><b>Authors</b></span>
                <%
                
                if (!pageInfo.getAuthors().isEmpty())
                {
                    %><ul id="authors" style="margin-bottom: 10px; margin-top: 0; list-style-type: none; padding: 0px; padding-left: 20px;">
                    <%
                    for (Author a : pageInfo.getAuthors())
                    {
                        %>
                        <li><span id="<%=a.getId()%>" onClick="document.location='author.jsp?&id=<%=a.getId()%>'" style="cursor: pointer"><%=a.getName()%></span></li>
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
</div>
</body>
</html>