'use strict';

angular.module('koobzApp')
    .factory('StageGateway', ['$resource', function($resource) {

    return {
        stageResource: $resource('/api/boards/:boardId/stages/:stageId', { boardId: '@boardId', stageId: '@id' }, {
             'update' : { method: 'PUT' }
         }),

        persist: function(boardId, stage) {
            return this.stageResource.save({boardId: boardId}, {name: stage.name}).$promise;
        },

        update: function(boardId, stage) {
            return this.stageResource.update({boardId: boardId, stageId: stage.id}, {name: stage.name}).$promise;
        }
    }

}]);
