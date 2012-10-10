<%@page import="jsppages.JspDocAtlas"%>
<%@page import="db.Article"%>
<%@page import="java.util.HashMap"%>
<%@page import="db.DBConnection"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Document Atlas for Search Results</title>
<script type="text/javascript" src="raphael-min.js"></script>
<script type="text/javascript" src="jquery-1.5.min.js"></script>
<script src="docAtlas/docatlas.js"></script>
<script src="docAtlas/seedrandom.js"></script>
<script src="docAtlas/jquery.mousewheel.min.js"></script>

</head>
<body>
<div id="frameDiv" style="float:none;width:1001px;height: 1000px;margin-left:auto;margin-right:auto;">
	<div
		style="width: 1000px; height: 10px; border: 0px; background-color: #DBDBFF;">
	</div>
	<div id="searchSou1Div"
		style="width: 1000px; height: 20px; border: 0px; background-color: #DBDBFF;color: #A19FFF;">
		<span id="newSearch" onClick="document.location='.'"
			style="cursor: pointer">New Search</span>
	</div>
	<div
		style="width: 1000px; height: 10px; border: 0px; background-color: #DBDBFF;">
	</div>
	<%
        int width = 1000;
        int hight = 1000;
            if (session.getAttribute("conn") == null) 
            {
            	DBConnection dbc = new DBConnection("docAtlas.jsp", request.getRemoteAddr());
                Connection c = dbc.getConnection();
                session.setAttribute("conn", c);
            }
            Connection conn = (Connection) session.getAttribute("conn");
            
            String ids = (session.getAttribute("articleIdsString") == null) ? null : session.getAttribute("articleIdsString").toString();
            if (session.getAttribute("aidmap") != null && request.getParameter("m") != null)
            {
            	HashMap<String,String> aidmap = (HashMap<String,String>)session.getAttribute("aidmap");
            	ids = aidmap.get(request.getParameter("m"));
            }
            System.out.println("articleIdsString: " + ids);
            ids = (ids == null || ids.isEmpty()) ? null : (ids.contains(",") ? ids : null);
            if (ids == null || ids.isEmpty())
            {
            	%>
	<div id="searchDiv"
		style="width: 1000px; height: 602px; border: 0px; background-color: #EBEBFF;color: #A19FFF;">
		<span id="newSearch" onClick="document.location='.'"
			style="cursor: pointer">No search results found to visualize</span>
	</div>
	<%
            }
            else
           {
            	JspDocAtlas pageInfo = new JspDocAtlas(ids, conn);
            %>
	<div id="DocAtlasDiv"
		style="width: 1000px; height: 20px; border: 0px; background-color: #DBDBFF;">
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
                                    var message="articles=<%=ids%>";
                                    
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
	<%
    	}
    %>
    <% 
if (request.getRemoteAddr().startsWith("66.249."))
{
    conn.close();
}
%>
</div>
</body>
</html>