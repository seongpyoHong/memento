document.addEventListener('DOMContentLoaded', function() {
    var clicklistener = document.getElementById('btnStartWorker');
    // onClick's logic below:
    clicklistener.addEventListener('click', function() {
        var port = chrome.extension.connect({
            name: "Sample Communication"
        });
        // background 통신 On 메세지
        port.postMessage("On");
    });
});

document.addEventListener('DOMContentLoaded', function() {
    var clicklistener = document.getElementById('btnStopWorker');
    // onClick's logic below:
    clicklistener.addEventListener('click', function() {
        var port = chrome.extension.connect({
            name: "Sample Communication"
        });
        // background 통신 Off 메세지
        port.postMessage("Off");
    });
});