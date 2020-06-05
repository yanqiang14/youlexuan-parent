app.controller('itemController',function ($scope) {

    $scope.num = 1;//购买的数量
    $scope.addNum = function (num) { // +1  -1
        $scope.num = $scope.num + num;
        if($scope.num < 1){
            $scope.num = 1;
        }
    }

    $scope.specificationItems = {};//用来记录你所点击勾选的规格选项
    // 规格名称，选项名称
    $scope.addSpecOption = function (key, value) {
        $scope.specificationItems[key] = value;

        for(var i = 0 ; i < skuList.length ; i++){
            var bol = $scope.checkSpecificationItemsAndSpec(skuList[i].spec,$scope.specificationItems);
            if(bol){
                $scope.sku = skuList[i];
            }
        }
    }

    //判断当前规格选项是否被选中 true false
    $scope.checkOptionIsSelect = function (specName, optionName) {
        //判断当前这组的规格+选项是否出现在$scope.specificationItems
        // {"网络":"移动3G","机身内存":"16G"}                网络,联通5G
        if($scope.specificationItems[specName] == optionName){
            return true;
        }
        return false;
    }

    $scope.loadSku = function () {
        $scope.sku = skuList[0];//sku.spec = {"网络":"移动3G","机身内存":"16G"}
        // $scope.specificationItems = $scope.sku.spec ;
        $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
        // 深克隆  {id:'',name:''}   json1  json2
        //  list = List[user1(name=tom),user2,user3]
        // User u = List[0];
        // u.name = "tim"
        // List[0].name = tim
    }


    //{"网络":"移动4G","机身内存":"16G"}     {"网络":"移动4G","机身内存":"16G"}
    $scope.checkSpecificationItemsAndSpec = function (specificationItems,spec) {//{"网络":"移动4G","机身内存":"16G"}
        // {"网络":"移动3G","机身内存":"16G"}
        for(var key in specificationItems){ // 对json对象中的每一个key进行遍历，【网络，机身内存】
            if(specificationItems[key] != spec[key]){
                return false;
            }
        }

        for(var key in spec){
            if(spec[key] != specificationItems[key]){
                return false;
            }
        }
        return true;
    }

    $scope.addCart = function () {
        // 数量  skuid
        alert($scope.sku.id + "," + $scope.num)
    }

});