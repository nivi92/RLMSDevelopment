(function () {
    'use strict';
	angular.module('rlmsApp')
	.controller('addBranchCtrl', ['$scope', '$filter','serviceApi','$route','$http','utility','$window','$rootScope', function($scope, $filter,serviceApi,$route,$http,utility,$window,$rootScope) {
		//initialize add Branch
		initAddBranch();
		loadCompayInfo();
		$scope.alert = { type: 'success', msg: 'You successfully Added Branch.',close:true };
		$scope.alert = { type: 'error', msg: 'Please fill the Required Field',close:true };
		//loadBranchListInfo();
		$scope.showCompany = false;
		$scope.showAlert = false;
		$scope.companies = [];
		function initAddBranch(){
			$scope.selectedCompany = {};
			$scope.addBranch={
					companyId:'',
					branchName:'',
					branchAddress:'',
					city:'',
					area:'',
					pinCode:'',
			};	
		    $scope.branchList={};
		    		    
		}
		//load compay dropdown data
		function loadCompayInfo(){
			serviceApi.doPostWithoutData('/RLMS/admin/getAllApplicableCompanies')
		    .then(function(response){
		    		$scope.companies = response;
		    });
		};
		if($rootScope.loggedInUserInfo.data.userRole.rlmsSpocRoleMaster.roleLevel ==1){
			$scope.showCompany=true;
			loadCompayInfo();
		}else{
			$scope.showCompany=false;
		}
		//Post call add branch
		$scope.submitAddBranch = function(){
			if($rootScope.loggedInUserInfo.data.userRole.rlmsSpocRoleMaster.roleLevel ==1){
				$scope.addBranch.companyId = $scope.selectedCompany.selected.companyId;
			}else{
				$scope.addBranch.companyId = $rootScope.loggedInUserInfo.data.userRole.rlmsCompanyMaster.companyId;
			}
			serviceApi.doPostWithData("/RLMS/admin/addNewBranchInCompany",$scope.addBranch)
			.then(function(response){
				$scope.showAlert = true;
				var key = Object.keys(response);
				var successMessage = response[key[0]];
				$scope.alert.msg = successMessage;
				$scope.alert.type = "success";
				initAddBranch();
				$scope.addBranchForm.$setPristine();
				$scope.addBranchForm.$setUntouched();
			},function(error){
				$scope.showAlert = true;
				$scope.alert.msg = error;
				$scope.alert.type = "danger";
			});
		}
		//rese add branch
		$scope.resetAddBranch = function(){
			initAddBranch();
		}
		$scope.backPage =function(){
			 $window.history.back();
		}
	}]);
})();
