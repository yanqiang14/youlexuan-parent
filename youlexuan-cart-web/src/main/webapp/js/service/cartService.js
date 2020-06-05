app.service('cartService',function ($http) {
    this.findCartList = function () {
        return $http.get("../cart/findCartList.do");
    }

    this.addCartList = function (itemId, num) {
        return $http.get("../cart/addCart.do?itemId="+itemId+"&num="+num);
    }
});