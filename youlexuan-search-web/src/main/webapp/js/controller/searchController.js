app.controller('searchController',function($scope,searchService,$location){

    //查询条件
    $scope.searchMap = {category:'',keywords:'',brand:'',spec:{},price:'',sort:{}};

    $scope.getIndexKeywords = function(){
        var keywords = $location.search()['keywords'];
        $scope.searchMap.keywords = keywords;
        $scope.search();
    }

    // 检查每一个品牌，是否在keywords中出现
    $scope.checkedKeywordsContailsBrand = function(){
        var keywords  = $scope.searchMap.keywords;
        keywords = keywords.replace(' ','');
        for(var i=0 ; i < $scope.resultMap.brandList.length ;i++){
            var brand = $scope.resultMap.brandList[i].text;
            if(keywords.indexOf(brand)>0){
                return true;
            }
        }
        return false;
    }

    $scope.resultMap = {brandList:[],categoryList:[]};

    $scope.addSort = function(sortField,direction){

        if(sortField == 'zonghe'){

            $scope.searchMap.sort = {};

            $scope.search();
            return;
        }

        $scope.searchMap.sort[sortField] = direction;
        $scope.search();
    }

    //将选择的条件添加到searchMap

    // ‘brand’，联想
    //’category‘，平板电视
    // 网络，机身内存
    $scope.addCondition = function(key,value){
        if(key == 'category' || key == 'brand' || key=='price'){
            $scope.searchMap[key] = value;
        }else{
            //规格
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    }

     $scope.removeCondition = function(key){
        if(key == 'category' || key == 'brand' || key=='price'){
            $scope.searchMap[key] = '';
        }else{
            delete $scope.searchMap.spec[key];
        }

         $scope.search();
    }

    //搜索
    $scope.search=function(){
        searchService.search( $scope.searchMap ).success(
            function(response){
                $scope.resultMap=response;//搜索返回的结果
                // $scope.checkedKeywordsContailsBrand();
                // alert($scope.checkedKeywordsContailsBrand())
            }
        );
    }
});