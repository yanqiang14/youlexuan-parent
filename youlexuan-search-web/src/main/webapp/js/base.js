var app = angular.module('youlexuan',[]);//没有分页组件的app模块
app.filter('trustHtml',['$sce',function($sce){
    return function(data){
        return $sce.trustAsHtml(data);
    }
}]);