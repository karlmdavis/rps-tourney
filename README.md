RPS Tourney
===========
*A Collection of Rock-Paper-Scissors Games*

[![Build Status](https://justdavis.com/jenkins/buildStatus/icon?job=rps-tourney)](https://justdavis.com/jenkins/job/rps-tourney/)

## Introduction

![Console Game Playthrough](rps-tourney-console/dev/game-gifs/playthrough-win-in-two-rounds.gif)

This is a collection of Rock-Paper-Scissors games. It includes a single-player console version of the game, as well as a multiplayer web version of the game, available at [rpstourney.com](https://rpstourney.com). In later releases the console version will also be multiplayer, and an Android version of the game will also be available.

I will also likely explore AI-only tournaments, similar to [RPSContest.com](http://www.rpscontest.com/).


## Development Documentation

The following documents document the architecture and development of the game:

* [RPS Tourney Architecture Overview](./dev/README-ARCHITECTURE.md)
* [Development Environment Setup](./dev/README-DEVENV.md)
* [Production Environment Setup](./dev/README-PRODUCTION.md)
* [Work Log](./dev/work-log.md)


## Releases

### [1.0.0](https://github.com/karlmdavis/rps-tourney/issues?q=milestone%3A1.0) (2013-11-03)

This release just provides a simple console application that allows for against a computer opponent.

Artifacts:

* [rps-tourney-console-1.0.0-dist.tar.gz](https://justdavis.com/nexus/service/local/repositories/releases-opensource/content/com/justdavis/karl/rpstourney/rps-tourney-console/1.0.0/rps-tourney-console-1.0.0-dist.tar.gz): the console game

### [2.0.0-milestone.1](https://github.com/karlmdavis/rps-tourney/issues?q=milestone%3A2.0.0-milestone.1) (2014-12-31)

This pre-release adds a multiplayer web version of the game, available at [rpstourney.com](https://rpstourney.com).

The console version of the game is (as of yet) unchanged, and does not support multiplayer.

Artifacts:

* [rps-tourney-webapp-2.0.0-milestone.1.war](https://justdavis.com/nexus/service/local/repositories/releases-opensource/content/com/justdavis/karl/rpstourney/rps-tourney-webapp/2.0.0-milestone.1/rps-tourney-webapp-2.0.0-milestone.1.war): the WAR for the game's web application
* [rps-tourney-service-app-2.0.0-milestone.1.war](https://justdavis.com/nexus/service/local/repositories/releases-opensource/content/com/justdavis/karl/rpstourney/rps-tourney-service-app/2.0.0-milestone.1/rps-tourney-service-app-2.0.0-milestone.1.war): the WAR for the game's web service (required by the web application)


## Inspiration

Credit for the idea goes to [RPSContest.com](http://www.rpscontest.com/), which I first came across in the following Reddit post: [My three-line rock, paper, scissors bot has done surprisingly well...](http://www.reddit.com/r/programming/comments/1nj3z6/my_threeline_rock_paper_scissors_bot_has_done/). If I do get into the AI-only tournaments space with this project, I'm thinking that a differentiator/selling point will be the JVM's multi-language support, rather than just the Python support that version has.

This is obviously not the world's most serious of projects. It's really meant to be a fun excuse to write some silly code. Given my current employer's rather stringent non-compete policy, it's actually a selling point (for me) that it's so more-or-less pointless.
