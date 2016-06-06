function loadString(content){
    document.getElementById("codearea").innerHTML =  content;
    finishedImport();
}

function addLine(line){
    document.getElementById("codearea").innerHTML += line +'<br>';
}

function finishedImport(){
    hljs.highlightBlock(document.getElementById("codearea"));
    Android.finishedImport();
}

function clearAll(){
    document.getElementById("codearea").innerHTML = '';
}

function testBridge(){
     Android.showToast('Bridge Working');
}


