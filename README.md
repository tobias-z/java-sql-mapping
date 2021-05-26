<div align="center">
  <h1>Java SQL Mapping</h1>
  <p>Mapping of SQL statements into it's corresponding object 🤩</p>
</div>

---

![documentation](https://img.shields.io/badge/documentation-yes-brightgreen.svg)
![GitHub](https://img.shields.io/github/license/tobias-z/java-sql-mapper)
[![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat-square)](#contributors-)
[![Maven Central](https://maven-badges.herokapp.com/maven-central/io.github.tobias-z/java-sql-mapping/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.tobias-z/java-sql-mapping)

## READ THIS

`This package is still in beta, and is not recommended to be used in production.`

`Expect bugs.`

## The problem

You are tired of doing your mapping of sql queries yourself, making your
repositories huge. You don't want to use an ORM framework, but still want the
mapping part of them.

## This solution

Java SQL Mapping is a package designed to work on any SQL database (see
[Supported](#Supported) section).

It allows you to create SQL queries, which will then be run through a jdbc
connection, and then return the object corresponding to your query.

## Installation

TBA

## Supported

Currently Java SQL Mapping has support for 3 databases:

- MySQL
- PostgreSQL
- SQLServer

These three have are listed since they have been thoroughly tested.

It is possible that other SQL databases are supported, but they have not been
tested.

## Usage

### Setup

Java SQL Mapping has a very simple setup. It provides a
`DBConnection.createDatabase()` method, which takes a `DBConfig`

### Example MySQL config

```java
public class MySQLDBConfig implements DBConfig {

    @Override
    public Map<DBSetting, String> getConfiguration() {
        Map<DBSetting, String> config = new HashMap<>();
        config.put(DBSetting.JDBC_DRIVER, "com.mysql.cj.jdbc.Driver");
        config.put(DBSetting.USER, "YOUR_USER");
        config.put(DBSetting.PASSWORD, "SUPER_SECRET_PASSWORD");
        config.put(DBSetting.URL, "jdbc:mysql://localhost:3306/chat_test");
        return config;
    }

}
```

### Example implementation

Following example can be used for any config. Just change the DBConfig you
provide to the `DBConnection.createDatabase()` method

```java
public class UserRepository {

    private static final Database db = DBConnection.createDatabase(new MySQLDBConfig());

    public User getUserById(int id) throws Exception {
        User user = db.get(id, User.class);
        return user;
    }

    public List<User> getAllUsers() throws Exception {
        List<User> users = db.getAll(User.class);
        return users;
    }

    public List<User> findAllUsersWithRole(Role role) throws Exception {
        SQLQuery query = new SQLQuery("SELECT * FROM users WHERE role = :role")
            .addParameter("role", role);
        List<User> users = db.select(query, User.class);
        return users;
    }

    public User createUser(String username, String password) throws Exception {
        SQLQuery insertQuery = new SQLQuery(
            "INSERT INTO users (username, password) VALUES (:username, :password)")
            .addParameter("username", username)
            .addParameter("password", password);
        User createdUser = db.insert(insertQuery, User.class);
        return createdUser;
    }

    public User updateUser(int id, String username) throws Exception {
        SQLQuery updateQuery = new SQLQuery(
            "UPDATE users SET username = :username WHERE id = :id")
            .addParameter("username", username)
            .addParameter("id", id);
        User updatedUser = db.update(updateQuery, User.class);
        return updatedUser;
    }

    public void deleteUser(int id) throws Exception {
        // Can also pass an SQLQuery
        db.delete(id, User.class);
    }

}
```

## Issues

Looking to contribute? Any feedback is very appreciated.

### 🪲 Bugs

Please file an issue for bugs, missing documentation, unexpected behavior etc.

[**Create bug
report**](https://github.com/tobias-z/java-sql-mapper/issues/new?assignees=&labels=&template=bug_report.md&title=)

### 🕯 Feature Requests

Please file an issue to suggest new features. Vote on feature requests by adding
a 👍.

[**Create Feature
Requests**](https://github.com/tobias-z/java-sql-mapper/issues/new?assignees=&labels=&template=feature_request.md&title=)

## Contributors ✨

Thanks goes to these wonderful people
([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="http://tobias-z.com"><img src="https://avatars.githubusercontent.com/u/70150300?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Tobias Zimmermann</b></sub></a><br /><a href="#infra-tobias-z" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a> <a href="https://github.com/tobias-z/java-sql-mapper/commits?author=tobias-z" title="Tests">⚠️</a> <a href="https://github.com/tobias-z/java-sql-mapper/commits?author=tobias-z" title="Code">💻</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the
[all-contributors](https://github.com/all-contributors/all-contributors)
specification. Contributions of any kind welcome!
