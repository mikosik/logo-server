# Logo Language Server

Language Server Protocol (LSP) implementation for the Logo programming language.
This server provides LSP features such as:
 - syntax highlighting
 - declaration navigation
 - publishing diagnostics with syntax errors  

## Table of Contents
- [How to Build and Run the Server](#how-to-build-and-run-the-server)
- [Connecting from LSP Client](#connecting-from-lsp-client)
- [Architecture](#architecture)
- [Project Layout](#project-layout)
- [Things to improve in the current feature set](#things-to-improve-in-the-current-feature-set)
  - [functional](#functional)
  - [technical](#technical)
- [Things to consider in the future](#things-to-consider-in-the-future)

## How to Build and Run the Server

You need Java 24 or later installed.

To build the server:

```bash
./gradlew build
```

To run the server directly:

```bash
./gradlew run
```

To create a distributable package:

```bash
./gradlew installDist
```

This will create a distribution in `build/install/logo-server/` with executable scripts in the `bin/` directory.

Alternatively, you can create distribution archives:

```bash
./gradlew distZip    # Creates a ZIP archive
./gradlew distTar    # Creates a TAR archive
```

## Connecting from LSP Client

The Logo Language Server communicates via standard input/output.
Configure your LSP client to:
   - Launch the server executable (built with commands described above)
   - Communicate with it via stdin/stdout
   - Associate it with Logo files (typically with extensions like `.logo`)

## Architecture

The Logo Language Server is implemented in Java and uses the LSP4J library 
for implementing the Language Server Protocol.
Communication between client and server is done via stdin/stdout.

```
+----------------+      stdin         +---------------+
|                |------------------> |               |
|   LSP Client   |      stdout        |  LOGO Server  |
|                |<------------------ |               |
+----------------+                    +---------------+
```

At the highest level the server is decomposed into two components:
 - LSP protocol endpoints implementation
 - analysis engine that performs parsing/analysing of logo code 

```
+----------------------------------------------+
|                   LOGO server                |
|  +---------------+         +--------------+  |
|  |               |         |              |  |
|  | LSP Endpoints |-------->|   Analysis   |  |
|  |               |         |   Engine     |  |
|  |               |         |              |  |
|  +---------------+         +--------------+  |
|                                              |
+----------------------------------------------+
```

The endpoint code is thread-confined and is always invoked by lsp4j framework
inside the thread owned by the framework.

Analysis engine API consists of:
 - [DocumentHandlerManager](src/main/java/com/mikosik/logoserver/analyse/DocumentHandlerManager.java)
  that manages lifecycle of [AsyncDocumentHandler](src/main/java/com/mikosik/logoserver/analyse/AsyncDocumentHandler.java) 
 instances
 - [AsyncDocumentHandler](src/main/java/com/mikosik/logoserver/analyse/AsyncDocumentHandler.java)
 that provides asynchronous API for handling a single LOGO document

[DocumentHandlerManager](src/main/java/com/mikosik/logoserver/analyse/DocumentHandlerManager.java)
creates, caches and destroys
[AsyncDocumentHandler](src/main/java/com/mikosik/logoserver/analyse/AsyncDocumentHandler.java) 
instances - one per document.
Destruction is lazy and happens after removal was requested and the hardcoded threshold of
inactivity has been reached.
This way server is capable of handling request that come after `didClose` was request
but before `didOpen` is received which is required by LSP specification and observed
when experimenting with lsp4ij client.

```
+----------------+                    +-------------------+
|                |                    |                   |
|    Document    |                    |      Async        |
|    Handler     |                    |     Document      |
|    Manager     |                    |     Handler       |
|                |                    |                   |
| +------------+ |                    +-------------------+
| |            | |                              .
| |  document  | | 1               *            .
| |  handlers  |<>------------------  +-------------------+
| |  map       | |                    |                   |+ 
| +------------+ |                    |      Async        ||+
|                |                    |     Document      |||                    
+----------------+                    |     Handler       |||                    
                                      |                   |||
                                      +-------------------+||
                                       +-------------------+|
                                        +-------------------+
```

[AsyncDocumentHandler](src/main/java/com/mikosik/logoserver/analyse/AsyncDocumentHandler.java) provides async API for document-related operations.
It is a wrapper around [DocumentHandler](src/main/java/com/mikosik/logoserver/analyse/DocumentHandler.java) which has synchronous API.
All operations on [DocumentHandler](src/main/java/com/mikosik/logoserver/analyse/DocumentHandler.java) are thread-confined to the virtual thread
(worker thread) held internally by [AsyncDocumentHandler](src/main/java/com/mikosik/logoserver/analyse/AsyncDocumentHandler.java).
Task submission is done via an internal thread-safe unbounded blocking queue.
Results are returned via `CompletableFuture`.
Each async operation has the following steps:
 - create `CompletableFuture` for publishing the result
 - submit Runnable to the queue that performs operation and publishes the result via 
   `CompletableFuture`
 - return `CompletableFuture` to the caller

```
                                                                 +--------------------+
                    +---------------+        +----------+        |                    |
      operation     |               |  put   |          |  poll  |   Worker Thread    |
------------------->|    Async      |------->|  queue   |<-------|                    |
        .           |   Document    |        |          |        |  +--------------+  |
 (returns future    |   Handler     |        +----------+        |  |              |  |
  synchronously)    |               |                            |  |   Document   |  |
        .           +---------------+                            |  |   Handler    |  |
        .                                                        |  |              |  |
  +--------------+             publish result                    |  +--------------+  |
  | Completable  | <-------------------------------------------- |                    |
  | Future       |                                               +--------------------+
  +--------------+                                         
```

[DocumentHandler](src/main/java/com/mikosik/logoserver/analyse/DocumentHandler.java) is responsible for handling document-related operations.
Its logic is pretty straightforward.
It performs operations in order, handles only full-text updates, and 
does not cache results. It uses ANTLR4 generated grammar for generating parse tree
and in-house implementations of LSP features (syntax highlighting, declaration navigation, 
publishing diagnostics).

## Project Layout

```
logo-server/
├── src/
│   ├── main/
│   │   ├── antlr/
│   │   │   └── com/mikosik/logoserver/analyse/parser/antlr/
│   │   │       └── Logo.g4                          -> grammar specification for ANTLR4
│   │   ├── java/
│   │   │   └── com/mikosik/logoserver/
│   │   │       ├── Main.java                        -> Main class for server app
│   │   │       ├── analyse/                         -> document handlers lifecycle and processing
│   │   │       │   ├── base/                        -> base value classes 
│   │   │       │   ├── declaration/                 -> declaration finding functionality
│   │   │       │   ├── highlight/                   -> syntax highlighting functionality
│   │   │       │   └── parser/                      -> parsing/diagnostic functionality
│   │   │       └── endpoints/                       -> LSP protocol handlers 
│   │   └── resources/
│   │       └── logging.properties                   -> logger configuration
├── build.gradle.kts                                 -> gradle build spec
└── settings.gradle.kts                              -> ditto
```

## Things to improve in the current feature set
There are a few things in the currently supported functionality that could be made better if
more time was spent on it. I've decided to cut some corners to make homework not become too 
big regarding needed time. Ideas listed below are not sorted in any particular order. Choosing 
which one should be implemented first most probably should depend on user feedback.

### functional
 - `publishDiagnostic` functionality apart from syntax problems could contain semantic errors as 
   well as warnings (for example, unused variables warning). This is a matter of adding semantic 
   checks to the analysis component.
 - `declaration` functionality treats variable declared with `localmake` as if it was a global 
   scope variable. This way you can use go-to-declaration functionality even on reference that 
   is outside the local variable scope. The full solution to that problem requires building 
   separate instance of Declarations for each scope in parsed tree and declarations of given 
   reference are taken from only from scopes visible from that reference. 
 - `readword` and `readlist` commands are not fully handled as they require context sensitive 
   behavior at the lexer level. As handling of all language features was not required, I've 
   implemented workaround which works as long as tokens inside brackets are not known commands 
   nor special characters.
 - go-to-declaraion functionality does not work when the caret is at the end of the reference 
   (after the last character). It seems to me that this is a problem with lsp4ij as a request is 
   not being sent at all to the lsp server. Anyway it seems annoying as it 
   works well for normal java files in intellij, so it is different behavior than normal 
   user is used to.
 - Server does not handle `shutdown` cleanly. It just replies it performed the shutdown but 
   actually there's no clean up performed. Similarly `close` is ignored. Experiments show that 
   lsp4ij can handle that situation so I decided it is not the highest priority. 
   Note that further down the text I suggest more advanced multithreading architecture in which 
   shutdown would be simpler due to usage of more advanced frameworks/libraries.
   Implementing proper shutdown with current implementation would require the following steps.
     - Adding a boolean flag to [LogoTextDocumentService](src/main/java/com/mikosik/logoserver/endpoints/LogoTextDocumentService.java)
       signaling that it is in shutdown mode and reject all normal requests if the flag is set
     - Adding shutdown method to DocumentHandlerManager that would invoke shutdown on all 
       handlers and create 
     - Changing AsyncDocumentHandler shutdown method to use poison pill which submitted to the 
       queue would inform workerThread to terminate itself. This way all tasks that are added 
       to the queue before poison-pill are processed before workerThread terminates. The shutdown 
       method should return CompletableFuture that is completed by poison-pill being executed. 
       This way DocumentHandlerManager can create single CompletableFuture 
       (`CompletableFuture.allOf()`) that is require by lsp4j `shutdown` method.
 - the analysis component does not cache any results. It can be added to DocumentHandler for each 
   functionality separately and reset it each time the document's text changes. 
 - Server supports only full document updates. Handling incremental updates is not difficult but 
   alone does not provide real time gains unless network connection between client and server has 
   low throughput. Real performance improvement can be achieved once incremental updates are used 
   for incremental parsing and incremental recalculation of previously cached results 
   (semantic/diagnostic/parse tree/ etc data).

### technical
 - Mutable LSP value objects are used within `analysis` component. This pollutes that component 
   and couples it with LSP despite its functionality can be reused outside of LSP server. It 
   also requires defensive copying. Creating immutable value classes inside `analysis` component 
   would be safer solution from coupling and thread-safety perspective. I've chosen the current 
   solution to save some time (for other features) by not reimplementing those value classes.
 - Test coverage is far from what I am comfortable with. I focused on providing tests for most 
   of the classes to present my testing style and to drive testability of those classes. Still,
   the biggest missing piece is testing the whole server. It should be done by starting server 
   from junit test and communicating with it using lsp4j client implementation connected to the 
   server via PipeInputStream/PipeOutputStream.

## Things to consider in the future
The current in-house multithreading solution is enough only for a simple thread per-document 
model. Performance improvements that can be gained from handling incremental updates and 
precalculating requests results in advance (before they are actually received) or preindexing 
updated data would benefit from a more advanced task scheduler that would allow:
  - cancelling tasks that are known to be no longer needed (for example, cancelling preindexing 
      task if another incremental update for the document has been received)
  - prioritizing tasks that are more important (for example, tasks related to an open document are 
    more important to a task of a document that has been closed but may be opened in a moment)
  - parallelizing tasks for single documents that do not depend on each other 

