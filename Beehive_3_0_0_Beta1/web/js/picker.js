function open_svn_url_picker_single(targetField, params, svnwebclienturl) {
    open_svn_url_picker(targetField, true, false, params, svnwebclienturl);
}


function open_svn_url_picker(targetField, isSingleRevision, isMultiSelectionUrlMode, params, svnwebclienturl) {
    if(svnwebclienturl == null || svnwebclienturl == undefined) {
        svnwebclienturl = "/svnwebclient";
    }
    var pickerUrl = svnwebclienturl+"/pickerDirectoryContent.jsp"
    params = (params == null || params == undefined) ? 'clear' : params+'&clear';
    var revisionUrl = pickerUrl + '?' + params;
    
    if (isSingleRevision) {
          revisionUrl += '&singlerevision';
    }
    if (isMultiSelectionUrlMode) {
          revisionUrl += '&multiurlselection';
    }
    
    self.targetField = targetField; 
    var vWinUsers = window.open(revisionUrl, 'SelectRepositoryUrl', 'status=no,resizable=yes,scrollbars=yes,top=100,left=200,width=700,height=700');                      
                      
    if (!vWinUsers.opener) {
        vWinUsers.opener = self;
    }
                     
	vWinUsers.focus();                
} 

function populateForm(formName) {   
     if(window.opener == null || window.opener.closed || window.opener.document == null || window.opener.targetField == null){
		cannot_use();
		return;
	}
                        
    var items = document.forms[formName].elements['items'];
    var urls = document.forms[formName].elements['pickerUrl'];
	var revisions = document.forms[formName].elements['pickerRevision'];					    		
	
	//alert(document.forms[formName].elements['items']);
					
   	if (items != null) {
       	var storage = '';
       	
       	    	    	
       	var countItems = items.length;
        if (countItems == undefined) {
            countItems = 1;
            var url = urls.value;
            var revision = revisions.value;
            
            //alert(url + revision);
            storage = populateFields(url, revision);                                    
        }  else {                                
        	 	for (var i = 0; i < countItems; i++) {                    	 
                    var url = urls[i].value;
                    var revision = 	revisions[i].value;
                                        
                    //alert(url + revision);                    
                    storage += populateFields(url, revision);                    
                    if ((i + 1)  < countItems) {
                        storage += ',';
                    }
                }    
        }   
        
        saveStorage(storage);   	       		                                             
        window.opener.update();                                        
        window.opener.focus();        
        window.close();         
    } else {
        alert('Please add at least one item');	    
    }		       
}


function pickerSingleSelectAction(url, revision) {
    if(window.opener == null || window.opener.closed || window.opener.document == null || window.opener.targetField == null){
		cannot_use();
		return;
	}
    
    var storage = populateFields(url, '');
    saveStorage(storage);
        
    window.opener.singleUpdate();  
    window.opener.focus();         
    window.close();
}

function pickerAction(actionName, formName) {            
	var items = document.forms[formName].elements['items'];
	var flags = document.forms[formName].elements['flags'];	
	
    var actions = document.forms[formName].elements['actions'];
	
	if (items != null && flags != null)
	{
    	var countItems = items.length;
    	if (countItems == undefined) {
        	if (items.checked) {
                flags.value="1"
                actions.value = actionName;
        		document.forms[formName].submit();                
        	} else {
        		alert('Please select at least one item');            	
        	}
    	} else {
        	var checkedItems = 0;
        	
        	for (var i = 0; i < countItems; i++)
        	{
        		if (items[i].checked)
        		{
        			checkedItems++;
        		}
        	}
        
        	if (checkedItems < 1)
        	{
        		alert('Please select at least one item');
        	}
        	else
        	{
        		for (var i = 0; i < countItems; i++) {
        			if (items[i].checked) flags[i].value="1"
        			else flags[i].value="0";
        		}        		
        		actions.value = actionName;
        		document.forms[formName].submit();
        	}
    	}
    }                
}


function isInputField(node) {
    if (node!=null){
        var tagName = node.tagName;
        return tagName == "INPUT" || tagName == "TEXTAREA" || tagName == "SELECT";
    } else {
        return false;
    }
}

function findFormRecursively(node) {
    if (isInputField(node)) {
        return node.form;
    }
    var childNodes = node.childNodes;
    for (var j = 0; j < childNodes.length; j++) {
        var result = findFormRecursively(childNodes[j]);
        if (result!=null) {
            return result;
        }
    }
    return null;
}


function findFormBySender(sender) {        
    var tr = sender.parentNode.parentNode; // table row in which must be the inputs
    var result = findFormRecursively(tr);
    if (result==null) {             
        result = document.forms[0]; //safety fallback
    }
    return result;
}


function appendRowResource(sender, ids) {
    var form = findFormBySender(sender); 	
    var newrow = createNewRow(sender);
	
	for(var i = 0; i < ids.length;i++){        	        	
		var id = ids[i];    		    		
		var td = document.createElement("td");
		td.className = "fixfont";
		newrow.appendChild(td);
		
		if(id!==""){
			var element = form.elements[id];			
			var value = getElemValue(element);
			var label = getElemLabel(element)
			td.innerHTML=label;    			
		}
	}
	
    var actions_td =document.createElement("td");
    actions_td.innerHTML="<a id='added_row' onClick='removerow(this)'><img src='./images/minus.gif' border='0'></a>";
	newrow.appendChild(actions_td);


    for(var i = 0; i < ids.length;i++){
		var id = ids[i];
		if(id!==""){
			var element = form.elements[id];			
			
		    //alert(element.name + element.value)
			clearElemValue(element);
		}
	}
    return newrow;
}

function createNewRow(sender){
	var plustr = sender.parentNode.parentNode;
	//alert(plustr.nodeName);
	var tbody = plustr.parentNode;
	//alert(tbody.nodeName);
	var table = tbody.parentNode;
	//alert(table.nodeName);

	var newrow = document.createElement("tr");	
	tbody.insertBefore(newrow,plustr);	
	return newrow;
}


function getElemValue(element){
    return element.value;
}


function getElemLabel(element){
	return element.value;
}


function clearElemValue(element){
	return element.value = "";
}


function isFilled(value){
   if(value==null){
	   	return false;
   } else if(value != null && value.length<1){   
		return false;
   } else if(value != null && value.search(/\w+/) == -1){
		return false;
   }	
   return true;
} 
    

function isNumber(elem) {
    var isNum = false;
       
    var re = new RegExp("\\d+");
    if (elem.match(re)) {
        isNum = true;   
    }        
    return isNum;
}


function populateFields(url, revision) {
    var res = url;
    if (revision != null && revision != '') {
        res += ':' + revision;    
    }
    return res;
}

function saveStorage(storage) {
    
    storage = storage.replace("&comma", "'");
    
    while (true) {
        if (storage.indexOf("&comma") != -1) {
            storage = storage.replace("&comma", "'");        
        } else {
            break;    
        }        
    }
       
    var targetField = window.opener.targetField;
    targetField.value = '' + storage; 
}

function cannot_use(){
	alert("The page from where the Url picker was opened is probably closed.");
    window.close();		
}    
