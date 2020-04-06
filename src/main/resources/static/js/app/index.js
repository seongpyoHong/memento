var index = {
    init : function () {
        var _this = this;
        $('#btn-search').on('click', function () {
            _this.search();
        });
    },
    search : function () {
        var keyword = $('#keyword').val();
        var userName = $('#userName').val();
        var destUrl = '/search?page=0&name=' + userName + '&keyword='+keyword;
        console.log(destUrl);
        window.location.href = destUrl;
    },
};

index.init();