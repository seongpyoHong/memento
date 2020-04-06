var totalPage = document.getElementsByName('totalPage')[0].content;
var currentPage = document.getElementsByName('currentPage')[0].content;
var hasNext = document.getElementsByName('hasNext')[0].content;
var hasPrev = document.getElementsByName('hasPrev')[0].content;
var isFirst = document.getElementsByName('isFirst')[0].content;
var isLast = document.getElementsByName('isLast')[0].content;
var baseUrl = document.baseURI;

function paging() {
    var resultHTML = '';
    if (hasPrev ==='true') {
        resultHTML+= '<a href="' + getPrevPage() + '">&laquo;</a>';
    }
    resultHTML += getListHref();
    if (hasNext === 'true') {
        resultHTML +=  '<a href="' + getNextPage() + '">&raquo;</a>';
    }
    console.log(resultHTML);
    return resultHTML;
}

function getPrevPage() {
    return baseUrl.replace('page='+currentPage, 'page+'+(currentPage-1));
}

function getNextPage() {
    return baseUrl.replace('page='+currentPage, 'page+'+(currentPage+1));
}

function getPage(page) {
    var page = 'page='+page;
    return baseUrl.replace('page='+currentPage, page);
}

function getListHref() {
    var html = '';
    var end = 0;
    while(end<=currentPage) {
        end+=5;
    }
    var start = end-5;

    if (end > totalPage) {
        end = totalPage
    }
    for (; start<end ; start++) {
        if (start.toString() !== currentPage) {
            html += '<a href="' + getPage(start) +'">' + (start+1) + '</a>';
        } else {
            html += '<a href="' + getPage(start) +'" class="active">' + (start+1) + '</a>';
        }
    }
    return html;
}
document.write(paging());
