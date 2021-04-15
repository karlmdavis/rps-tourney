# User Interview: Tournament Functionality

In 2020, I had discussions with a consulting client
  that wanted to run RPS Tourney as part of a corporate tournament:
  a bracketed series of games with multiple players advancing from round to round.
Despite the name, the applications do not currently have that functionality.
We explored how best to meet their needs, regardless.

The [database.md](.database.md] document details the short-term solution we arrived at for them:
  interacting directly with the database in a separate application to drive the tournament.

Below, are my notes from the original user interview.

## Questions

* May not automate brackets; may create them manually after each bracket/round.
    * But **does** want to create the games automatically.
* If a player advances to an additional match, give them the link for that match.
* Heading up to 5pm, will collect players.
* Will also need to retrieve the results from the match.
    * Is it possible to store the winner in the DB?
* Expect folks to pre-register with the site.
* Want to create games in the DB.
* Need to keep an eye on non-responsive players: is a game stuck waiting on someone to join or make a move?
* About players:
    * Given an email address, what's their player ID?
* About games:
    * Given two players, how do I create a game with them in it?
        * Either by player ID or email address.
* About a game that's been created:
    * What is the URL for the game?
    * Is the game complete?
    * If so, who won?
        * By player ID, email address, or name.
    * If not, which player(s) is it waiting on and for how long?
        * By player ID, email address, or name.

## Feature Wishlist

* The database really **should** track the winner of each game.
    * Even aside from tournaments, it'd be nice to be able to query for "how many games has a player won and lost?"
* The ability to forfeit/abandon a game you're in, so that you don't end up with a million "in progress" games forever.
* Time limits for player moves.
* A tournament bracket system.
* A REST API method to query for a games list.
    * This is likely a bad idea; expensive and thus DDoS-prone.
* A REST API method to look up the games for a specific player.
    * Might be a safer alternative.
