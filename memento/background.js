chrome.extension.onConnect.addListener(function(port) {
    console.log("Connected .....");
    port.onMessage.addListener(function(msg) {
        console.log("message recieved " + msg);
        if (msg == 'On') {
            console.log('워커 시작');
            startWorker();
        }
        else if (msg == 'Off'){
            console.log('워커 종료');
            stopWorker();
        }
    });
});

var interval;
function startWorker() {
    if (!interval){
        console.log('start 실행중인 인터벌 종료');
        clearInterval(interval);
    }
    interval = setInterval( function() {
        buildTypedUrlList();
    }, 3000 );
}

function stopWorker() {
    if(interval){
        console.log('stop 실행중인 인터벌 종료');
        clearInterval(interval);
    }
}

function onAnchorClick(event) {
    chrome.tabs.create({
        selected: true,
        url: event.srcElement.href
    });
    return false;
}


// 기록을 검색하여 사용자가 입력한 링크를 최대 10개 찾아 팝업으로 표시
function buildTypedUrlList() {
    // 지난주의 히스토리 아이템을 찾기 위해, 현재 시간에서 일주일을 뺀다.
    var tenSec = (new Date).getTime() - 1000 * 10;

    // chrome.history.getVisits() 에서 받기를 원하는 callback 수 추적
    // 0에 도달할 경우, 모든 결과를 얻는다
    var numRequestsOutstanding = 0;

    chrome.history.search({
        'text': '',              // 모든 히스토리 아이템을 가져온다
        'startTime': tenSec  // 10초 이내
    },
        function(historyItems) {
        // 각각의 히스토리로부터 모든 방문 세부사항을 얻음
            for (var i = 0; i < historyItems.length; ++i) {
                let title = historyItems[i].title;
                let lastVisitTime = historyItems[i].lastVisitTime;
                let visitCount = historyItems[i].visitCount;
                let url = historyItems[i].url;

                console.log('----------------------------');
                console.log(title);
                console.log(lastVisitTime);
                console.log(visitCount);
                console.log(url);
                console.log('----------------------------');
                // let processVisitsWithUrl = function(url) {
                //     return function(visitItems) {
                //         processVisits(url, visitItems);
                //         };
                // };
        
                // getVisits callback -> processVisitsWithUrl
                //chrome.history.getVisits({url: url}, processVisitsWithUrl(url));
                numRequestsOutstanding++; // 히스토리 개수
            }
            if (!numRequestsOutstanding) {
                //onAllVisitsProcessed();
            }}
    );

    // var onAllVisitsProcessed = function() {
    //     console.log();
    //     // 팝업에 추가
    // };
    // var urlToCount = [];
    // // chrome.history.getVisits() 콜백.
    // var processVisits = function(url, visitItems) {
    //     for (var i = 0, ie = visitItems.length; i < ie; ++i) {
    //         let tmp = "type : " + visitItems[i].transition + " | url : " + url
    //         urlToCount.push(tmp);
    //     }
    //     // 만일 이것이 ProcessVisits()에 대한 최종 outstanding 호출이라면, 최종 결과를 얻는다. 
    //     // 팝업에 표시할 URL 목록을 작성 진행
    //     if (!--numRequestsOutstanding) {
    //         onAllVisitsProcessed();
    //     }
    // };

    // 디스플레이 할 최종 url 리스트가 있을 경우 호출됨
}