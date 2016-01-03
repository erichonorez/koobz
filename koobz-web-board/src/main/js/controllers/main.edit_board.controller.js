angular.module('koobzApp').controller('EditBoardCtrl', function ($scope, $modalInstance, board) {

  $scope.board = board;

  $scope.edit = function () {
    $modalInstance.close($scope.board);
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});
