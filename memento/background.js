var turnoff = false;
var user = "";

chrome.tabs.onUpdated.addListener( function(tabId, changeInfo, tab) {
    if (turnoff) { return }

    if (changeInfo.status == 'complete' && tab.status == 'complete' && tab.url != undefined) {
        console.log("Now login user " + user);
        console.log("Now tab id " + typeof(tabId));
        console.log("Now tab title " + tab.title);
        console.log("Now tab url " + tab.url);

        sendPost(data = {
            tabId: tabId,
            title: tab.title,
            url: tab.url,
            visitedTime: new Date().getTime() / 1000,
            stayedTime: 10000000
        });
    }
}); 

chrome.extension.onConnect.addListener(function(port) {
    console.log("Connected .....");
    port.onMessage.addListener(function(msg) {
        console.log("message recieved " + msg);
        if (msg == 'On') {
            console.log('이벤트 감지 시작');
            Login();
            turnoff = false;
        }
        else if (msg == 'Off'){
            console.log('이벤트 감지 종료');
            turnoff = true;
        }
    });
});

function Login() {
    chrome.identity.getProfileUserInfo(function(userInfo) {
        user = userInfo.email;
        var url = "http://localhost:8080/save-user?name=" + user;
        var xhr = new XMLHttpRequest();
        xhr.open("POST",url ,true);
        xhr.setRequestHeader('Content-Type',  'application/x-www-form-urlencoded');
        xhr.send('');
    });
}

function sendPost(data) {
    var destUrl;
    chrome.identity.getProfileUserInfo(function(userInfo) {
        destUrl = 'http://localhost:8080/collect?name=' + user;
        console.log(destUrl);
        console.log(JSON.stringify(data));
        var xhr = new XMLHttpRequest();
        xhr.open("POST", destUrl, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.send(JSON.stringify(data));
    });
}