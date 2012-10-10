function DocumentAtlas(canvasId, data, width, height, keywordSelectCallback, getArticleContent){
	this.id = canvasId;
	this.data = data;
	this.width = width;
	this.height = height;
	this.keywordSelectCallback = keywordSelectCallback;
	this.getArticleContent = getArticleContent;
	var container = document.getElementById(this.id);
	$(container).css('background-color', 'rgb(54,52,166)'); //rgb(28,58,130)
	//$(container).css('background-color', '#333');
	$(container).css('width', this.width + 'px');
	$(container).css('height', this.height + 'px');
	$(container).css('position', 'relative');
	$(container).append($('<canvas id="' + this.id + '_canvas" width="' + this.width + '" height="' + this.height + '"></canvas>'));
	/*this.xx = h337.create({"element":container, 
	"radius":80, "visible":true,	
	"gradient":{
		0.0:"rgb(28,58,130)",
		0.1:"rgb(38,73,166)",
		0.2:"rgb(43,81,183)",
		0.3:"rgb(53,96,208)",
		0.4:"rgb(84,120,216)",
		0.5:"rgb(112,142,222)",
		0.6:"rgb(136,162,227)",
		0.7:"rgb(160,181,233)",
		0.8:"rgb(184,199,239)",
		0.9:"rgb(208,218,244)",
		1.0:"rgb(222,230,248)"
	}});*/
	this.canvas = document.getElementById(this.id + '_canvas');
	this.context = this.canvas.getContext('2d');
	this.center = {x:0.5, y:0.5};
	this.minX = 0;
	this.maxX = 1;
	this.minY = 0;
	this.maxY = 1;
	this.zoomLevel = 1;
	this.maxKeywords = (this.width * this.height) / 2000;
	this.maxDocuments = this.data.documents.length;
	//alert("maxDocs: " + this.maxDocuments);
	this.dragEnabled = true;		
	this.tooltipExist = false;
	
	this.draw();
	this.initControls();
	this.initMousewheelEvents();
	this.initDragCanvas();
	this.initKeywordSelect();
	this.initMouseClickEvent();
}

DocumentAtlas.prototype.getKeywordsCloseTo = function(x, y, k){
	// x and y are scaled to the interval [0, 1]
	var kwlist = [];
	for(var i=0; i < this.data.keywords.length; i++){
		var kw = this.data.keywords[i];
		var dist = Math.sqrt((x - kw.x)*(x - kw.x) + (y - kw.y)*(y - kw.y))
		kwlist.push({'dist':dist, word:kw.word});
	}
	
	kwlist.sort(function(a, b){
		return a.dist - b.dist;
	});
	
	var wordlist = [];
	for(var i = 0; i < Math.min(k, kwlist.length); i++){
		wordlist.push(kwlist[i].word);
	}
	
	return wordlist;
}

DocumentAtlas.prototype.getDocOnPos = function(x, y){
	// x and y are scaled to the interval [0, 1]
	for(var i=0; i < this.data.documents.length; i++){
		doc = this.data.documents[i];
		if ((Math.abs(x - doc.x) < 0.002) && (Math.abs(y - doc.y) < 0.002))
		{
			return doc;
		}
	}
	
	return null;
}

DocumentAtlas.prototype.drawHeatmap = function() {	
	Math.seedrandom('the life of the wife was ended by the knife');
	datapoints = []
	// preselect datapoints
	for(var k = 0; k < this.data.documents.length; k++){
		var doc = this.data.documents[k];
		if(doc.x >= this.minX && doc.x <= this.maxX && doc.y >= this.minY && doc.y <= this.maxY){			
			datapoints.push(doc);
		}
	}
	var treshold = Math.min(200, (this.width / 4)) / datapoints.length;
	var n = 0;
	for(var k = 0; k < datapoints.length; k++){
		r = Math.random();
		if(r > treshold){
			continue;
		}
		n += 1;
		var x = datapoints[k].x;
		var y = datapoints[k].y;
		
		var scaledX = ((x - this.minX) / (this.maxX - this.minX)) * this.width;
		var scaledY = ((y - this.minY) / (this.maxY - this.minY)) * this.height;		
		
		this.context.beginPath();
		this.context.arc(scaledX, scaledY, this.width / 2, 0, Math.PI * 2);
		this.context.closePath();
		
		grd = this.context.createRadialGradient(scaledX, scaledY, 1, scaledX, scaledY, this.width + this.width * 0.25);
				
		var maxAlpha = 0.09;
		var minAlpha = 0;
		var levels = 4;
		for(var i = 0; i <= levels; i++){
			var alpha = maxAlpha - (i * (maxAlpha - minAlpha)/levels);
			grd.addColorStop(i * (maxAlpha - minAlpha)/levels, 'rgba(255, 255, 255, ' + alpha + ')');	
		}
		
		this.context.fillStyle = grd;
		
		this.context.fill();			
	}	
}

DocumentAtlas.prototype.draw = function() {
	this.context.clearRect(0, 0, this.width, this.height);
	
	//draw heatmap
	this.drawHeatmap();
	
	//draw keywords	
	this.context.fillStyle = '#FFFFFF';
	var keywordsDrawn = 0;
	//TODO if show kws
	var kwsPainted = new Array();
	for(var i = 0; i < this.data.keywords.length && keywordsDrawn < this.maxKeywords; i++){
		var kw = this.data.keywords[i];
		if(kw.x >= this.minX && kw.x <= this.maxX && kw.y >= this.minY && kw.y <= this.maxY){
			var scaledX = ((kw.x - this.minX) / (this.maxX - this.minX)) * this.width;
			var scaledY = ((kw.y - this.minY) / (this.maxY - this.minY)) * this.height;
			
			//make sure keywords don't cover each other
			var drawPoint = true;
            for(point in kwsPainted)
            {
                var pointx = parseInt(kwsPainted[point].x);
                var pointy = parseInt(kwsPainted[point].y);
                
                if (((pointx - 40) < scaledX) && (scaledX < (pointx + 40)) && ((pointy-10) < scaledY) && (scaledY < (pointy + 10)))
                {
                    drawPoint = false;
                    break;
                }
            }
            if (drawPoint)
            {
            	this.context.fillText(kw.word, scaledX, scaledY);
            	kwsPainted.push({x:scaledX, y:scaledY});
            	keywordsDrawn ++;
            }
		}
		
	}
	//draw documents
	this.context.strokeStyle = "#FFFF00";
	this.context.fillStyle = "#FFFF00";
	for(var i = 0; i < this.data.documents.length; i++){
		var doc = this.data.documents[i];
		if(doc.x >= this.minX && doc.x <= this.maxX && doc.y >= this.minY && doc.y <= this.maxY){
			var scaledX = ((doc.x - this.minX) / (this.maxX - this.minX)) * this.width;
			var scaledY = ((doc.y - this.minY) / (this.maxY - this.minY)) * this.height;
			this.context.beginPath();
			this.context.arc(scaledX, scaledY ,1,0,Math.PI*2,true);
			this.context.closePath();
			this.context.stroke();
			this.context.fill();
		}		
	}
}

DocumentAtlas.prototype.setZoomLevel = function(zoomLevel) {
	this.zoomLevel = zoomLevel;
	this.minX = this.center.x - zoomLevel/2;
	this.maxX = this.center.x + zoomLevel/2;
	this.minY = this.center.y - zoomLevel/2;
	this.maxY = this.center.y + zoomLevel/2;
	
	if(this.minX < 0){
		this.maxX = this.maxX - this.minX;
		this.minX = 0;
	}
	
	if(this.maxX > 1){
		this.minX = this.minX - (this.maxX - 1);
		this.maxX = 1;
	}
	
	if(this.minY < 0){
		this.maxY = this.maxY - this.minY;
		this.minY = 0;
	}
	
	if(this.maxY > 1){
		this.minY = this.minY - (this.maxY - 1);
		this.maxY = 1;
	}
	
	this.center.x = (this.minX + this.maxX) / 2; 
	this.center.y = (this.minY + this.maxY) / 2;
	
	this.draw();
}

DocumentAtlas.prototype.translate = function(dx, dy){
	var newMinX = this.minX + dx;
	var newMaxX = this.maxX + dx;
	var newMinY = this.minY + dy;
	var newMaxY = this.maxY + dy;
	if(newMinX < 0){
		dx = dx - newMinX;
	}
	if(newMaxX > 1){
		dx = dx - (newMaxX - 1);
	}
	if(newMinY < 0){
		dy = dy - newMinY;
	}
	if(newMaxY > 1){
		dy = dy - (newMaxY - 1);
	}
	this.minX = this.minX + dx;
	this.minY = this.minY + dy;
	this.maxX = this.maxX + dx;
	this.maxY = this.maxY + dy;
	
	this.center.x = this.center.x + dx;
	this.center.y = this.center.y + dy;
	
	this.draw();
}

DocumentAtlas.prototype.zoomIn = function(){
	if(this.canZoomIn()){
		var zL = Math.max(this.zoomLevel - 0.1, 0.1);
		this.setZoomLevel(zL);
	}	
}

DocumentAtlas.prototype.zoomOut = function(){
	if(this.canZoomOut()){
		var zL = Math.min(this.zoomLevel + 0.1, 1);
		this.setZoomLevel(zL);
	}
}

DocumentAtlas.prototype.panLeft = function(){
	if(this.canPanLeft()){		
		var dx = - (this.zoomLevel / 10);
		this.translate(dx, 0);
	}
}

DocumentAtlas.prototype.panRight = function(){
	if(this.canPanRight()){
		var dx = this.zoomLevel / 10;
		this.translate(dx, 0);
	}
}

DocumentAtlas.prototype.panUp = function(){
	if(this.canPanUp()){
		var dy = - (this.zoomLevel / 10);
		this.translate(0, dy);
	}
}

DocumentAtlas.prototype.panDown = function(){
	if(this.canPanDown()){
		var dy = this.zoomLevel / 10;
		this.translate(0, dy);
	}
}

DocumentAtlas.prototype.canZoomIn = function(){
	if(this.zoomLevel > 0.1){
		return true;
	}else{
		return false;
	}
}

DocumentAtlas.prototype.canZoomOut = function(){
	if(this.zoomLevel < 1){
		return true;
	}else{
		return false;
	}
}

DocumentAtlas.prototype.canPanLeft = function(){
	if(this.minX > 0){
		return true;
	}else{
		return false;
	}
}

DocumentAtlas.prototype.canPanRight = function(){
	if(this.maxX < 1){
		return true;
	}else{
		return false;
	}
}

DocumentAtlas.prototype.canPanUp = function(){
	if(this.minY > 0){
		return true;
	}else{
		return false;
	}
}

DocumentAtlas.prototype.canPanDown = function(){
	if(this.maxY < 1){
		return true;
	}else{
		return false;
	}
}

DocumentAtlas.prototype.initKeywordSelect = function(){
	var self = this;
	
	if(self.keywordSelectCallback){
		$('#' + self.canvas.id).live('mousemove', function(event){
			if(!self.isDragging){
				canvas_offset = $(this).offset();			
				var x = event.pageX - canvas_offset.left;
				var y = event.pageY - canvas_offset.top;
				var scaledX = self.minX + (x / self.width)*(self.maxX - self.minX);
				var scaledY = self.minY + (y / self.height)*(self.maxY - self.minY);
				
				wordlist = self.getKeywordsCloseTo(scaledX, scaledY, 20);
				self.keywordSelectCallback(wordlist);
				
				doc = self.getDocOnPos(scaledX,scaledY);
				if (self.tooltipExist)
				{
					self.draw();
					self.tooltipExist = false;
				}
				if (doc != null)
				{
					self.tooltipExist = true;
					self.tooltip = self.context.fillText(doc.title,x,y);
					if (self.getArticleContent)
					{
						self.getArticleContent(doc);
					}
					doc = null;
				}
			}
		});
	}
}

DocumentAtlas.prototype.initMouseClickEvent = function(){
	var self = this;
	$('#' + self.canvas.id).live('click', function(event){
		canvas_offset = $(this).offset();
		var x = event.pageX - canvas_offset.left;
		var y = event.pageY - canvas_offset.top;
		var scaledX = self.minX + (x / self.width)*(self.maxX - self.minX);
		var scaledY = self.minY + (y / self.height)*(self.maxY - self.minY);
		doc = self.getDocOnPos(scaledX,scaledY);
		if (doc != null)
		{
			document.location = 'article.jsp?&id=' + doc.aid;
		}
	})
}

DocumentAtlas.prototype.initMousewheelEvents = function(){
	var self = this;
	$('#' + self.canvas.id).live('mousewheel', function(event, delta, deltaX, deltaY){
		if(delta < 0){
			self.zoomOut();
		}else{
			self.zoomIn();
		}
	})
}

DocumentAtlas.prototype.initDragCanvas = function(){
	var self = this;
	$('#' + self.canvas.id).live('mousedown', function(event){
		if(self.dragEnabled){
			canvas_offset = $(this).offset();			
			var x = event.pageX - canvas_offset.left;
			var y = event.pageY - canvas_offset.top;
			var scaledX = self.minX + (x / self.width)*(self.maxX - self.minX);
			var scaledY = self.minY + (y / self.height)*(self.maxY - self.minY);
			self.startDragX = scaledX;
			self.startDragY = scaledY;
			self.isDragging = true;			
		}
	});
	$('#' + self.canvas.id).live('mouseup', function(event){
		if(self.dragEnabled && self.isDragging){
			canvas_offset = $(this).offset();			
			var x = event.pageX - canvas_offset.left;
			var y = event.pageY - canvas_offset.top;
			var scaledX = self.minX + (x / self.width)*(self.maxX - self.minX);
			var scaledY = self.minY + (y / self.height)*(self.maxY - self.minY);
			var dx = self.startDragX - scaledX;
			var dy = self.startDragY - scaledY; 
			//self.translate(dx, dy);
			self.isDragging = false;
		}
	});
	$('#' + self.canvas.id).live('mousemove', function(event){
		if(self.dragEnabled && self.isDragging){
			canvas_offset = $(this).offset();			
			var x = event.pageX - canvas_offset.left;
			var y = event.pageY - canvas_offset.top;
			var scaledX = self.minX + (x / self.width)*(self.maxX - self.minX);
			var scaledY = self.minY + (y / self.height)*(self.maxY - self.minY);
			var dx = self.startDragX - scaledX;
			var dy = self.startDragY - scaledY;
			if(Math.abs(dx) > 0.05 || Math.abs(dy) > 0.05){
				self.startDragX = scaledX;
				self.startDragY = scaledY;
				if(self.canPanLeft() || self.canPanRight() || self.canPanUp() || self.canPanDown()){
					self.translate(dx, dy);
				}
			}
		}
	});
	$('#' + self.canvas.id).live('mouseleave', function(event){
		if(self.dragEnabled && self.isDragging){
			self.isDragging = false;
		}
	});
}

DocumentAtlas.prototype.initControls = function(){
	var html = '' +
	'<div id="navControls" class="noSel" style="position: absolute; width: 97px; height: 64px; z-index: 7; bottom: 10px; right: 10px;">' +
		'<div id="controlsBg" class="noSel" style="position: relative; width: 100%; height: 100%; opacity: 0.55; z-index: 1; background-color: rgb(0, 0, 0); border-radius: 5px 5px 5px 5px; box-shadow:2px 2px 5px #222"></div>' +
		'<div class="noSel" style="display: inherit; position: absolute; left: 4px; top: 11px; width: 20px; height: 20px; opacity: 0.7; z-index: 1;">' + 
			'<div id="_zinorm" style="position: absolute; left: 1px; top: 1px; width: 18px; height: 18px; background: none repeat scroll 0% 0% rgb(255, 255, 255); border-radius: 2px 2px 2px 2px; display: block;"></div>' +
			'<div id="_ziover" style="position: absolute; left: 0px; top: 0px; width: 20px; height: 20px; display: none; background: none repeat scroll 0% 0% rgb(255, 255, 255); border-radius: 2px 2px 2px 2px;"></div>' +
			'<div id="_zi_icon" style="position: absolute; left: 1px; top: 1px; width: 18px; height: 18px; "></div>' +
		'</div>' +
		'<div class="noSel" style="display: inherit; position: absolute; left: 4px; top: 33px; width: 20px; height: 20px; opacity: 0.7; z-index: 2;">' +
			'<div id="_zonorm" style="position: absolute; left: 1px; top: 1px; width: 18px; height: 18px; background: none repeat scroll 0% 0% rgb(255, 255, 255); border-radius: 2px 2px 2px 2px; display: block;"></div>' +
			'<div id="_zoover" style="position: absolute; left: 0px; top: 0px; width: 20px; height: 20px; display: none; background: none repeat scroll 0% 0% rgb(255, 255, 255); border-radius: 2px 2px 2px 2px;"></div>' +
			'<div id="_zo_icon" style="position: absolute; left: 1px; top: 1px; width: 18px; height: 18px; "></div>' +
		'</div>' +
		'<div class="noSel" style="display: inherit; position: absolute; left: 37px; top: 23px; width: 18px; height: 18px; opacity: 0.7; z-index: 3;">' +
			'<div id="_mlnorm" style="position: absolute; left: 1px; top: 1px; width: 16px; height: 16px; background: none repeat scroll 0% 0% rgb(255, 255, 255); border-radius: 2px 2px 2px 2px; display: block;"></div>' +
			'<div id="_mlover" style="position: absolute; left: 0px; top: 0px; width: 18px; height: 18px; display: none; background: none repeat scroll 0% 0% rgb(255, 255, 255); border-radius: 2px 2px 2px 2px;"></div>' +
			'<div id="_ml_icon" style="position: absolute; left: 1px; top: 1px; width: 16px; height: 16px; "></div>' +
		'</div>' +
		'<div class="noSel" style="display: inherit; position: absolute; left: 75px; top: 23px; width: 18px; height: 18px; opacity: 0.7; z-index: 4;">' +
			'<div id="_mrnorm" style="position: absolute; left: 1px; top: 1px; width: 16px; height: 16px; background: none repeat scroll 0% 0% rgb(255, 255, 255); border-radius: 2px 2px 2px 2px; display: block;"></div>' +
			'<div id="_mrover" style="position: absolute; left: 0px; top: 0px; width: 18px; height: 18px; display: none; background: none repeat scroll 0% 0% rgb(255, 255, 255); border-radius: 2px 2px 2px 2px;"></div>' +
			'<div id="_mr_icon" style="position: absolute; left: 1px; top: 1px; width: 16px; height: 16px; "></div>' +
		'</div>' +
		'<div class="noSel" style="display: inherit; position: absolute; left: 56px; top: 42px; width: 18px; height: 18px; opacity: 0.7; z-index: 5;">' +
			'<div id="_mdnorm" style="position: absolute; left: 1px; top: 1px; width: 16px; height: 16px; background: none repeat scroll 0% 0% rgb(255, 255, 255); border-radius: 2px 2px 2px 2px; display: block;"></div>' +
			'<div id="_mdover" style="position: absolute; left: 0px; top: 0px; width: 18px; height: 18px; display: none; background: none repeat scroll 0% 0% rgb(255, 255, 255); border-radius: 2px 2px 2px 2px;"></div>' +
			'<div id="_md_icon" style="position: absolute; left: 1px; top: 1px; width: 16px; height: 16px; "></div>' +
		'</div>' +
		'<div class="noSel" style="display: inherit; position: absolute; left: 56px; top: 4px; width: 18px; height: 18px; opacity: 0.7; z-index: 6;">' +
			'<div id="_munorm" style="position: absolute; left: 1px; top: 1px; width: 16px; height: 16px; background: none repeat scroll 0% 0% rgb(255, 255, 255); border-radius: 2px 2px 2px 2px; display: block;"></div>' +
			'<div id="_muover" style="position: absolute; left: 0px; top: 0px; width: 18px; height: 18px; display: none; background: none repeat scroll 0% 0% rgb(255, 255, 255); border-radius: 2px 2px 2px 2px;"></div>' +
			'<div id="_mu_icon" style="position: absolute; left: 1px; top: 1px; width: 16px; height: 16px; "></div>' +
		'</div>' +
		'<div class="noSel" style="display: inherit; position: absolute; left: 56px; top: 23px; width: 18px; height: 18px; opacity: 0.7; z-index: 7;">' +
			'<div id="_rsnorm" style="position: absolute; left: 1px; top: 1px; width: 16px; height: 16px; background: none repeat scroll 0% 0% rgb(255, 255, 255); border-radius: 2px 2px 2px 2px;"></div>' +
			'<div id="_rsover" style="position: absolute; left: 0px; top: 0px; width: 18px; height: 18px; display: none; background: none repeat scroll 0% 0% rgb(255, 255, 255); border-radius: 2px 2px 2px 2px;"></div>' + 
			'<div id="_rs_icon" style="position: absolute; left: 1px; top: 1px; width: 16px; height: 16px; "></div>' +
		'</div>' +
	'</div>';
	$('#' + this.id).append($(html));
	$('#_zi_icon').css('background', 'url("docAtlas/icons.png") no-repeat scroll -16px -16px transparent');
	$('#_zo_icon').css('background', 'url("docAtlas/icons.png") no-repeat scroll -66px -16px transparent');
	$('#_ml_icon').css('background', 'url("docAtlas/icons.png") no-repeat scroll -117px -17px transparent');
	$('#_mr_icon').css('background', 'url("docAtlas/icons.png") no-repeat scroll -167px -17px transparent');
	$('#_md_icon').css('background', 'url("docAtlas/icons.png") no-repeat scroll -217px -17px transparent');
	$('#_mu_icon').css('background', 'url("docAtlas/icons.png") no-repeat scroll -267px -17px transparent');
	$('#_rs_icon').css('background', 'url("docAtlas/icons.png") no-repeat scroll -317px -17px transparent');
	
	var da = this;
	
	$('#_zi_icon').live('click', function(){
		da.zoomIn();
	});
	
	$('#_zo_icon').live('click', function(){
		da.zoomOut();
	});
	
	$('#_ml_icon').live('click', function(){
		da.panLeft();
	});
	
	$('#_mr_icon').live('click', function(){
		da.panRight();
	})
	
	$('#_mu_icon').live('click', function(){
		da.panUp();
	})
	
	$('#_md_icon').live('click', function(){
		da.panDown();
	})		
}
