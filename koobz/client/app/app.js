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
        controller: 'MainCtrl'
      })
      .when('/', {
        templateUrl: 'app/home/home.html',
        controller: 'HomeCtrl'
      })
      .otherwise({
        redirectTo: '/boards/1'
      });

    $locationProvider.html5Mode(true);
  });
