app.controller('indexController',function ($scope,contentService1) {

    $scope.catagoryList = [];

    // catagoryList[1]  轮播图广告

    $scope.toSearch = function(){
        var keywords = $scope.keywords;
        window.location.href = "http://localhost:9104/search.html#?keywords="+keywords;
    }

    $scope.listContentByCategoryId = function (catagoryId) {
        contentService1.listContentByCategoryId(catagoryId).success(function (resp) {
            //resp 指定分类的广告
            $scope.catagoryList[catagoryId] = resp;
        })
    }
});