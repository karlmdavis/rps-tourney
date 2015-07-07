RPS Tourney
===========
*A Collection of Rock-Paper-Scissors Games*

[![Build Status](https://justdavis.com/jenkins/buildStatus/icon?job=rps-tourney)](https://justdavis.com/jenkins/job/rps-tourney/)

## Introduction

![Console Game Playthrough](rps-tourney-console/dev/game-gifs/playthrough-win-in-two-rounds.gif)

This is an open source multiplayer Rock-Paper-Scissors game, available to play at [rpstourney.com](https://rpstourney.com). Why on earth would someone build such a thing? Why on earth not?!

Right now, the project is mostly just the web application linked above, along with the web service that supports it. However, there's also a console version of the game that will eventually be updated to support multiplayer via the web service as well. There will probably also be an Android version of the game. That's all a good ways down the road, though. At the moment, work is focused on polishing the web application and on adding AI-player support.


## Inspiration

Credit for the idea goes to [RPSContest.com](http://www.rpscontest.com/), which I first came across in the following Reddit post: [My three-line rock, paper, scissors bot has done surprisingly well...](http://www.reddit.com/r/programming/comments/1nj3z6/my_threeline_rock_paper_scissors_bot_has_done/). Once I do get to adding in support for AI and AI tournaments, I'm thinking that a fun differentiator/selling point will be the JVM's multi-language support.

This is obviously not the world's most serious of projects. Given my employer's rather stringent non-compete policy, it's actually a selling point (for me) that it's so pointless. This project is mostly an entertaining excuse to explore various technologies that I've wanted to play with.


## Development Documentation

The following documents document the architecture and development of the game:

* [RPS Tourney Architecture Overview](./dev/README-ARCHITECTURE.md)
* [Development Environment Setup](./dev/README-DEVENV.md)
* [Production Environment Setup](./dev/README-PRODUCTION.md)
* [Work Log](./dev/work-log.md)


## Releases

* `1.0.0`: This release just provides a simple console application that allows for play against a computer opponent.
    * Release Date: 2013-11-03
    * Issues: [1.0.0](https://github.com/karlmdavis/rps-tourney/issues?q=milestone%3A1.0)
    * Binaries: [com.justdavis.karl.rpstourney:*:1.0.0](https://justdavis.com/nexus/#nexus-search;gav~com.justdavis.karl.rpstourney~~1.0.0~~)
* `2.0.0-milestone.1`: This pre-release adds a multiplayer web version of the game, available at [rpstourney.com](https://rpstourney.com). The console version of the game is (as of yet) unchanged, and does not support multiplayer.
    * Release Date: 2014-12-31
    * Issues: [2.0.0-milestone.1](https://github.com/karlmdavis/rps-tourney/issues?q=milestone%3A2.0.0-milestone.1)
    * Binaries: [com.justdavis.karl.rpstourney:*:2.0.0-milestone.1](https://justdavis.com/nexus/#nexus-search;gav~com.justdavis.karl.rpstourney~~2.0.0-milestone.1~~)
* `2.0.0-milestone.2`: This pre-release adds named logins/accounts to the game and also fixes a client bug.
    * Release Date: 2015-04-26
    * Issues: [2.0.0-milestone.2](https://github.com/karlmdavis/rps-tourney/issues?q=milestone%3A2.0.0-milestone.2)
    * Binaries: [com.justdavis.karl.rpstourney:*:2.0.0-milestone.2](https://justdavis.com/nexus/#nexus-search;gav~com.justdavis.karl.rpstourney~~2.0.0-milestone.2~~)
* `2.0.0-milestone.3`: This pre-release contains a number of important bugfixes.
    * Release Date: 2015-06-04
    * Issues: [2.0.0-milestone.3](https://github.com/karlmdavis/rps-tourney/issues?q=milestone%3A2.0.0-milestone.3)
    * Binaries: [com.justdavis.karl.rpstourney:*:2.0.0-milestone.3](https://justdavis.com/nexus/#nexus-search;gav~com.justdavis.karl.rpstourney~~2.0.0-milestone.3~~)
* `2.0.0-milestone.4`: This pre-release includes a major UI and UX overhaul, but also contains an important security fix and other minor changes.
    * Release Date: 2015-07-05
    * Issues: [2.0.0-milestone.4](https://github.com/karlmdavis/rps-tourney/issues?q=milestone%3A2.0.0-milestone.4)
    * Binaries: [com.justdavis.karl.rpstourney:*:2.0.0-milestone.4](https://justdavis.com/nexus/#nexus-search;gav~com.justdavis.karl.rpstourney~~2.0.0-milestone.4~~)
