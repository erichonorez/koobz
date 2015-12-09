'use strict';

angular.module('koobzApp')
  .controller('HomeCtrl', ['$scope', '$location', 'boardService', function($scope, $location, boardService) {
    
    $scope.name = null;

    $scope.create = function() {
      boardService.newBoard($scope.name);
      $location.path('/boards/1');
    };

  }]);