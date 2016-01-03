var underscore = angular.module('sluggerizer', []);
underscore.factory('sluggerizer', function($window) {

  return {
    slug: function(value) {
        return value.toLowerCase()
            .replace(/ /g,'-')
            .replace(/[^\w-]+/g,'');
    }
  };

});
