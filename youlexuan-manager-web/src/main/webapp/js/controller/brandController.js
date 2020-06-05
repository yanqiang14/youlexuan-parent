/*控制层*/
/*brandController注入brandService*/
app.controller('brandController',function ($scope,$http,brandService,$controller) {

    //继承baseController
    $controller('baseController',{$scope:$scope});

    $scope.findAll = function () {
        // $http.get("../brand/findAll.do").success(function (resp) {
        brandService.findAll().success(function (resp) {
            $scope.brandList = resp;//resp返回的数据
        });
    };

    $scope.findPage = function(pageNum,pageSize){
        // $http.get("../brand/findPage.do?pageNum="+pageNum+"&pageSize="+pageSize).success(function (resp) {
        brandService.findPage(pageNum,pageSize).success(function (resp) {
            //resp == PageResult
            $scope.paginationConf.totalItems = resp.total;//总记录数，赋值给paginationConf.totalItems
            $scope.brandList = resp.rows;//数据集合
        });
    };




    //修改和添加，都要执行save方法，所有我么在save中判断，是需要完成添加还是修改，根据id
    $scope.save = function () {

        if($scope.entity.id==null){
            //添加
            // $http.post("../brand/add.do",$scope.entity).success(function (resp) {
            brandService.add($scope.entity).success(function (resp) {
                //resp == Result (success,message)
                if(resp.success){
                    //添加成功，重新加载数据
                    $scope.reloadList();
                    //添加成功，entity清空
                    $scope.entity = {};
                }else {
                    alert(resp.message)
                }
            });
        }else{
            //entity有id，完成修改
            // $http.post("../brand/update.do",$scope.entity).success(function (resp) {
            brandService.update($scope.entity).success(function (resp) {
                if(resp.success){
                    $scope.reloadList();
                    $scope.entity = {};
                }else{
                    alert(resp.message);
                }
            });
        }
    }

    $scope.findOne = function (id) {
        // $http.get("../brand/findOne.do?id="+id).success(function (resp) {
        brandService.findOne(id).success(function (resp) {
            //resp == 品牌对象
            $scope.entity = resp;
        });
    }



    $scope.delete = function () {
        // $http.get("../brand/delete.do?ids="+$scope.selectIds).success(function (resp) {
        brandService.delete($scope.selectIds).success(function (resp) {
            if(resp.success){
                $scope.reloadList();
                $scope.selectIds = [];
            }else{
                alert(resp.message)
            }
        });
    };

    //初始化searchEntity
    $scope.searchEntity = {};

    $scope.search = function (pageNum,pageSize) {
        // $http.post("../brand/search.do?pageNum="+pageNum+"&pageSize="+pageSize,$scope.searchEntity).success(function (resp) {
        brandService.search(pageNum,pageSize,$scope.searchEntity).success(function (resp) {
            // resp = PageResult
            $scope.paginationConf.totalItems = resp.total;//总记录数，赋值给paginationConf.totalItems
            $scope.brandList = resp.rows;//数据集合
        });
    };






});