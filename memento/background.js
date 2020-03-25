// 호출한 페이지에 1씩 증가시킨 i를 1초마다 전달한다.
setInterval( function() {
    buildTypedUrlList();
}, 3000 );

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
    var oneWeekAgo = (new Date).getTime() - 1000 * 100 * 6;

    // chrome.history.getVisits() 에서 받기를 원하는 callback 수 추적
    // 0에 도달할 경우, 모든 결과를 얻는다
    var numRequestsOutstanding = 0;

    chrome.history.search({
        'text': '',              // 모든 히스토리 아이템을 가져온다
        'startTime': oneWeekAgo  // 일주일 이하
    },
        function(historyItems) {
        // 각각의 히스토리로부터 모든 방문 세부사항을 얻음
            for (var i = 0; i < historyItems.length; ++i) {
                var url = historyItems[i].url;
                var processVisitsWithUrl = function(url) {
                    return function(visitItems) {
                        processVisits(url, visitItems);
                        };
                };
        
                // getVisits callback -> processVisitsWithUrl
                chrome.history.getVisits({url: url}, processVisitsWithUrl(url));
                numRequestsOutstanding++; // 히스토리 개수
            }
            if (!numRequestsOutstanding) {
                onAllVisitsProcessed();
            }}
    );

    var urlToCount = [];    
    // chrome.history.getVisits() 콜백.
    var processVisits = function(url, visitItems) {
        for (var i = 0, ie = visitItems.length; i < ie; ++i) {
            let tmp = "type : " + visitItems[i].transition + " | url : " + url
            urlToCount.push(tmp);
        }
        // 만일 이것이 ProcessVisits()에 대한 최종 outstanding 호출이라면, 최종 결과를 얻는다. 
        // 팝업에 표시할 URL 목록을 작성 진행
        if (!--numRequestsOutstanding) {
            onAllVisitsProcessed();
        }
    };

    // 디스플레이 할 최종 url 리스트가 있을 경우 호출됨
    var onAllVisitsProcessed = function() {
        console.log(urlToCount);
        // 팝업에 추가
    };
}