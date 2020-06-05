 //商品类目控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承

	//新增分类，关联模板 下拉框显示的数据
	//查询模板
	// $scope.typeList = [{id:101,name:"模板1",specIds:[]},{id:102,name:"模板2"},{id:103,name:"模板3"}];

	$scope.findTypeList = function(){
		typeTemplateService.findAll().success(function (resp) {
			$scope.typeList = resp;
        });
	}


	$scope.grade = 1;//用来记录当前分类的级别\

	$scope.pid = 0;//顶级分类 ， 记录当前列表的父id
	$scope.setPid = function(pid){
		$scope.pid = pid;
	}

	$scope.entity1 = {};
	$scope.entity2 = {};

	$scope.setGrade = function(newGrade){
		$scope.grade = newGrade;
	}

    /**
	 *
     * @param entity 商品分类
     */
	$scope.selectList = function(entity){

		if($scope.grade == 1){
            $scope.entity1 = {};
            $scope.entity2 = {};
		}
        if($scope.grade == 2){
            $scope.entity1 = entity;
            $scope.entity2 = {};
        }

        if($scope.grade == 3){
            // $scope.entity1 = entity;
			$scope.entity2 = entity;
        }

        $scope.findByParentId(entity.id);
	}

	$scope.findByParentId = function(pid){
		itemCatService.findByParentId(pid).success(function (resp) {
			//resp []
			$scope.list = resp;
        });
	}
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
                    // $scope.reloadList();//重新加载
					//刷新当前分类列表
					$scope.findByParentId($scope.pid);
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
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
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	