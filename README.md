# aqoursql

[![CircleCI](https://circleci.com/gh/lagenorhynque/aqoursql.svg?style=shield)](https://circleci.com/gh/lagenorhynque/aqoursql)
[![codecov](https://codecov.io/gh/lagenorhynque/aqoursql/branch/master/graph/badge.svg)](https://codecov.io/gh/lagenorhynque/aqoursql)

AqoursQL, an example GraphQL API based on [Lacinia-Pedestal](https://github.com/walmartlabs/lacinia-pedestal) & [Duct](https://github.com/duct-framework/duct).

## Developing

### Prerequisites

- [Java (JDK)](http://openjdk.java.net/)
    - `java --version` >= 11
- [Leiningen](https://leiningen.org/)
- [Docker](https://www.docker.com/)

### Setup

When you first clone this repository, run:

```sh
$ lein duct setup
```

This will create files for local configuration, and prep your system
for the project.

### Database

```sh
# Start local DB
$ docker compose up -d
# Import DB schema
$ docker compose exec -T mariadb mariadb -uroot -proot aqoursql < sql/ddl/aqoursql.sql
$ docker compose exec -T mariadb-test mariadb -uroot -proot aqoursql_test < sql/ddl/aqoursql.sql
# Seed DB
$ docker compose exec -T mariadb mariadb -uroot -proot aqoursql < sql/dml/seed.sql
```

### Development environment

To begin developing, start with a REPL.

```sh
$ lein repl
```

With [rebel-readline](https://github.com/bhauman/rebel-readline):

```sh
$ lein rebel
```

Then load the development environment.

```clojure
user=> (dev)
:loaded
```

Run `go` to prep and initiate the system.

```clojure
dev=> (go)
:duct.server.http.jetty/starting-server {:port 8888}
:initiated
```

By default this creates a web server at <http://localhost:8888>.

When you make changes to your source files, use `reset` to reload any
modified files and reset the server.

```clojure
dev=> (reset)
:reloading (...)
:resumed
```

Run `halt` to halt the system.

```clojure
dev=> (halt)
:halted
```

### Production build & run

```sh
$ lein uberjar
$ DATABASE_URL="..." java -jar target/aqoursql.jar
```

### Testing

Testing is fastest through the REPL, as you avoid environment startup
time.

```clojure
dev=> (test)
...
```

But you can also run tests through Leiningen.

```sh
$ lein test
```

with [cloverage](https://github.com/cloverage/cloverage):

```sh
$ lein test-coverage
# Open the coverage report
$ open target/coverage/index.html
```

### Linting

- [`eastwood`](https://github.com/jonase/eastwood), [`kibit`](https://github.com/jonase/kibit)

```sh
$ lein lint
```

- [`cljstyle check`](https://github.com/greglook/cljstyle), [`clj-kondo`](https://github.com/borkdude/clj-kondo), [`joker`](https://github.com/candid82/joker)

```sh
$ make lint
```

- fixing formatting

```sh
$ make cljstyle-fix
```

### API Documentation ([Codox](https://github.com/weavejester/codox))

```sh
$ lein codox
$ open target/codox/index.html
```

### GraphQL execution ([GraphiQL](https://github.com/graphql/graphiql))

```sh
# After starting the server
$ open http://localhost:8888
```
