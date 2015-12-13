'use strict';

angular.module('koobzApp')
  .controller('HomeCtrl', ['$window', '$scope', '$location', 'sluggerizer', 'BoardGateway', function($window, $scope, $location, sluggerizer, boardGateway) {

    $window.document.title = 'Koobz';

    $scope.name = null;

    $scope.create = function() {
      boardGateway.persist($scope.name).then(function(result) {
        $location.path('/boards/' + result.id + '/' + sluggerizer.slug($scope.name));
      });
    };

  }]);
