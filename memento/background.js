var turnoff = false;
var user = "";
const googlePostFix = " - Google 검색";
const naverPostFix = " : 네이버 통합검색";
var tabAndTitles = [];
//TODO:
// 1. StayedTime 측정 - 탭의 내용이 변경될 때 마다 측정해서 보내

chrome.windows.onRemoved.addListener( function(closedWindowId) {
   if (turnoff) { return }
   sendClosed();
});

// tab이 닫힐 때 tabAndTitles 객체에서 tabid에 관한 요소를 지운다
chrome.tabs.onRemoved.addListener( function(tabId, removeInfo) {
    console.log('탭이 닫힙니다.')
    const itemToFind = tabAndTitles.find( function(item) { return item.id === tabId})
    const idx = tabAndTitles.indexOf(itemToFind); 
    if (idx > -1) {
        console.log('탭 정보 삭제')
        tabAndTitles.splice(idx, 1);
    }
})

chrome.tabs.onUpdated.addListener( function(tabId, changeInfo, tab) {
    if (turnoff) { return }

    if (changeInfo.status == 'complete' && tab.status == 'complete' && tab.url != undefined) {
        // tab title이  검색 forme인지 여부 검사
        if (tab.title.indexOf(googlePostFix) > -1 || tab.title.indexOf(naverPostFix) > -1){
            
            console.log('검색 폼입니다.')
            // 검색 폼일 경우 GoBack인지 테스트한다
            if (isGoBack(tabId, tab.title)){
                console.log('뒤로가기 입니다.')
                // GoBack으로 판명되면 이하 수행하지 않고 종료
                return;
            }
            console.log('뒤로가기가 아닙니다');
        }
        console.log("Now login user " + user);
        console.log("Now tab id " + tabId);
        console.log("Now tab title " + tab.title);
        console.log("Now tab url " + tab.url);
        
        sendPost(data = {
            tabId: tabId,
            title: tab.title,
            url: tab.url,
            visitedTime: new Date().getTime(),
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
            sendStopWorker();
            turnoff = true;
        }
        else if (msg == 'GetUser'){
            console.log('유저 정보 전달');
            chrome.identity.getProfileUserInfo(function(userInfo) {
                port.postMessage(userInfo.email);
            });
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

function sendClosed() {
    var destUrl;
    destUrl = 'http://localhost:8080/close-window?name=' + user;
    console.log("창 닫힘, Redis 저장 요청");
    console.log(destUrl);
    var xhr = new XMLHttpRequest();
    xhr.open("POST", destUrl, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.send('');
}

function sendStopWorker() {
    var destUrl;
    chrome.identity.getProfileUserInfo(function(userInfo) {
        destUrl = 'http://localhost:8080/stop-worker?name=' + user;
        console.log("워커 종료, Redis 저장 요청");
        console.log(destUrl);
        var xhr = new XMLHttpRequest();
        xhr.open("POST", destUrl, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.send();
    });
}

function isGoBack(id, title) {
    var tabAndTitle = {
        id: id,
        title: title
    }
    for(var i = 0; i< tabAndTitles.length; i++) {
        if(tabAndTitles[i].id == tabAndTitle.id && tabAndTitles[i].title == tabAndTitle.title){
            return true;
        }
    }
    tabAndTitles.push(tabAndTitle);
    return false;
}