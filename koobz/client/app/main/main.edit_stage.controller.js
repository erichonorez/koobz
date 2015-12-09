angular.module('koobzApp').controller('EditStageCtrl', function ($scope, $modalInstance, stage) {

  $scope.stage = stage;

  $scope.add = function () {
    $modalInstance.close($scope.stage);
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});
