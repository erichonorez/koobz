'use strict';

angular.module('koobzApp', [
  'ngCookies',
  'ngResource',
  'ngSanitize',
  'ngRoute',
  'ui.bootstrap',
  'dndLists'
])
  .config(function ($routeProvider, $locationProvider) {
    $routeProvider
      .when('/boards/:boardId', {
        templateUrl: 'app/main/main.html',
        controller: 'MainCtrl',
            resolve: {
                board: function($route, BoardGateway) {
                    return BoardGateway.find($route.current.params.boardId);
                }
            }
      })
      .when('/', {
        templateUrl: 'app/home/home.html',
        controller: 'HomeCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });

    $locationProvider.html5Mode(true);
  });
