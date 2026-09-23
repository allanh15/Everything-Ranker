# Everything Ranker

A Java + SQLite application for creating, browsing, searching, and ranking lists. Built as a 6-person team project for CS 3354 (Software Engineering) at The University of Texas at Dallas.

## Team
- Allan Hoang
- Charli-Renae
- Arnav Jain
- Lucas Herrera
- Diego Hernandez
- Kaliyah Jackson

## My Contributions (Allan Hoang)
- Edit List: implemented list editing in ListManager.java and DBManager.java using parameterized JDBC queries on SQLite, with author-only authorization so only a list's creator can modify it
- Navigate List: implemented browsing and searching of public lists
- Testing: wrote 33 test cases across 4 test suites (EditListTest, CreateAccountTest, LoginTest, LogoutTest)

This repo contains two Java codebases:

- **Everything Ranker** (repo root): a SQLite-backed backend for creating/browsing/ranking lists, plus lightweight test runners (no JUnit).
- **Flight Planner** (`testProject/`): a separate console program that reads flight/request files and outputs best routes.

## Prerequisites

- **Java JDK**: Java 17+ recommended.
- **SQLite JDBC** (Everything Ranker): the repo includes `sqlite-jdbc-3.51.3.0.jar` and uses it via the Java classpath.

> Classpath separator: macOS/Linux uses `:` and Windows uses `;`.

## Everything Ranker 

### Compile

macOS/Linux:

```bash
javac -cp ".:sqlite-jdbc-3.51.3.0.jar" *.java
```

Windows (PowerShell):

```powershell
javac -cp ".;sqlite-jdbc-3.51.3.0.jar" *.java
```

### Run a test

Tests in this repo are plain Java programs with a `main(...)` method (no JUnit). Example:

macOS/Linux:

```bash
java -cp ".:sqlite-jdbc-3.51.3.0.jar" NavigateListTest
```

Windows:

```powershell
java -cp ".;sqlite-jdbc-3.51.3.0.jar" NavigateListTest
```

Other runnable test drivers include:

- `CreateListTest`
- `EditListTest`
- `RankListTest`
- `LoginTest`
- `LogoutTest`
- `ViewProfileTest`
- `CreateAccountTest`

### Database notes

- The default DB file is `test.db` (created/used by `DBManager()`).
- Several tests create temporary DB files (e.g., `navigate_list_test.db`) and delete them during teardown.

## Flight Planner (`testProject/`)

This is a standalone console app.

### Compile

```bash
cd testProject
javac *.java
```

### Run

Uses `Flights.txt`, `Requests.txt`, and writes to `Output.txt` by default:

```bash
java Main
```

Or provide custom file paths:

```bash
java Main <FlightsFile> <RequestsFile> <OutputFile>
```
