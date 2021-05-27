# Contributing

Thank you for wanting to contribute!

## Project setup

_The tests are using multiple database connections running on a docker
environment_

1. Fork and clone the repository
2. Setting up the databases for testing:

- `docker-compose build`
- `docker-compose up` or with `-d` if you want it to be run in detached mode

3. Create a new branch with `git checkout -b pr/<branch-name>`

## Before making PR

Make sure that all tests are passing before you commit your changes. You can do
that with:

`mvn clean test`

## After making a PR

Please keep watching the repository so that you can respond to any questions
that come up on your PR.
