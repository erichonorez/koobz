'use strict';

angular.module('koobzApp')
  .controller('MainCtrl', [
     '$window', '$scope', '$route', '$modal', '_', 'BoardGateway', 'StageGateway', 'WorkItemGateway', 'board'
        ,function ($window, $scope, $route, $modal, _, boardGateway, stageGateway, workItemGateway, board) {

    $window.document.title = board.name;

    // before displaying the board sort the workItems by their order property.
    // This is only done once. This should no be done by angular with orderBy filter
    // in the ngRepeat directory because that should no be sorted after reorder by dnd.
    _.each(board.stages, function(stage) {
        stage.workItems = _.sortBy(stage.workItems, function(workItem) {
            return workItem.order
        });
    });
    $scope.board = board;

    $scope.selected = null;
    $scope.moving = null;
    $scope.movingIndex = null;


    $scope.deleteWorkItem = function (stage, workItem) {
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
        workItemGateway.removeWorkItem($scope.board.id, workItem).then(function(result) {
            var elementIndex;
            stage.workItems.forEach(function(currentWorkItem, stageIndex) {
                if (currentWorkItem.id == workItem.id) {
                    elementIndex = stageIndex;
                }
            });
            console.log(elementIndex);
            stage.workItems.splice(elementIndex, 1);
        });
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
        boardGateway.update($scope.board).catch(function(failure) {
            console.log(failure);
        });
      });
    }

    $scope.editWorkItem = function (stage, workItem) {
      var modalInstance = $modal.open({
        templateUrl: 'add_workitem.html',
        controller: 'AddWorkItemCtrl',
        resolve: {
          workItem: function() {
            workItem.stageId = stage.id;
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
        workItemGateway.update($scope.board.id, editedWorkItem).catch(function(failure) {
        });
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
        stageGateway.update($scope.board.id, stage).catch(function(failure) {
            console.log(failure);
        })
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
              workItems: []
            };
          }
        }
      });
      modalInstance.result.then(function(stage) {
        stageGateway.persist($scope.board.id, stage).then(function(result) {
            stage.id = result.id;
            $scope.board.stages.push(stage)
        }, function(failure) {
            console.log(failure);
        });
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
              stageId: null,
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
      var firstStageIndex = 0;
      $scope.board.stages.forEach(function(element, index) {
        if (element.order < $scope.board.stages[firstStageIndex].order) {
            firstStageIndex = index;
        }
      });
        var stage = $scope.board.stages[firstStageIndex];
        workItem.stageId = stage.id;
        workItemGateway.persist($scope.board.id, workItem).then(function(result) {
            workItem.id = result.id;
            stage.workItems.push(workItem)
        }, function(failure) {
            console.log(failure);
        });
      });
    };

    $scope.dropCallback = function(event, index, item) {
      var targetStageId = $(event.target).parents('.stage')[0].dataset.stageId;
      item.stageId = targetStageId;
      item.order = index;
      workItemGateway.update($scope.board.id, item).catch(function(failure) {
      });
      return item;
    };

  }]);
