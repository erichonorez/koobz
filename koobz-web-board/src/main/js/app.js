'use strict';

angular.module('koobzApp', [
  'ngResource',
  'ngSanitize',
  'ngRoute',
  'ui.bootstrap',
  'ngMaterial',
  'ngAnimate',
  'ngAria',
  'ngMessages',
  'dndLists',
  'underscore',
  'sluggerizer'
])
  .config(function ($routeProvider, $locationProvider, $mdThemingProvider) {
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

    $mdThemingProvider.theme('default')
        .primaryPalette('blue');
  });
