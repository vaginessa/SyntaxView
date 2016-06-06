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

function listLanguages(){
    var languages = hljs.listLanguages();
    var numLanuages = languages.length;
    for (i = 0; i < numLanuages; i++) {
        console.log(languages[i]);
    }
}

function setLanguage(language){
    if(language.length == 0){
        return;
    }
    document.getElementById("codearea").className = language + ' hljs';
}

function beautifyXML(){
    //todo
}

function minifyXML(){
    //todo
}

function clearAll(){
    document.getElementById("codearea").innerHTML = '';
}

function testBridge(){
     Android.showToast('Bridge Working');
}


