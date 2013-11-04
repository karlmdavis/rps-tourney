RPS Tourney
===========
*A Collection of Rock-Paper-Scissors Games*

[![Build Status](https://justdavis.com/jenkins/buildStatus/icon?job=rps-tourney)](https://justdavis.com/jenkins/job/rps-tourney/)

## Introduction

![Console Game Playthrough](rps-tourney-console/dev/game-gifs/playthrough-win-in-two-rounds.gif)

This is a collection of Rock-Paper-Scissors games. The 1.0 release just completed is quite modest: it just provides a simple console application that allows for against a computer opponent. This was more  to build momentum and get a proof of concept in place than anything else.

In later releases, I'm planning to build both web and Android versions of the game. These will be built on top of a central web service, to allow for networked play. The web service and web game are planned for inclusion in the next major release, 2.0.

In addition, it would also be interesting to explore AI-only tournaments, similar to [RPSContest.com](http://www.rpscontest.com/).


## Releases

### 1.0.0

This release just provides a simple console application that allows for against a computer opponent.

Artifacts:

* [rps-tourney-console-1.0.0-dist.tar.gz](https://justdavis.com/nexus/service/local/repositories/releases-opensource/content/com/justdavis/karl/rpstourney/rps-tourney-console/1.0.0/rps-tourney-console-1.0.0-dist.tar.gz): the console game


## Inspiration

Credit for the idea goes to [RPSContest.com](http://www.rpscontest.com/), which I first came across in the following Reddit post: [My three-line rock, paper, scissors bot has done surprisingly well...](http://www.reddit.com/r/programming/comments/1nj3z6/my_threeline_rock_paper_scissors_bot_has_done/). If I do get into the AI-only tournaments space with this project, I'm thinking that a differentiator/selling point will be the JVM's multi-language support, rather than just the Python support that version has.

This is obviously not the world's most serious of projects. It's really meant to be a fun excuse to write some silly code. Given my current employer's rather stringent non-compete policy, it's actually a selling point (for me) that it's so more-or-less pointless.
