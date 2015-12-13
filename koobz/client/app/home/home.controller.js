'use strict';

angular.module('koobzApp')
  .controller('HomeCtrl', ['$scope', '$location', 'BoardGateway', function($scope, $location, boardGateway) {
    
    $scope.name = null;

    $scope.create = function() {
      boardGateway.persist($scope.name).then(function(result) {
        $location.path('/boards/' + result.id);
      });
    };

  }]);
