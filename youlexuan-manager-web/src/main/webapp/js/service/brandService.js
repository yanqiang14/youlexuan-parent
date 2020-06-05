/*自定义service服务层*/
/*在自定义服务中，注入内置服务$http*/
/*service中，定义一些方法，每个方法，都可以向后台发请求*/
/*servcie方法中，不对返回值进行处理，仅仅是发送一个请求*/
app.service('brandService',function ($http) {
    this.findAll = function () {
        return $http.get("../brand/findAll.do");
    };
    this.findPage = function (pageNum,pageSize) {
        return $http.get("../brand/findPage.do?pageNum="+pageNum+"&pageSize="+pageSize);
    };
    this.add = function (entity) {
        return $http.post("../brand/add.do",entity)
    };
    this.update = function (entity) {
        return $http.post("../brand/update.do",entity)
    };
    this.findOne = function (id) {
        return $http.get("../brand/findOne.do?id="+id);
    };
    this.delete = function (ids) {
        return $http.get("../brand/delete.do?ids="+ids)
    };
    this.search = function (pageNum,pageSize,searchEntity) {
        return $http.post("../brand/search.do?pageNum="+pageNum+"&pageSize="+pageSize,searchEntity);
    }
});