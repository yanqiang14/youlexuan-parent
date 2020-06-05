app.controller('indexController',function ($scope,loginService) {
    $scope.getUserName = function () {
        loginService.getUserName().success(function (resp) {
            $scope.loginName = resp.loginName;
        });
    }
});