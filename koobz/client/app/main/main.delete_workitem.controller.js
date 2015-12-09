angular.module('koobzApp').controller('DeleteWorkItemCtrl', function ($scope, $modalInstance, workItem) {

  $scope.workItem = workItem;

  $scope.delete = function () {
    $modalInstance.close($scope.workItem);
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});
