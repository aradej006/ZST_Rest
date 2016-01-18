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
            $scope.getValue("1.3.6.1.2.1.6.1.0").success(function(result){
                $scope.tcpRtoAlgorithm = result[0];
            });
            $scope.getValue("1.3.6.1.2.1.6.2.0").success(function(result){
                $scope.tcpRtoMin = result[0];
            });
            $scope.getValue("1.3.6.1.2.1.6.3.0").success(function(result){
                $scope.tcpRtoMax = result[0];
            });
            $scope.getValue("1.3.6.1.2.1.6.4.0").success(function(result){
                $scope.tcpMaxConn = result[0];
            });
            $scope.getValue("1.3.6.1.2.1.6.5.0").success(function(result){
                $scope.tcpActiveOpens = result[0];
            });
            $scope.getValue("1.3.6.1.2.1.6.6.0").success(function(result){
                $scope.tcpPassiveOpens = result[0];
            });
            $scope.getValue("1.3.6.1.2.1.6.7.0").success(function(result){
                $scope.tcpAttemptFails = result[0];
            });
            $scope.getValue("1.3.6.1.2.1.6.8.0").success(function(result){
                $scope.tcpEstabResets = result[0];
            });
            $scope.getValue("1.3.6.1.2.1.6.9.0").success(function(result){
                $scope.tcpCurrEstab = result[0];
            });
            $scope.getValue("1.3.6.1.2.1.6.10.0").success(function(result){
                $scope.tcpInSegs = result[0];
            });
            $scope.getValue("1.3.6.1.2.1.6.11.0").success(function(result){
                $scope.tcpOutSegs = result[0];
            });
            $scope.getValue("1.3.6.1.2.1.6.12.0").success(function(result){
                $scope.tcpRetransSegs = result[0];
            });
            $scope.getValue("1.3.6.1.2.1.6.14.0").success(function(result){
                $scope.tcpInErrs = result[0];
            });
            $scope.getValue("1.3.6.1.2.1.6.15.0").success(function(result){
                $scope.tcpOutRsts = result[0];
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

        };

        $scope.getValues();


    });