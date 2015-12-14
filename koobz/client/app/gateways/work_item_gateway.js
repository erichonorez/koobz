'use strict';

angular.module('koobzApp')
    .factory('WorkItemGateway', ['$resource', function($resource) {

    return {
        workItemResource: $resource('/api/kanban/boards/:boardId/workitems/:workItemId', { boardId: '@boardId', workItemId: '@id' }, {
            'update' : { method: 'PUT' }
        }),

        removeWorkItem: function(boardId, workItem) {
            return this.workItemResource.delete({boardId: boardId, workItemId: workItem.id}).$promise;
        },

        persist: function(boardId, workItem) {
            return this.workItemResource.save({boardId: boardId}, {
                title: workItem.title,
                order: workItem.order,
                stageId: workItem.stageId,
                description: workItem.description
            }).$promise;
        },

        update: function(boardId, workItem) {
            return this.workItemResource.update({boardId: boardId, workItemId: workItem.id}, {
                title: workItem.title,
                order: workItem.order,
                stageId: workItem.stageId,
                description: workItem.description
            }).$promise;
        }

    }

}]);
