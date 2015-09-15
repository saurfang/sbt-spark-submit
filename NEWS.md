# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).
Thanks to [keepachangelog.com](http://keepachangelog.com/) for this template.

## [Unreleased][unreleased]

## [0.0.4] - 2015-09-13
### Added
- This CHANGELOG file to track changes across versions.
- Example to submit Spark job to remote EC2 standalone cluster.

### Changed
- `sparkSubmitJar` now accepts `String` instead of `File` so arbitrary file path can be specified
such as `hdfs:///tmp/my-awesome-job.jar`

## [0.0.3] - 2015-08-26
### Changed
- Unsuccessful submission now emits error and shows on the screen
- Compile sources to Java 7 for compatability

### Fixed
- Fix a bug that prevents `settings(settings: Setting[_]*)` from working

## [0.0.2] - 2014-06-24
### Added
- `SparkSubmitYARN` plugin provides default settings suitable for YARN submission
- `SparkSubmitSetting` gains a few convenience constructors

## 0.0.1 - 2014-06-23
### Added
- Productivity and Awesomeness incoming...

[unreleased]: https://github.com/saurfang/sbt-spark-submit/compare/v0.0.4...HEAD
[0.0.4]: https://github.com/saurfang/sbt-spark-submit/compare/v0.0.3...v0.0.4
[0.0.3]: https://github.com/saurfang/sbt-spark-submit/compare/v0.0.2...v0.0.3
[0.0.2]: https://github.com/saurfang/sbt-spark-submit/compare/v0.0.1...v0.0.2
