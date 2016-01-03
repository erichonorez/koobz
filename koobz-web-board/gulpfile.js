var gulp = require('gulp');
var minifyCss = require('gulp-minify-css');
var uglify = require('gulp-uglify');
var less = require('gulp-less');

var paths = {
    "js": "src/main/js/**",
    "html": "src/main/html/**",
    "less": "src/main/less/**/*.less",
    "dist": "target/dist/"
};

gulp.task('minify-css', function() {
    return gulp.src(paths.less)
        .pipe(less())
        .pipe(minifyCss())
        .pipe(gulp.dest(paths.dist + 'css/'));
});

gulp.task('minify-js', function() {
    return gulp.src(paths.js)
        //.pipe(uglify())
        .pipe(gulp.dest(paths.dist + 'js/'));
});

gulp.task('copy-html', function() {
    return gulp.src(paths.html)
        .pipe(gulp.dest(paths.dist + 'html/'))
});

gulp.task('default', ['minify-css', 'minify-js', 'copy-html'], function() {});
