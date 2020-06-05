app.controller('cartController',function ($scope,cartService) {

    $scope.findCartList = function () {
        cartService.findCartList().success(function (resp) {
            $scope.cartList = resp;
            $scope.countTotal();
        });
    }

    $scope.addCartList = function (itemId,num) {
        cartService.addCartList(itemId,num).success(function (resp) {
            if(resp.success){
                $scope.findCartList();
            }else{
                alert("添加失败")
            }
        })
    }

    $scope.total = {countNum:0,countTotalFee:0};
    
    $scope.countTotal = function () {
        $scope.total = {countNum:0,countTotalFee:0};

        for(var i = 0 ; i < $scope.cartList.length ;i++){
            var orderItemList = $scope.cartList[i].orderItemList;
            for(var j = 0 ; j < orderItemList.length ; j++){
                var orderItem  = orderItemList[j];
                $scope.total.countNum += orderItem.num;
                $scope.total.countTotalFee += orderItem.totalFee;
            }
        }
    }
});