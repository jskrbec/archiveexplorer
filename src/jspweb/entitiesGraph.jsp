<%@page import="java.util.Collections"%>
<%@page import="java.util.Map"%>
<%@page import="db.DBConnection"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="db.Entity"%>
<%@page import="db.EntityList"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<style>
body,html,table {
    font-family: arial, sans-serif;
    font-size: small;
}
div {
    border: 0px solid #8A89BF;
    color: #3634A6;
    background-color: #EBEBFF;
}
</style>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Arbor graph for Search Results</title>
<script type="text/javascript" src="../jquery-1.5.min.js"></script>
<script type="text/javascript" src="arbor/arbor.js"></script>
<script type="text/javascript" src="arbor/arbor-tween.js"></script>
<script type="text/javascript" src="arbor/arbor-graphics.js"></script>
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
	<%
            int width = 1000;
            int hight = 1000;
                if (session.getAttribute("conn") == null) 
                {
                	DBConnection dbc = new DBConnection("entitiesGraph.jsp", request.getRemoteAddr());
                    Connection c = dbc.getConnection();
                    session.setAttribute("conn", c);
                }
                Connection conn = (Connection) session.getAttribute("conn");
                String ids = request.getParameter("ids");
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
                    EntityList entityList = new EntityList(conn);
                    entityList.getEntitiesByArticleIds(ids, null, 100);
                    ArrayList<Entity> tempEntities = new ArrayList<Entity>();
                    tempEntities.addAll(entityList);
                    HashMap<String,ArrayList<String>> entConn = new HashMap<String, ArrayList<String>>();
                    for (Entity e : entityList)
                    {
                    	tempEntities.remove(e);
                    	HashMap<String, Integer> tempNamesValues = new HashMap<String,Integer>();
                    	for (Entity e1 : tempEntities)
                    	{
                    		   for (Integer aid : e.getArticleIds())
                    		   {
                    			   if (e1.getArticleIds().contains(aid))
                    			   {
                    				   Integer value = 0;
                    				   if (tempNamesValues.containsKey(e1.getName()))
                    				   {
                    					   value = tempNamesValues.get(e1.getName());
                    					   tempNamesValues.remove(e1.getName());
                    				   }
                    				   tempNamesValues.put(e1.getName(),value+1);
                    				   //tempNames.add(e1.getName());
                    				   break;
                    			   }
                    		   }
                    	}
                    	ArrayList<Integer> topValues = new ArrayList<Integer>();
                    	topValues.addAll(tempNamesValues.values()); 
                    	Collections.sort(topValues);
                    	Collections.reverse(topValues);
                    	int i = 0;
                    	ArrayList<String> tempNames = new ArrayList<String>();
                    	for (Integer val : topValues)
                    	{
                    		i++;
                    		for(String ent : tempNamesValues.keySet())
                    		{
                    			if(tempNamesValues.get(ent).equals(val))
                    			{
                    				tempNames.add(ent);
                    				tempNamesValues.remove(ent);
                    				break;
                    			}
                    		}
                    		if(i == 4)
                    		{
                    			break;
                    		}
                    	}
                    	entConn.put(e.getName(),tempNames);
                    }
            %>
	<div id="emptyDiv"
		style="width: 1000px; height: 20px; border: 0px; background-color: #DBDBFF;"></div>
	<div
		style="width: 1000px; height: 10px; border: 0px; background-color: #DBDBFF;">
	</div>
	<div id="graphDiv"
		style="width: 1001px; height: 1001px; border: 0px; background-color: #EBEBFF;">
		<canvas id="viewport" width="1000px" height="1000px"></canvas>
	</div>
	<script type="text/javascript"> 
                    (function($){
                        var Renderer = function(canvas){
                            var canvas = $(canvas).get(0)
                            var ctx = canvas.getContext("2d");
                            var particleSystem
                             var sys = null
                              var dom = $(canvas)
                            

							var _vignette = null
							    var selected = null,
							        nearest = null,
							        _mouseP = null;
							        
                            var that = {
                                init:function(system){
                                    // the particle system will call the init function once, right before the first frame is drawn. set up the canvas and pass the canvas size to the particle system save a reference to the particle system for use in the .redraw() loop
                                    particleSystem = system
                                    sys = system
                                    sys.screen({size:{width:dom.width(), height:dom.height()},
                                                padding:[36,60,36,60]})
    
                                    // inform the system of the screen dimensions so it can map coords. if the canvas is resized, screenSize should be called again with the new dimensions
                                    particleSystem.screenSize(canvas.width, canvas.height) 
                                    particleSystem.screenPadding(80) // leave an extra 80px of whitespace per side
            
                                    // set up some event handlers to allow for node-dragging
                                    that.initMouseHandling()
                                },
                                redraw:function(){
                                    // redraw will be called repeatedly during the run whenever the node positions change. new positions for the nodes are at the .p attribute of a given node. p.x & p.y are the coordinates of the particle system rather than the screen. 
                                    //you can either map them to the screen yourself, or use the convenience iterators .eachNode (and .eachEdge) which allow you to step through the actual node objects but also pass an x,y point in the screen's coordinate system
                                    if (particleSystem===null) return

							        ctx.clearRect(0,0, canvas.width, canvas.height)
							        ctx.strokeStyle = "#d3d3d3"
							        ctx.lineWidth = 1
							        ctx.beginPath()
            
                                    particleSystem.eachEdge(function(edge, pt1, pt2){
                                        // edge: {source:Node, target:Node, length:#, data:{}} // pt1:  {x:#, y:#}  source position in screen coords // pt2:  {x:#, y:#}  target position in screen coords

                                        // draw a line from pt1 to pt2
                                        ctx.strokeStyle = "rgba(0,0,0, .1)";
                                        ctx.lineWidth = 1;
                                        ctx.beginPath();
                                        ctx.moveTo(pt1.x, pt1.y);
                                        ctx.lineTo(pt2.x, pt2.y);
                                        ctx.stroke();
                                    })

                                    particleSystem.eachNode(function(node, pt){
                                        var w = ctx.measureText(node.data.label||"").width + 6
                                        var label = node.data.label
								          if (!(label||"").match(/^[ \t]*$/)){
								            pt.x = Math.floor(pt.x)
								            pt.y = Math.floor(pt.y)
								          }else{
								            label = null
								          }
								          
                                          ctx.clearRect(pt.x-w/2, pt.y-7, w,14)

								          // draw the text
								          if (label){
								            ctx.font = "bold 11px Arial"
								            ctx.textAlign = "center"
								            
								            ctx.fillStyle = "#888888"
								
								            ctx.fillText(label||"", pt.x, pt.y+4)
								          }
                                    })       
                                },
                                
                                resize:function(){
							        var w = $(window).width(),
							            h = $(window).height();
							        canvas.width = w; canvas.height = h // resize the canvas element to fill the screen
							        particleSystem.screenSize(w,h) // inform the system so it can map coords for us
							        that.redraw()
							      },
							      
                                initMouseHandling:function(){
                                    selected = null;
                                    nearest = null;
                                    var dragged = null;    // no-nonsense drag and drop (thanks springy.js)
        
                                    // set up a handler object that will initially listen for mousedowns then for moves and mouseups while dragging
                                    var handler = {
                                    moved:function(e){
								            var pos = $(canvas).offset();
								            _mouseP = arbor.Point(e.pageX-pos.left, e.pageY-pos.top)
								            nearest = sys.nearest(_mouseP);
								
								            if (!nearest.node) return false
								
								            if (nearest.node.data.shape!='dot'){
								              selected = (nearest.distance < 50) ? nearest : null
								              if (selected){
								                 dom.addClass('linkable')
								                 window.status = selected.node.data.link.replace(/^\//,"http://"+window.location.host+"/").replace(/^#/,'')
								              }
								              else{
								                 dom.removeClass('linkable')
								                 window.status = ''
								              }
								            }else if ($.inArray(nearest.node.name, ['arbor.js','code','docs','demos']) >=0 ){
								              if (nearest.node.name!=_section){
								                _section = nearest.node.name
								                that.switchSection(_section)
								              }
								              dom.removeClass('linkable')
								              window.status = ''
								            }
								            
								            return false
								          },
                                         clicked:function(e){
									            var pos = $(canvas).offset();
									            _mouseP = arbor.Point(e.pageX-pos.left, e.pageY-pos.top)
									            nearest = dragged = sys.nearest(_mouseP);
									            
									            if (nearest && selected && nearest.node===selected.node){
									              var link = selected.node.data.link
									              if (link.match(/^#/)){
									                 $(that).trigger({type:"navigate", path:link.substr(1)})
									              }else{
									                 window.location = link
									              }
									              return false
									            }
									            
									            
									            if (dragged && dragged.node !== null) dragged.node.fixed = true
									
									            $(canvas).unbind('mousemove', handler.moved);
									            $(canvas).bind('mousemove', handler.dragged)
									            $(window).bind('mouseup', handler.dropped)
									
									            return false
									          },
									          dragged:function(e){
									            var old_nearest = nearest && nearest.node._id
									            var pos = $(canvas).offset();
									            var s = arbor.Point(e.pageX-pos.left, e.pageY-pos.top)
									
									            if (!nearest) return
									            if (dragged !== null && dragged.node !== null){
									              var p = sys.fromScreen(s)
									              dragged.node.p = p
									            }
									
									            return false
									          },
									
									          dropped:function(e){
									            if (dragged===null || dragged.node===undefined) return
									            if (dragged.node !== null) dragged.node.fixed = false
									            dragged.node.tempMass = 1000
									            dragged = null;
									            // selected = null
									            $(canvas).unbind('mousemove', handler.dragged)
									            $(window).unbind('mouseup', handler.dropped)
									            $(canvas).bind('mousemove', handler.moved);
									            _mouseP = null
									            return false
									          }

                                 }      
                                 // start listening
                                 $(canvas).mousedown(handler.clicked);
                             },
                        }
                        return that;
                    }

                    $(document).ready(function(){
                        var sys = arbor.ParticleSystem(1000, 10, 0.5,0); // create the system with sensible repulsion/stiffness/friction
                        sys.parameters({gravity:true}); // use center-gravity to make the graph settle nicely (ymmv)
                        sys.renderer = Renderer("#viewport"); // our newly created renderer will have its .init() method called shortly by sys...
    
                        sys.graft({
                            nodes:{
                                <%
                                  for (Entity e: entityList)
                            	  {  
                                    %>'<%=e.getName()%>':{'label':'<%=e.getName()%>','link':'entity.jsp?&entity=<%=e.getName()%>&id=<%=e.getId()%>'},<%
                                  }
                                %>
                                'bla':{'label':''}
                            }, 
                            edges:{
                                <%
                                for (String entity : entConn.keySet())
                                {
                                %>
                                    '<%=entity%>':{
                                        <%
                                        for (String connEntity : entConn.get(entity))
                                        {
                                            %>'<%=connEntity%>':{},<%
                                        }
                                        %> 
                                        '<%=entity%>':{}
                                    },
                                <%
                                }
                                %>
                                'bla':{}
                            }
                        })
                    })
                })(this.jQuery)
        </script>
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