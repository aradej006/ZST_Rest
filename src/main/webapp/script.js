angular.module('snmp', [])
    .controller('snmpCtrl', function ($scope, $http) {

        $scope.getOid = function (oid) {
            $http.get('rest/snmp/getVariable/' + oid).success(function (result) {
                console.log( result );
                $scope.oidValue = result[0];
            }).error(function (error) {
                console.log(error);
            });
        };

        $scope.getTable = function (oid) {
            $http.get('rest/snmp/getTable/' + oid).success(function (result) {
                $scope.oids = [];
                $scope.headers = [];
                for(var i = 0;i<result.length;i++){
                    $scope.oids.push([]);
                    for(var j = 0;j<result[i].length;j++){
                        $scope.oids[i].push({value:result[i][j]});
                        $scope.headers[j] = {value:'Column'+(j+1)};
                    }
                }
                console.log($scope.oids);


            }).error(function (error) {
                console.log(error);
            });
        };


    });