 //商品控制层 
app.controller('goodsController' ,function($scope,$controller   ,goodsService,itemCatService){
	
	$controller('baseController',{$scope:$scope});//继承

	$scope.updateIsMarketable = function(isMarketable){
		goodsService.updateIsMarketable($scope.selectIds,isMarketable).success(function (resp) {
			if(resp.success){
                $scope.reloadList();
                $scope.selectIds = [];
			}else{
				alert('操作失败')
			}
        });
	}

	$scope.updateAuditStatus = function(auditStatus){
		goodsService.updateAuditStatus($scope.selectIds,auditStatus).success(function (resp) {
			if(resp.success){
				$scope.reloadList();
				$scope.selectIds = [];
			}else{
				alert(resp.message);
			}
        });
	}


    $scope.status = ['未审核','已审核','驳回','关闭'];// 0  1  2 3

	$scope.categoryName = [];
	$scope.findAllCategory = function(){
		//商品分类id  商品分类名称
		itemCatService.findAll().success(function (resp) {
			// resp = []
			for(var i = 0 ; i < resp.length ; i++){
				var category = resp[i];
				$scope.categoryName[category.id] = category.name;
			}
        });
	}

	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}

	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	