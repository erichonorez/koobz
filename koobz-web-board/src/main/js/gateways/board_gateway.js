'use strict';

angular.module('koobzApp')
    .factory('BoardGateway', ['$resource', function($resource) {

    return {
        boardResource: $resource('/api/boards/:boardId', { boardId: '@id' }, {
            'update' : { method: 'PUT' }
        }),

        persist: function(boardName) {
            return this.boardResource.save({name: boardName}).$promise;
        },

        find: function(boardId) {
            return this.boardResource.get({boardId: boardId}).$promise;
        },

        update: function(board) {
            return this.boardResource.update({boardId: board.id}, { name: board.name }).$promise;
        }

    }

}]);
