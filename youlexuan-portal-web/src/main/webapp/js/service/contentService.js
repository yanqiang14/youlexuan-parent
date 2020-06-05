app.service('contentService1',function ($http) {
    this.listContentByCategoryId = function (catagoryId) {
        return $http.get('../index/listContentByCategoryId.do?categoryId='+catagoryId);
    }
})