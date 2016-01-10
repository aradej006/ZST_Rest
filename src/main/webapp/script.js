angular.module('snmp', ['ngAnimate', 'ui.bootstrap'])
    .controller('snmpCtrl', function ($scope, $http) {

        $scope.getOid = function (oid) {
            $http.get('rest/snmp/getVariable/' + oid).success(function (result) {
                console.log( result );
                $scope.oidValue = result[0];
            }).error(function (error) {
                console.log(error);
            });
        };

        $scope.getValue = function(oid){
            return $http.get('rest/snmp/getVariable/' + oid);
        };

        $scope.getTable2 = function(oid){
            return $http.get('rest/snmp/getTable/' + oid);
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

        $scope.getValues = function(){
            $scope.getValue("1.3.6.1.2.1.1.3.0").success(function(result){
                $scope.sysUpTime = result[0];
            });
            $scope.getValue("1.3.6.1.2.1.1.5.0").success(function(result){
                $scope.sysName = result[0];
            });
            $scope.getValue("1.3.6.1.2.1.1.1.0").success(function(result){
                $scope.systemDate = result[0];
            });

            $scope.getTable2("1.3.6.1.2.1.4.20").success(function(result){
                $scope.ipElements = [];
                $scope.ipHeaders = [{value:"ipAdEntAddr"},
                    {value:"ipAdEntIfIndex"},
                    {value:"ipAdEntNetMask"},
                    {value:"ipAdEntBcastAddr"},
                    {value:"ipAdEntReasmMaxSize"}];
                for(var i = 0;i<result.length;i++){
                    $scope.ipElements.push([]);
                    for(var j = 0;j<result[i].length;j++){
                        $scope.ipElements[i].push({value:result[i][j]});
                    }
                }
            });
            $scope.getTable2("1.3.6.1.2.1.6.13").success(function(result){
                $scope.tcpElements = [];
                $scope.tcpHeaders = [{value:"tcpConnState"},
                    {value:"tcpConnLocalAddress"},
                    {value:"tcpConnLocalPort"},
                    {value:"tcpConnRemAddress"},
                    {value:"tcpConnRemPort"}];
                for(var i = 0;i<result.length;i++){
                    $scope.tcpElements.push([]);
                    for(var j = 0;j<result[i].length;j++){
                        $scope.tcpElements[i].push({value:result[i][j]});
                    }
                }
            });
            $scope.getTable2("1.3.6.1.2.1.7.5").success(function(result){
                $scope.udpElements = [];
                $scope.udpHeaders = [{value:"udpLocalAddress"},
                    {value:"udpLocalPort"}];
                for(var i = 0;i<result.length;i++){
                    $scope.udpElements.push([]);
                    for(var j = 0;j<result[i].length;j++){
                        $scope.udpElements[i].push({value:result[i][j]});
                    }
                }
            });
            $scope.getTable2("1.3.6.1.2.1.25.2.3").success(function(result){
                $scope.hrStorageElements = [];
                $scope.hrStorageHeaders = [{value:"hrStorageIndex"},
                    {value:"hrStorageType"},
                    {value:"hrStorageDescr"},
                    {value:"hrStorageAllocationUnit"},
                    {value:"hrStorageSize"},
                    {value:"hrStorageUsed"},
                    {value:"hrStorageAllocationFaliures"}];
                for(var i = 0;i<result.length;i++){
                    $scope.hrStorageElements.push([]);
                    for(var j = 0;j<result[i].length;j++){
                        $scope.hrStorageElements[i].push({value:result[i][j]});
                    }
                }
            });
            $scope.getTable2("1.3.6.1.2.1.25.3.6").success(function(result){
                $scope.hrDiskElements = [];
                $scope.hrDiskHeaders = [{value:"hrDiskStorageAccess"},
                    {value:"hrDiskStorageMedia"},
                    {value:"hrDiskStorageRemoveble"},
                    {value:"hrDiskStorageCapacity"}];
                for(var i = 0;i<result.length;i++){
                    $scope.hrDiskElements.push([]);
                    for(var j = 0;j<result[i].length;j++){
                        $scope.hrDiskElements[i].push({value:result[i][j]});
                    }
                }
            });
        };

        $scope.getValues();


    });