'use strict';

angular.module('koobzApp', [
  'ngResource',
  'ngSanitize',
  'ngRoute',
  'ui.bootstrap',
  'dndLists',
  'underscore',
  'sluggerizer'
])
  .config(function ($routeProvider, $locationProvider) {
    $routeProvider
      .when('/boards/:boardId/:boardName', {
        templateUrl: 'html/main/main.html',
        controller: 'MainCtrl',
            resolve: {
                board: function($route, BoardGateway) {
                    return BoardGateway.find($route.current.params.boardId);
                }
            }
      })
      .when('/', {
        templateUrl: 'html/home/home.html',
        controller: 'HomeCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });

    $locationProvider.html5Mode(true);
  });