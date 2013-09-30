function xstooltip_findPosX(obj) 
{
  var curleft = 0;
  if (obj.offsetParent) 
  {
    while (obj.offsetParent) 
        {
            curleft += obj.offsetLeft
            obj = obj.offsetParent;
        }
    }
    else if (obj.x)
        curleft += obj.x;
    return curleft;
}

function xstooltip_findPosY(obj) 
{
    var curtop = 0;
    if (obj.offsetParent) 
    {
        while (obj.offsetParent) 
        {
            curtop += obj.offsetTop
            obj = obj.offsetParent;
        }
    }
    else if (obj.y)
    {
        curtop += obj.y;
    }
    return curtop;
}

function xstooltip_show(tooltipId, parentId)
{
    var posX = 0;
    var posY = 0;
    it = document.getElementById(tooltipId);
    
    if ((it.style.top == '' || it.style.top == 0) 
        && (it.style.left == '' || it.style.left == 0))
    {
        // need to fixate default size (MSIE problem)
        it.style.width = it.offsetWidth;
        it.style.height = it.offsetHeight;
        
        img = document.getElementById(parentId); 
    
        // if tooltip is too wide, shift left to be within parent 
        if (posX + it.offsetWidth > img.offsetWidth) posX = img.offsetWidth - it.offsetWidth;
        if (posX < 0 ) posX = 0; 
        
        x = xstooltip_findPosX(img) + posX;
        y = xstooltip_findPosY(img) + posY;
        
        it.style.top = y+ img.offsetHeight;
        var screenSize;
        var isDOM = document.getElementById;
        var isMozilla=isDOM && navigator.appName=="Netscape";
        if (isMozilla) {
	    	screenSize = document.width;
	    } else {
		    screenSize = document.body.scrollWidth;
		}
                             
        var toolRightEdge = x + it.offsetWidth;                
        if (screenSize < toolRightEdge) {
	    	x = x - (toolRightEdge - screenSize);
	    	if (x < 0) {
		    	it.style.left = 0;	
		    } else {
				it.style.left = x;    
			}
	    } else {
			it.style.left = x;    
		}
    }
    
    it.style.visibility = 'visible'; 
}

function xstooltip_hide(id)
{
    it = document.getElementById(id); 
    it.style.visibility = 'hidden'; 
}
