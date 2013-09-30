var leftFrameName,rightFrameName;
var loadedFrameNum = 0;

function scrollLeftFrame()
{	
	var left_shift = (frames[rightFrameName].pageXOffset)?(frames[rightFrameName].pageXOffset):(frames[rightFrameName].document.documentElement&&frames[rightFrameName].document.documentElement.value!=undefined)?frames[rightFrameName].document.documentElement.scrollLeft:frames[rightFrameName].document.body.scrollLeft;
	var top_shift = (frames[rightFrameName].pageYOffset)?(frames[rightFrameName].pageYOffset):(frames[rightFrameName].document.documentElement&&frames[rightFrameName].document.documentElement.value!=undefined)?frames[rightFrameName].document.documentElement.scrollTop:frames[rightFrameName].document.body.scrollTop;
	window.frames[leftFrameName].scrollTo(left_shift,top_shift);
}

function scrollRightFrame()
{
	var left_shift = (frames[leftFrameName].pageXOffset)?(frames[leftFrameName].pageXOffset):(frames[leftFrameName].document.documentElement&&frames[leftFrameName].document.documentElement.value!=undefined)?frames[leftFrameName].document.documentElement.scrollLeft:frames[leftFrameName].document.body.scrollLeft;
	var top_shift = (frames[leftFrameName].pageYOffset)?(frames[leftFrameName].pageYOffset):(frames[leftFrameName].document.documentElement&&frames[leftFrameName].document.documentElement.value!=undefined)?frames[leftFrameName].document.documentElement.scrollTop:frames[leftFrameName].document.body.scrollTop;
	window.frames[rightFrameName].scrollTo(left_shift,top_shift);
}

function frame_loaded(left_frame_name, right_frame_name){	
	loadedFrameNum++;
	if (loadedFrameNum == 2) {
		attach_scroll(left_frame_name, right_frame_name);
	}
}
	
function attach_scroll(left_frame_name, right_frame_name){
	leftFrameName = left_frame_name;
	rightFrameName = right_frame_name;
	
	if(navigator.userAgent.indexOf("Gecko")!=-1) {//Gecko compatible
	  	frames[right_frame_name].document.onscroll = function(){scrollLeftFrame();};
		frames[left_frame_name].document.onscroll = function(){scrollRightFrame();};
	} else {//IE or other*/
		frames[right_frame_name].document.body.onscroll = function(){scrollLeftFrame();};
		frames[left_frame_name].document.body.onscroll = function(){scrollRightFrame();};
	}
}
	var currentPosition = -1;
	var array;
	var frameNames;
	var arraySize;
	
	function set(numbers,names){
		array = numbers;		
		frameNames = names;
		arraySize = array.length;	
	}	
	function next(){
		var goto = array[arraySize-1];		
		if(arraySize > 0){			
			if(currentPosition != arraySize-1) {
				goto = array[++currentPosition];
			}								
		}
		jump(goto);
	}			
	function previous(){
		var goto = array[0];	
		if(arraySize > 0) {
			if (currentPosition != -1 && currentPosition != 0) {
				goto = array[--currentPosition];
			}			
		}				
		jump(goto);
	}		
	function jump(goto) {		
		if ( self.frames[leftFrameName] == null || self.frames[rightFrameName] == null) {
			return;
		} else {
			var path = frameNames[currentPosition];		
			if (path.indexOf("startrev") != -1) {			
				self.frames[leftFrameName].location = path+"#"+goto;		
	
			} else {
				self.frames[rightFrameName].location = path+"#"+goto;		
			}
		}
	}