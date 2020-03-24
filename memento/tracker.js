// Copyright (c) 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

// Event listner for clicks on links in a browser action popup.
// Open the link in a new tab of the current window.
function onAnchorClick(event) {
  chrome.tabs.create({
    selected: true,
    url: event.srcElement.href
  });
  return false;
}

// Given an array of URLs, build a DOM list of those URLs in the
// browser action popup.
function buildPopupDom(divName, data) {
  var popupDiv = document.getElementById(divName);

  var ul = document.createElement('ul');
  popupDiv.appendChild(ul);

  for (var i = 0, ie = data.length; i < ie; ++i) {
    var a = document.createElement('a');
    a.href = data[i];
    a.appendChild(document.createTextNode(data[i]));
    a.addEventListener('click', onAnchorClick);

    var li = document.createElement('li');
    li.appendChild(a);

    ul.appendChild(li);
  }
}

// 기록을 검색하여 사용자가 입력한 링크를 최대 10개 찾아 팝업으로 표시
function buildTypedUrlList(divName) {
  // 지난주의 히스토리 아이템을 찾기 위해, 현재 시간에서 일주일을 뺀다.
  var microsecondsPerWeek = 1000 * 60 * 10;
  var oneWeekAgo = (new Date).getTime() - microsecondsPerWeek;

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
          // 방문을 처리하기 위해 visited item의 url이 필요함
          // url을 callback arg로 넣기 위해 클로저 사용하여 바인딩
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
      }
    });


  // 사용자가 해당 URL을 옴니박스에 입력한 횟수만큼 URL을 매핑
  var urlToCount = {};

  // chrome.history.getVisits() 콜백.
  // 유저가 URL을 타이핑하여 방문한 횟수를 카운트
  var processVisits = function(url, visitItems) {
    for (var i = 0, ie = visitItems.length; i < ie; ++i) {
      let tmp = "type : " + visitItems[i].transition + " | url : " + url
      urlToCount[tmp] = 0;
    //   // 사용자가 URL을 입력하지 않은 항목 무시
    //   if (visitItems[i].transition != 'typed') {
    //     continue;
    //   }

    //   if (!urlToCount[url]) {
    //     urlToCount[url] = 0;
    //   }

    //   urlToCount[url]++;
    // }
    }

    // 만일 이것이 ProcessVisits()에 대한 최종 outstanding 호출이라면, 최종 결과를 얻는다. 
    // 팝업에 표시할 URL 목록을 작성하려면 이 목록을 사용
    if (!--numRequestsOutstanding) {
      onAllVisitsProcessed();
    }
  };

  // 디스플레이 할 최종 url 리스트가 있을 경우 호출됨
  var onAllVisitsProcessed = function() {

    urlArray = [];
    for (var url in urlToCount) {
      urlArray.push(url);
    }
    
    // 유저 typed url 중 횟수가 많은 순으로 소팅
    urlArray.sort(function(a, b) {
      return urlToCount[b] - urlToCount[a];
    });

    buildPopupDom(divName, urlArray.slice(0, 10));
  };
}

document.addEventListener('DOMContentLoaded', function () {
  buildTypedUrlList("typedUrl_div");
});