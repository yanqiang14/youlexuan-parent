 //类型模板控制层 
app.controller('typeTemplateController' ,function($scope,$controller   ,typeTemplateService,$http){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;
				// response 中的brandIds  specIds customAttributeItems 都是字符串，我们需要转成json
                $scope.entity.brandIds = JSON.parse($scope.entity.brandIds);
                $scope.entity.specIds = JSON.parse($scope.entity.specIds);
                $scope.entity.customAttributeItems = JSON.parse($scope.entity.customAttributeItems);
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
                    $scope.entity = {customAttributeItems:[]};
                    // $scope.entity={};  第一次可以添加，后边就不行了
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.selectIds ).success(
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
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};

    // [{"id":16,"text":"TCL"},{"id":22,"text":"LG"},{"id":4,"text":"小米"}]  将字符串中的所有的text属性值取出来，拼成一个字符串
	//将json中的text属性取出
	$scope.jsonToStr = function (json) {

		if(json==null || json.length==0){
			return "暂无数据";
		}

		var s = [];
		var arr = JSON.parse(json);//json数组
		// JSON.stringify();//转成字符串
		for(var i = 0 ; i < arr.length ; i++){
			var obj = arr[i];//{"id":22,"text":"LG"}
			s.push(obj.text);//取出当前json对象的text属性值
		}
		return s.toString();// TCL，LG，小米
    };

	//讲义：  把属性做成了一个参数（动态）
	// str ： json数组字符串
	// key  ：属性名称
	$scope.jsonToStr2 = function(str,key){
		if (str==null || str.length ==0 ){
			return "暂无数据";
		}
		var  s = [];

		var arr = JSON.parse(str);
        for(var i = 0 ; i < arr.length ; i++){
			var obj = arr[i];//json对象
			//从json对象中取出key指定的属性
			s.push(obj[key]);
		}
		return s.toString();
	}

    $scope.entity = {customAttributeItems:[]};//初始了entity


    $scope.addTableRow = function () {
        $scope.entity.customAttributeItems.push({});//前提：初始化customAttributeItems属性
    }

    //从entity.specificationOptionList 数组中删除指定位置的元素
    $scope.deleteTableRow = function (index) {
        $scope.entity.customAttributeItems.splice(index,1);
    }


    $scope.brandList = {data:[]};
    $scope.specList = {data:[]};

    /*必须按照指定格式，去查询品牌列表和规格列表*/
	
	$scope.initSelect2 = function () {
		$http.get("../typeTemplate/findDataList.do").success(function (resp) {
			//resp = （）
            $scope.brandList.data = resp.brandList;
			$scope.specList.data = resp.specList;
        });
    }

});	