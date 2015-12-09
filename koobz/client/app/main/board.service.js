'use strict';

angular.module('koobzApp').factory('boardService', function() {

  return {

    board: null,

    getCurrentBoard: function() {
      return this.board;
    },

    newBoard: function(boardName) {
      this.board = {
          id: 1,
          name: boardName,
          stages: []
      }
    }
  
  }

});
