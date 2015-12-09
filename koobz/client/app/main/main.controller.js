'use strict';

angular.module('koobzApp')
  .controller('MainCtrl', ['$scope', '$modal', 'boardService', function ($scope, $modal, boardService) {

    // var BoardEndpoint = $resource('/boards/:boardId', { boardId: '@boardId' } );
    // var WorkItemEndPoint = $resource('/boards/:boardId/workitems/:workItemId', { boardId: '@boardId', workItemId: '@workItemId' });
    // var StageEndPoint = $resource('/boards/:boardId/stages/:stageId', { boardId: '@boardId', stageId: '@stageId' });

    $scope.selected = null;

    // BoardEndpoint.get({boardId: 1}).$promise.then(function(board) {
    //   $scope.board = board;
    // });

    $scope.board = boardService.getCurrentBoard();

    $scope.deleteWorkItem = function (workItem) {
      var modalInstance = $modal.open({
        templateUrl: 'delete_workitem.html',
        controller: 'DeleteWorkItemCtrl',
        resolve: {
          workItem: function() {
            return workItem
          }
        }
      });
      modalInstance.result.then(function() {
        $scope.board.removeWorkItem(workItem);
      });
    };

    $scope.editBoard = function() {
      var modalInstance = $modal.open({
        templateUrl: 'edit_board.html',
        controller: 'EditBoardCtrl',
        resolve: {
          board: function() {
            return $scope.board;
          }
        }
      });
      modalInstance.result.then(function() {
        // update board
      });
    }

    $scope.editWorkItem = function (workItem) {
      var modalInstance = $modal.open({
        templateUrl: 'add_workitem.html',
        controller: 'AddWorkItemCtrl',
        resolve: {
          workItem: function() {
            return workItem;
          },
          title: function() {
            return 'Edit work item';
          },
          submitBtnLabel: function() {
            return 'Save'
          }
        }
      });
      modalInstance.result.then(function(editedWorkItem) {
        boardService.addWorkItem(workItem);
      });
    };

    $scope.editStage = function (stage) {
      var modalInstance = $modal.open({
        templateUrl: 'edit_stage.html',
        controller: 'EditStageCtrl',
        resolve: {
          stage: function () {
            return stage;
          }
        }
      });
      modalInstance.result.then(function(stage) {
        //boardService.updateStage(stage);
      });
    };

    $scope.addStage = function() {
      var modalInstance = $modal.open({
        templateUrl: 'edit_stage.html',
        controller: 'EditStageCtrl',
        resolve: {
          stage: function () {
            return { 
              id: null,
              name: null,
              workitems: []
            };
          }
        }
      });
      modalInstance.result.then(function(stage) {
        $scope.board.stages.push(stage);
      });
    }

    $scope.addWorkItem = function () {
      var modalInstance = $modal.open({
        templateUrl: 'add_workitem.html',
        controller: 'AddWorkItemCtrl',
        resolve: {
          workItem: function () {
            var workItem = {
              id: null,
              title: null,
              description: null
            };
            return workItem;
          },
          title: function () {
            return 'New work item';
          },
          submitBtnLabel: function() {
            return 'Add'
          }
        }
      });
      modalInstance.result.then(function(workItem) {
        //boardService.updateWorkItem(workItem);
        $scope.board.stages[0].workitems.push(workItem);
      });
    };

    $scope.dropCallback = function(event, index, item) {
      //console.log(event)
      //console.log(index, item, $(event.path[0]).parents('.stage')[0].dataset.stageId);
      return item;
    };

  }]);
