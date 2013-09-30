function selectRow(row) {
	if(row.className != "active") {
		row.className = "selected";
	}
}

function deselectRow(row) {
	if(row.className != "active") {
		row.className = row.name;
	}
}

function makeRowActive(row) {
	var prev = row.previousSibling;
	while(prev != null) {
		if(prev.className == "active") {
			prev.className = prev.name;
		}
		prev = prev.previousSibling;
	}
	
	var next = row.nextSibling;
	while(next != null) {
		if(next.className == "active") {
			next.className = next.name;
		}
		next = next.nextSibling;
	}
	
	row.className = "active";
}



function choseRow(row, adresa, cil) {
	makeRowActive(row);
	updateNavigation(row);
	window.open(adresa, cil);
}

function updateNavigation(row) {
	item_navis = document.getElementsByName("item_navi");
	for(i = 0; i < item_navis.length; i++) {
		title = row.id;
		title = title.substring(title.indexOf("-") + 1);
		item_navis[i].innerHTML = "> " + title;
//		window.status = window.status + " - " + item_navis[i].innerHTML;
	}
}

function paintactive (table) {
	var rows = table.rows;
	for (i = 0;i < rows.length;i++) {
		if (rows[i].className == "active") {
			var cells = rows[i].cells;
			for (n = 0;n < cells.length;n++) {
				cells[n].style.backgroundColor = "#FFCC00";
			}
		}
	}
}

function firstsecond (tableName) {    
	table = document.getElementsByName(tableName);
	
	if(table.length > 0) {
		table = table[table.length-1];
	}
	
	rows = table.rows;
	second = true;
	
	if(rows != null) {
		for (i = 0; i < rows.length; i++) {
			if (second) {
				if (rows[i].className !== "active") {
					rows[i].className = "second";
				}
				rows[i].name = "second";
				second = false;
			} else {
				if (rows[i].className !== "active") {
					rows[i].className = "first";
				}
				rows[i].name = "first";
				second = true;
			}
		}
	}
}



function changeRowStyle(trr, style) {
	trr.className = style;
}

function changeRowColumnsStyle(tr, style) {
	var columns = tr.getElementsByTagName("td");
	for (var i = 0; i < columns.length; i++) {
		columns[i].className = style;
	}
}

//Load subtree into hidden frame
function showPreview(url){	
	window.preview.location = url;
}

function activateRow(table, rowId) {
	var rows = table.getElementsByTagName("tr");
	firstRow = true;
	for (var i = 0; i < rows.length; i++) {
		if(rows[i] == rowId) {
			rows[i].className = "active";
		} else {
			if(firstRow) {
				rows[i].className = "first";
			} else {
				rows[i].className = "second";
			}
		}
	}
}



function zjistiactive(table) {
var rows = table.rows;
for (var i = 0;i< rows.length;i++) {
  if (rows[i].className == "active") {
  last= rows[i];
  } 
}


}


function over(tr) {
	var columns = tr.getElementsByTagName("td");
	if (last !== tr) {
	for (var i = 0; i < columns.length; i++) {
		columns[i].background = selected.src;
	}
	}else{
	for (var i = 0; i < columns.length; i++) {
		columns[i].background = activeselected.src;
		
	}
	
	}
}
function out(tr) {
	var columns = tr.getElementsByTagName("td");
	if (last !== tr) {
	for (var i = 0; i < columns.length; i++) {
		columns[i].background = unselected.src;
		}
	}else{
	for (var i = 0; i < columns.length; i++) {
		columns[i].background = active.src;
	}
	}
}
function klik(tr) {
	var columns = tr.getElementsByTagName("td");
	for (var i = 0; i < columns.length; i++) {
		columns[i].background = active.src;
	}
	if (last !== "") {
	var column = last.getElementsByTagName("td");
	for (var i = 0; i < column.length; i++) {
		column[i].background = unselected.src;
	}
	}
	last=tr;
}

function hide_row(idStr) {
    var row = document.getElementById(idStr);
    var columns = row.getElementsByTagName("td");
    columns[0].style.display='none';
}

function show_row(idStr) {
    var row = document.getElementById(idStr);
    var columns = row.getElementsByTagName("td");
    columns[0].style.display='block';
}

function checkRevision(idStr) {
	var currentRevision = document.getElementById(idStr);
	if (currentRevision.checked)
	{
		var revisions = document.forms['revisionform'].elements['revision'];
		var countRevisions = revisions.length;
		var checkedRevisions = 0;
	
		for (var i = 0; i < countRevisions; i++)
		{
			if (revisions[i].checked)
			{
				if (checkedRevisions == 0)
				{
					firstRevision = revisions[i].value;
				}
				else if (checkedRevisions == 1)
				{
					secondRevision = revisions[i].value;			
				}
				checkedRevisions++;
			}
		}
		
		if (checkedRevisions > 2)
		{
			currentRevision.checked = !currentRevision.checked;
		}
	}
}

function compareRevisions(diff_url) {
	var revisions = document.forms['revisionform'].elements['revision'];
	var countRevisions = revisions.length;
	var checkedRevisions = 0;
	var firstRevision;
	var secondRevision;
	
	for (var i = 0; i < countRevisions; i++)
	{
		if (revisions[i].checked)
		{
			if (checkedRevisions == 0)
			{
				leftRevision = revisions[i].value;
			}
			else if (checkedRevisions == 1)
			{
    			if (revisions[i].value >= leftRevision) 
    			{
        			rightRevision = revisions[i].value;
    			}
    			else
    			{
        			rightRevision = leftRevision;
        			leftRevision = revisions[i].value;
    			}
			}
			checkedRevisions++;
		}
	}

	if (checkedRevisions < 2)
	{
		alert('Please select two revisions');
	}
	else if (checkedRevisions == 2)
	{
		document.location.href=diff_url+'&startrev='+leftRevision+'&endrev='+rightRevision;
	}
}

function checkDelete() {
	var items = document.forms['dir_list'].elements['items'];
	var flags = document.forms['dir_list'].elements['flags'];

	if (items != null && flags != null)
	{
    	var countItems = items.length;
    	if (countItems == undefined) {
        	if (items.checked) {
                flags.value="1"
        		document.dir_list.submit();                
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
        		document.dir_list.submit();
        	}
    	}
    }
    else
    {
        alert('Directory is empty, so you can\'t delete anything');
    }
}

function moveSelectedItems (selectFrom, selectTo) {
	var from = document.getElementById(selectFrom);
	var to = document.getElementById(selectTo);
	var fromlen = from.options.length;
	var tolen = to.options.length;
	
	for (i = fromlen-1;i >= 0 ;i--) {
		if (from.options[i].selected) {
			var item=document.createElement("OPTION");
            item.text = from.options[i].text;
            item.value = from.options[i].value;
            (to.options.add) ? to.options.add(item) : to.add(item, null);
			tolen++;
			from.options.remove(i);
		}
	}
}

function moveAllItems (selectFrom, selectTo) {
	var from = document.getElementById(selectFrom);
	var to = document.getElementById(selectTo);
	var fromlen = from.options.length;
	var tolen = to.options.length;
	
	for (i = fromlen-1;i >= 0 ;i--) {
		var item=document.createElement("OPTION");
        item.text = from.options[i].text;
        item.value = from.options[i].value;
        (to.options.add) ? to.options.add(item) : to.add(item, null);
		tolen++;
		from.options.remove(i);
	}
}

function enableCheckbox (formname, master, slave) {
	var mst = document.forms[formname].elements[master];
	var slv = document.forms[formname].elements[slave];
	var len = mst.length;
	
	for (i = 0; i <len; i++) {
		slv[i].disabled = !mst[i].checked;
	}
}

function utf8(wide) {
  var c, s;
  var enc = "";
  var i = 0;
  while(i<wide.length) {
    c= wide.charCodeAt(i++);
    // handle UTF-16 surrogates
    if (c>=0xDC00 && c<0xE000) continue;
    if (c>=0xD800 && c<0xDC00) {
      if (i>=wide.length) continue;
      s= wide.charCodeAt(i++);
      if (s<0xDC00 || c>=0xDE00) continue;
      c= ((c-0xD800)<<10)+(s-0xDC00)+0x10000;
    }
    // output value
    if (c<0x80) enc += String.fromCharCode(c);
    else if (c<0x800) enc += String.fromCharCode(0xC0+(c>>6),0x80+(c&0x3F));
    else if (c<0x10000) enc += String.fromCharCode(0xE0+(c>>12),0x80+(c>>6&0x3F),0x80+(c&0x3F));
    else enc += String.fromCharCode(0xF0+(c>>18),0x80+(c>>12&0x3F),0x80+(c>>6&0x3F),0x80+(c&0x3F));
  }
  return enc;
}

var hexchars = "0123456789ABCDEF";

function toHex(n) {
  return hexchars.charAt(n>>4)+hexchars.charAt(n & 0xF);
}

var okURIchars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-";

function encodeURIComponentNew(s) {
  var s = utf8(s);
  var c;
  var enc = "";
  for (var i= 0; i<s.length; i++) {
    if (okURIchars.indexOf(s.charAt(i))==-1)
      enc += "%"+toHex(s.charCodeAt(i));
    else
      enc += s.charAt(i);
  }
  return enc;
}

function buildURL(fld)
{
	if (fld == "") return false;
	var encodedField = "";
	var s = fld;
	if (typeof encodeURIComponent == "function")
	{
		// Use JavaScript built-in function
		// IE 5.5+ and Netscape 6+ and Mozilla
		encodedField = encodeURIComponent(s);
	}
	else 
	{
		// Need to mimic the JavaScript version
		// Netscape 4 and IE 4 and IE 5.0
		encodedField = encodeURIComponentNew(s);
	}
	alert ("New encoding: " + encodeURIComponentNew(fld) +
		 "\n           escape(): " + escape(fld));
	return true;
}
