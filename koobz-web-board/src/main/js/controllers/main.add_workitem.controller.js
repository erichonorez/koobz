angular.module('koobzApp').controller('AddWorkItemCtrl', function ($scope, $modalInstance, workItem, title, submitBtnLabel) {

  $scope.title = title;
  $scope.submitBtnLabel = submitBtnLabel;
  $scope.workItem = workItem;

  $scope.add = function () {
    $modalInstance.close($scope.workItem);
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});
