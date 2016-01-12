'use strict';

angular.module('koobzApp')
  .controller('MainCtrl', [
     '$window', '$scope', '$route', '_', '$mdDialog', 'BoardGateway', 'StageGateway', 'WorkItemGateway', 'board'
        ,function ($window, $scope, $route, _, $mdDialog, boardGateway, stageGateway, workItemGateway, board) {

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
      var parentEl = angular.element(document.body);
      $mdDialog.show({
        parent: parentEl,
        template:
            '<md-dialog-content class="layout-padding">' +
            '   <p>Are you sure you want to delete this work item?</p>' +
            '   <form button-form ng-submit="delete()" layout="row" layout-align="center end">' +
            '       <md-button class="md-raised" type="button" ng-click="closeDialog()">Cancel</md-button>' +
            '       <md-button class="md-raised md-warn" ng-click="delete()">Delete</md-button>' +
            '   </form>' +
            '</md-dialog-content>',
        locals: {
            boardId: $scope.board.id,
            stage: stage,
            workItem: workItem,
            removeFn: $scope.removeWorkItemFromStage
        },
        controller: function DialogController($scope, $mdDialog, boardId, stage, workItem, removeFn) {
            $scope.boardId = boardId;
            $scope.stage = stage;
            $scope.workItem = workItem;
            $scope.removeFn = removeFn;

            $scope.closeDialog = function() {
              $mdDialog.hide();
            };

            $scope.delete = function() {
                workItemGateway.removeWorkItem($scope.boardId, $scope.workItem).then(function(result) {
                    $scope.removeFn.apply(this, [$scope.stage, $scope.workItem]);
                    $scope.closeDialog();
                });
            };
        }
      });
    };

    $scope.removeWorkItemFromStage = function(stage, workItem) {
        var elementIndex;
        stage.workItems.forEach(function(currentWorkItem, stageIndex) {
            if (currentWorkItem.id == workItem.id) {
                elementIndex = stageIndex;
            }
        });
        stage.workItems.splice(elementIndex, 1);
    };

    $scope.editBoard = function() {
      var parentEl = angular.element(document.body);
      $mdDialog.show({
          parent: parentEl,
          template:
              '<md-dialog aria-label="Edit board" class="stage-dialog">' +
              '     <md-toolbar class="layout-padding">' +
              '       <h3>Edit the board name</h3>' +
              '     </md-toolbar>' +
              '     <md-dialog-content class="layout-padding">' +
              '       <form button-form ng-submit="save()">' +
              '         <md-input-container class="md-block">' +
              '           <label for="title">Title</label>' +
              '           <input type="text" name="title" id="title" required="true" autofocus ng-model="board.name">' +
              '         </md-input-container>' +
              '         <md-content layout="row" layout-align="end center">' +
              '           <md-button class="md-raised" type="button" ng-click="closeDialog()">Cancel</md-button>' +
              '           <md-button class="md-raised md-primary" ng-click="save()">Submit</md-button>' +
              '         </md-content>' +
              '     </form>' +
              '   </md-dialog-content>' +
              '</md-dialog>',
          locals: {
              board: board,
          },
          onComplete: function afterShowAnimation(scope, element, options) {
              element.find('input:first').focus();
          },
          controller: function($scope, $mdDialog, board) {
              $scope.originalBoard = board;
              $scope.board = {
                id: board.id,
                name: board.name
              };

              $scope.closeDialog = function() {
                $mdDialog.hide();
              };

              $scope.save = function() {
                  boardGateway.update($scope.board).then(function(result) {
                    $scope.originalBoard.name = $scope.board.name;
                    $scope.closeDialog();
                  }, function(failure) {
                    console.log(failure);
                  });
              };
          }
      });
    }

    $scope.editWorkItem = function (stage, workItem) {
        $scope.showWorkItemForm(
        $scope.board.id,
        $scope.board.stages,
        {  id: workItem.id,
           title: workItem.title,
           stageId: stage.id,
           description: workItem.description
        },
       function(editedWorkItem, innerScope) {
             workItemGateway.update($scope.board.id, editedWorkItem)
             .then(function(result) {
                if (stage.id != editedWorkItem.stageId) {
                    $scope.removeWorkItemFromStage(stage, workItem);
                }

                workItem.title = editedWorkItem.title;
                workItem.description = editedWorkItem.description;

                if (stage.id != editedWorkItem.stageId) {
                   $scope.addWorkItemOnBoard($scope.board, editedWorkItem);
                }

                innerScope.closeDialog();
             }, function(failure) {
                console.log(failure);
             });
         }
        );
    };

    $scope.editStage = function (stage) {
      var stageToEdit = {
        id: stage.id,
        name: stage.name
      };
      $scope.showStageForm($scope.board, stageToEdit, function(editedStage, innerScope) {
          stageGateway.update($scope.board.id, editedStage).then(function(result) {
            stage.name = editedStage.name;
            innerScope.closeDialog();
          }, function(failure) {
            console.log(failure);
          });
      });
    };

    $scope.addStage = function () {
        $scope.showStageForm($scope.board, {
            id: null,
            name: null,
            workItems: []
        }, function(stage, innerScope) {
            stageGateway.persist($scope.board.id, stage).then(function(result) {
                stage.id = result.id;
                $scope.board.stages.push(stage);
                innerScope.closeDialog();
            }, function(failure) {
                console.log(failure);
            });
        });
    };

    $scope.deleteStage = function(stage) {
      var parentEl = angular.element(document.body);
      $mdDialog.show({
        parent: parentEl,
        template:
            '<md-dialog-content class="layout-padding">' +
            '   <p>Are you sure you want to delete this stage?</p>' +
            '   <form button-form ng-submit="delete()" layout="row" layout-align="center end">' +
            '       <md-button class="md-raised" type="button" ng-click="closeDialog()">Cancel</md-button>' +
            '       <md-button class="md-raised md-warn" ng-click="delete()">Delete</md-button>' +
            '   </form>' +
            '</md-dialog-content>',
        locals: {
            board: $scope.board,
            stage: stage
        },
        controller: function DialogController($scope, $mdDialog, board, stage) {
            $scope.board = board;
            $scope.stage = stage;

            $scope.closeDialog = function() {
              $mdDialog.hide();
            };

            $scope.delete = function() {
                stageGateway.removeStage($scope.board.id, $scope.stage).then(function(result) {
                    _.each(board.stages, function(stage, index) {
                        if (stage.id == $scope.stage.id) {
                            $scope.board.stages.splice(index, 1);
                            $scope.closeDialog();
                        }
                    });
                }, function(failure) {
                    console.log(failure);
                });
            };
        }
      });
    };

    $scope.showStageForm = function(board, stage, saveFn) {
        var parentEl = angular.element(document.body);
        $mdDialog.show({
            parent: parentEl,
            template:
                '<md-dialog aria-label="Edit stage" class="stage-dialog">' +
                '     <md-toolbar class="layout-padding">' +
                '       <h3>Add a stage</h3>' +
                '     </md-toolbar>' +
                '     <md-dialog-content class="layout-padding">' +
                '       <form button-form ng-submit="save()">' +
                '         <md-input-container class="md-block">' +
                '           <label for="title">Title</label>' +
                '           <input type="text" name="title" id="title" required="true" autofocus ng-model="stage.name">' +
                '         </md-input-container>' +
                '         <md-content layout="row" layout-align="end center">' +
                '           <md-button class="md-raised" type="button" ng-click="closeDialog()">Cancel</md-button>' +
                '           <md-button class="md-raised md-primary" ng-click="save()">Submit</md-button>' +
                '         </md-content>' +
                '     </form>' +
                '   </md-dialog-content>' +
                '</md-dialog>',
            locals: {
                board: board,
                stage: stage,
                saveFn: saveFn
            },
            onComplete: function afterShowAnimation(scope, element, options) {
                element.find('input:first').focus();
            },
            controller: function($scope, $mdDialog, stage, board, saveFn) {
                $scope.stage = stage;
                $scope.board = board;
                $scope.saveFn = saveFn;

                $scope.closeDialog = function() {
                  $mdDialog.hide();
                };

                $scope.save = function() {
                    $scope.saveFn.apply(this, [$scope.stage, $scope]);
                };
            }
        });
    };

    $scope.showWorkItemForm = function(boardId, stages, workItem, saveFn) {
        var parentEl = angular.element(document.body);
        $mdDialog.show({
            parent: parentEl,
            template:
                '<md-dialog aria-label="Add a work item" class="workitem-dialog">' +
                '   <md-toolbar class="layout-padding">' +
                '       <h3>Add a work item</h3>' +
                '   </md-toolbar>' +
                '   <md-dialog-content class="layout-padding">' +
                '     <form button-form ng-submit="save()">' +
                '       <md-input-container class="md-block">' +
                '         <label for="title">Title</label>' +
                '         <input type="text" name="title" id="title" required="true" autofocus ng-model="workItem.title">' +
                '       </md-input-container>' +
                '       <md-input-container class="md-block">' +
                '         <label>Stage</label>' +
                '         <md-select ng-model="workItem.stageId">' +
                '           <md-option ng-repeat="stage in stages | orderBy:\'order\'" value="{{stage.id}}" ng-selected="workItem.stageId == null ? $index == 0 : workItem.stageId == stage.id">' +
                '             {{stage.name}}' +
                '           </md-option>' +
                '         </md-select>' +
                '       </md-input-container>' +
                '       <md-input-container class="md-block">' +
                '         <label for="description">Description</label>' +
                '         <textarea class="form-control"name="description" id="description" ng-model="workItem.description"></textarea>' +
                '       </md-input-container>' +
                '       <md-content layout="row" layout-align="end center">' +
                '         <md-button class="md-raised" ng-click="closeDialog()" type="button">Cancel</md-button>' +
                '         <md-button class="md-raised md-primary" ng-click="save()">Submit</md-button>' +
                '       </md-content>' +
                '     </form>' +
                '   </md-dialog-content>' +
                '</md-dialog>',
            locals: {
                workItem: workItem,
                 stages: stages,
                 saveFn: saveFn
            },
            onComplete: function afterShowAnimation(scope, element, options) {
                element.find('input:first').focus();
            },
            controller: function DialogController($scope, $mdDialog, workItem, stages, saveFn) {
                $scope.workItem = workItem;
                $scope.stages = stages;
                $scope.saveFn = saveFn;

                $scope.closeDialog = function() {
                  $mdDialog.hide();
                };

                $scope.save = function() {
                    $scope.saveFn.apply(this, [$scope.workItem, $scope]);
                };
            }
        });
    }

    $scope.addWorkItem = function() {
        $scope.addWorkItemToStage(null);
    };

    $scope.addWorkItemToStage = function(stageId) {
        $scope.showWorkItemForm(
            $scope.board.id,
            $scope.board.stages,
            {  id: null,
               title: null,
               stageId: stageId,
               description: null
            },
           function(workItem, innerScope) {
                 workItemGateway.persist($scope.board.id, workItem).then(function(result) {
                    workItem.id = result.id;
                    $scope.addWorkItemOnBoard($scope.board, workItem);

                     innerScope.closeDialog();
                 }, function(failure) {
                     console.log(failure);
                 });
             }
        );
    };

    $scope.addWorkItemOnBoard = function(board, workItem) {
         _.each(board.stages, function(stage) {
             if (stage.id == workItem.stageId) {
                stage.workItems.push(workItem);
             }
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

    if ($scope.board.stages.length == 0) {
        $scope.addStage();
    }

  }]);
