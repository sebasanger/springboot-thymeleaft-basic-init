//Constantes para el automatizador de tareas
const gulp = require("gulp");
const sass = require("gulp-sass");
const babel = require("gulp-babel");
const autoprefixer = require("gulp-autoprefixer");
const concat = require("gulp-concat");
const uglify = require("gulp-uglify");
const plumber = require("gulp-plumber");

//tarea para los estilos de la UX
gulp.task("styles", () => {
  return gulp
    .src("src/main/resources/scss/styles.scss")
    .pipe(plumber())
    .pipe(
      sass({
        outputStyle: "compact",
      })
    )
    .pipe(autoprefixer())
    .pipe(gulp.dest("src/main/resources/static/css"));
});

//tarea para el js de la UX
gulp.task("babel", () => {
  return gulp
    .src("src/main/resources/js/*.js")
    .pipe(plumber())
    .pipe(babel({ presets: ["@babel/preset-env"] }))
    .pipe(concat("scripts-min.js"))
    .pipe(uglify())
    .pipe(gulp.dest("src/main/resources/static/js"));
});

//tarea por defecto para que se ejecuten todas
gulp.task("default", () => {
  //Watchers (vigilantes) para vigilar los cambios y mostrarlos en tiempo real
  //SCSS
  gulp.watch("src/main/resources/scss/**/*.scss", gulp.series("styles"));

  //JS
  gulp.watch("src/main/resources/js/*.js", gulp.series("babel"));
});
