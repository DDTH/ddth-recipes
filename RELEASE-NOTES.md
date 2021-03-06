# ddth-recipes release notes

## 1.1.0 - 2019-09-06

- `API Service`:
  - Refactor: new `ApiFilter` to intercept API calls and do pre-processing and post-processing work.
  - Refactor `IApiLogger` and add built-in implementations in `com.github.ddth.recipes.apiservice.logging` package:
    - API performance loggers: `AbstractPerfApiLogger`, `PrintStreamPerfApiLogger` and `Slf4jPerfApiLogger`.
- Update dependency libs.

## 1.0.0 - 2019-08-05

- Migrate to `Java 11`.


## 0.3.0 - 2019-03-17

- New recipe: Global.
- Upgrade dependency libs.


## 0.2.0.1 - 2018-07-22

- New recipe: API Service.


## 0.1.1.1 - 2018-07-03

- Migrate to `ddth-dao` v0.9.0.1.


## 0.1.1 - 2018-06-21

- New class `CheckpointUtils`.


## 0.1.0.1 - 2018-06-04

- Bug fixes.
- Add unit tests for checkpoint recipe.


## 0.1.0 - 2018-06-03

First release:

- Checkpoint recipe.
