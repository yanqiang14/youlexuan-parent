app.controller('baseController',function ($scope) {

    //分页控件的模型数据（参数：当前第几页，每页多少条，总记录数，reload重新加载页面数据）
    $scope.paginationConf = {
        currentPage:1,  //pageNum默认1
        totalItems:10,//总记录数
        itemsPerPage:5,//默认每页显示5条
        perPageOptions:[5,10,15,20],//下拉框的数据
        onChange:function () {
            //onChange的作用，就是当 currentPage，itemsPerPage参数改变时，需要重新加载数据
            //页面加载后，自动就来执行，默认查询第一页的5条
            $scope.reloadList();//页面加载，会自动执行，所以在html上不需要ng-init调用方法
        }
    };

    //记录表格中被勾选的行的id
    $scope.selectIds = [];

    //定义方法，绑定给复选框，用来记录id（勾选，取消勾选）
    $scope.updateSelection = function ($event,id) {
        if($event.target.checked==true){//$event对象可以用来判断，本次点击复选框，是勾选还是取消勾选
            $scope.selectIds.push(id);//选中，将当前行的id，存入到selectIds数组中
        }else{
            //从数组中，把当前行的id移除
            //判断，id所在位置
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index,1);
        }
    };


    //添加成功，修改成功，删除成功  后，需要重新加载页面
    $scope.reloadList = function () {
        // $scope.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);//这个方法不能进行提取，不同模块的search方法请求的后台接口不一样
    };


});