(function () {
    'use strict';
	angular.module('rlmsApp')
	.controller('editMemberCtrl', ['$scope', '$filter','serviceApi','$route','$http','utility','$window','$rootScope', function($scope, $filter,serviceApi,$route,$http,utility,$window,$rootScope) {
		//initialize add Branch
		$scope.alert = { type: 'success', msg: 'You successfully Edited Member.',close:true };
		//loadBranchListInfo();
		$scope.showAlert = false;
		$scope.companies = [];

		function initEditMember(){
			$scope.selectedCompany = {};
			$scope.selectedActiveFlag = {};
			$scope.addMember={
					companyId:'',
					branchName:'',
					branchAddress:'',
					city:'',
					area:'',
					pinCode:'',
					contactNumber:'',
					emailId:'',
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
		//Post call add branch
		$scope.submitEditMember= function(){
			var memberData = {};
			memberData = {
					id:$rootScope.editMember.branchId,
					branchName:$scope.editMember.branchName,
					contactNumber:$scope.editUser.contactnumber,
					firstName:$scope.editMember.firstName,
					area:$scope.editMember.area,
					city:$scope.editMember.city,
					pinCode:$scope.editMember.pinCode,
					};
			serviceApi.doPostWithData("/RLMS/admin/editBranchInCompany",memberData)
			.then(function(response){
				$scope.showAlert = true;
				var key = Object.keys(response);
				var successMessage = response[key[0]];
				$scope.alert.msg = successMessage;
				$scope.alert.type = "success";
				$scope.addBranchForm.$setPristine();
				$scope.addBranchForm.$setUntouched();
			},function(error){
				$scope.showAlert = true;
				$scope.alert.msg = error.exceptionMessage;
				$scope.alert.type = "danger";
			});
		}
		//rese add branch
		$scope.resetAddBranch = function(){
			//initAddBranch();
		}
		$scope.backPage =function(){
			 $window.history.back();
		}
	}]);
})();
