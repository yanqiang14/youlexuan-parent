 //商品控制层 
app.controller('goodsController' ,function($scope,$controller  , $location ,goodsService,uploadService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承

	//定义方法，让规格选项回显 checked = true
	// 规格名称  规格选项名称
    // [{"attributeValue":["移动4G","移动3G"],"attributeName":"网络"},{"attributeValue":["32G","16G"],"attributeName":"机身内存"}]
	// 网络  联通5G
	$scope.checkedOption = function(specName,optionName){
		//返回true  false
		//找到specName对应的json
		var json = $scope.selectJsonFromSpecificationItemsBySpecName(specName);
		if(json == null){
			return false;
		}else{
			//json存在，在数组attributeValue是否存在 optionName
			var i = json.attributeValue.indexOf(optionName);
			if(i<0){
				return false;
			}else{
				return true;
			}
		}
	}


	$scope.getGoodsVO = function(){
		// 获取  <a href="goods_edit.html#?id={{entity.id}}" class="btn bg-olive btn-xs">修改</a>
		var id = $location.search()['id'];
		// alert(id)

		goodsService.findOne(id).success(function (resp) {
			$scope.entity = resp;
			//回显富文本编辑器
			editor.html($scope.entity.goodsDesc.introduction);
			//图片
			$scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
			//扩展属性
            // $scope.entity.goodsDesc.customAttributeItems =  JSON.parse($scope.typeTemplate.customAttributeItems); 模板id有变化，就会执行 【{text}，{}】
            $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);//页面加载即执行 【{text，value}，{}】
            $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);//页面加载即执行 【{text，value}，{}】

			//规格  spec   skuList

			for(var i = 0 ; i < $scope.entity.skuList.length ; i++){
				$scope.entity.skuList[i].spec = JSON.parse($scope.entity.skuList[i].spec);
			}

        });
	}

	//spec  {"机身内存":"16G","网络":"联通3G"}
    $scope.entity = {goods:{isEnableSpec:0},goodsDesc:{itemImages:[],specificationItems:[]} , skuList:[]};

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

    //选择的规格选项
	/*
		[
			{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},
			{"attributeName":"机身内存","attributeValue":["16G","32G"]}
		]
	*/


    /*
				{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},
					skuList =
							{spec:{网络制式:移动3G},price:0,num:99999,status:'0',isDefault:'0' }
							{spec:{网络制式:移动4G},price:0,num:99999,status:'0',isDefault:'0' }

				{"attributeName":"机身内存","attributeValue":["16G","32G"]}
					skuList =
							{spec:{网络制式:移动3G,机身内存:16G},price:0,num:99999,status:'0',isDefault:'0' }
							{spec:{网络制式:移动3G,机身内存:32G},price:0,num:99999,status:'0',isDefault:'0' }

							{spec:{网络制式:移动4G,,机身内存:16G},price:0,num:99999,status:'0',isDefault:'0' }
							{spec:{网络制式:移动4G,机身内存:32G},price:0,num:99999,status:'0',isDefault:'0' }
    * */

    //创建sku列表
    $scope.createSkuList = function(){
		//每次勾选规格选项时，skuList被清空了
		//根据specificationItems，重头遍历，重新生成skuList
        $scope.entity.skuList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' } ];

        var  specificationItems = $scope.entity.goodsDesc.specificationItems; //数组【{}，{}】

    	for(var i = 0 ; i < specificationItems.length ; i++){
    		$scope.entity.skuList  = $scope.addSpec($scope.entity.skuList,specificationItems[i].attributeName,specificationItems[i].attributeValue);
		}

	}

	// attributeValue = ["移动3G","移动4G"]
	$scope.addSpec = function(skuList,attributeName,attributeValue){
        var arr = [];
    	for(var i = 0 ; i < skuList.length ; i++){
    		var oldRow = skuList[i];
    		for(var j = 0 ; j < attributeValue.length ; j++){
    			//生成新行
				var newRow = JSON.parse(JSON.stringify(oldRow));//深克隆
				//newRow的spec属性，添加一组规格
				newRow.spec[attributeName] = attributeValue[j];   // spec = {'网络制式':'移动3G'}
				arr.push(newRow);
			}
		}
		return arr;//新的sku列表
	}


    /*
         goodsDesc.specificationItems : 用来记录都勾选了哪些规格选项

        [
        	{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},  // 网络制式对应的json
			{"attributeName":"机身内存","attributeValue":["16G","32G"]}    // 机身内存对应的json
			{"attributeName":"颜色","attributeValue":['黑色','白色']}    // 机身内存对应的json
        ]
        */

    //勾选 规格选项 ，将它存入到指定json中的attributeValue数组中
	$scope.addOptionToJson = function($event,specName,option){ //颜色  白色
		//找到指定的json
		var json = $scope.selectJsonFromSpecificationItemsBySpecName(specName);

		if(json!=null){
			//判断勾选还是取消勾选
			if($event.target.checked){
				json.attributeValue.push(option);
			}else{
                json.attributeValue.splice(json.attributeValue.indexOf(option),1);
                //如果数组中一个选项都没有了，当前json就删除
				if(json.attributeValue.length == 0){
					//删除json
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(json),1);
				}
			}
		}else{
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":specName,"attributeValue":[option]} );
		}
	}

    //你在勾选一个新的规格选项的时候，你的找到当前规格对应的json，找到了之后，把你所勾选的规格选项，放到json中的attributeValue数组中
	//定义方法，用来寻找指定规格对应的json
	$scope.selectJsonFromSpecificationItemsBySpecName = function(specName){
		var list = $scope.entity.goodsDesc.specificationItems;
		for(var i = 0 ; i < list.length ;i++){
			var json = list[i];
			if(json.attributeName == specName){
				return json;
			}
		}
		return null;
	}

	//一级商品分类
	$scope.findItemCat1List = function(){
		itemCatService.findByParentId(0).success(function (resp) {
			$scope.itemCat1List = resp;
        });
	}

	$scope.$watch('entity.goods.category1Id',function (newValue, oldValue) {
		if(newValue){
			// alert(newValue)
			//newValue新的一级分类id
			itemCatService.findByParentId(newValue).success(function (resp) {
                $scope.itemCat2List = resp;
            });
		}
    });

    $scope.$watch('entity.goods.category2Id',function (newValue, oldValue) {
        if(newValue){
            // alert(newValue)
            //newValue新的一级分类id
            itemCatService.findByParentId(newValue).success(function (resp) {
                $scope.itemCat3List = resp;
            });
        }
    });

    $scope.$watch('entity.goods.category3Id',function (newValue, oldValue) {
    	if(newValue){
    		//当前三级分类改变时，查询该分类的typeid，赋值给goods.typeTemplateId
			itemCatService.findOne(newValue).success(function (resp) {
				//resp  第三级分类
				$scope.entity.goods.typeTemplateId = resp.typeId;
            });
        }
    })

    $scope.$watch('entity.goods.typeTemplateId',function (newValue, oldValue) {
    	if(newValue){
    		typeTemplateService.findOne(newValue).success(function (resp) {
				//resp 模板
				$scope.typeTemplate = resp;
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
                //添加时，执行
				// id
				var id = $location.search()['id'];
				if(id == null){
                    $scope.entity.goodsDesc.customAttributeItems =  JSON.parse($scope.typeTemplate.customAttributeItems);// [{text:'',value:''}]
				}

            });

    		typeTemplateService.findSpecAndOption(newValue).success(function (resp) {
				$scope.specList =  resp;
            });
		}
    })




	$scope.addImage = function(image_entity){
        $scope.entity.goodsDesc.itemImages.push(image_entity)
	}

	$scope.deleteImage = function(index){
        $scope.entity.goodsDesc.itemImages.splice(index,1);
	}

	$scope.image_entity = {};//{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/01/wKgZhVmHINyAQAXHAAgawLS1G5Y136.jpg"}

	$scope.upload = function(){
		uploadService.upload().success(function (resp) {
			if(resp.success){
				$scope.image_entity.url = resp.message;//url
			}else{
				alert(resp.message)
			}
        });
	}
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		/*var serviceObject;//服务层对象
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);*/
		if($scope.entity.goods.id == null){

            //提取富文本编辑器的内容
            $scope.entity.goodsDesc.introduction =  editor.html();

			goodsService.add($scope.entity).success(function (resp) {

				if(resp.success){
					alert('添加成功')
					// $scope.entity = {};//添加成功，情况页面

                    $scope.entity = {goods:{isEnableSpec:0},goodsDesc:{itemImages:[],specificationItems:[]} , skuList:[]};

					editor.html('');//清空富文本编辑器
				}else {
					alert(resp.message)
				}
            });
		}else{
            //提取富文本编辑器的内容
            $scope.entity.goodsDesc.introduction =  editor.html();
            goodsService.update($scope.entity).success(function (resp) {
				if(resp.success){
					alert('修改成功');
					window.location.href="goods.html";
				}else{
					alert('修改失败')
				}
            });
		}
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