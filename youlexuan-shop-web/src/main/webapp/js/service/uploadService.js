app.service('uploadService',function ($http) {

    this.upload = function () {
        //entity  id name
        /*$http.get()
        $http.post();*/

        var formData = new FormData();
        formData.append("file",file.files[0]);

        return $http({
            url:'../upload.do',
            method:'post',
            data:formData,
            headers: {'Content-Type':undefined},
            transformRequest: angular.identity
        });
    }

})