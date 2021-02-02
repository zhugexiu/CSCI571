URL = "/api/search/?";
URL += "keyword=" + document.getElementById('keyword').value;

jsonObj = loadJSON(URL);
function loadJSON(url) {
    if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
        xmlhttp=new XMLHttpRequest();
    } else {// code for IE6, IE5
        xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlhttp.open("GET",url,false);
    xmlhttp.send();
    jsonObj= JSON.parse(xmlhttp.responseText);
    return jsonObj;
}
console.log(jsonObj);

