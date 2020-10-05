# RPS Tourney Database

RPS Tourney's database (on deployment) is PostgreSQL.
All of the table and column names are case-sensitive
  and need to be double-quoted.
(In hindsight, this was definitely a mistake,
  but it seemed like a good idea at the time.)
Other than that, the schema is pretty simple.

These tables are related to games that have been created:

* `"Games"`:
  Represents a game that has been created,
    along with its players, state, settings, etc.
* `"GameRounds"`:
  Represents the separate rounds (i.e. turns) for each `"Games"` entry.

These tables are related to identity/login:

* `"Players"`:
  Represents someone/thing playing a `"Game"`,
    and will reference either a human `"Account"` or an AI player.
* `"Accounts"`:
  Represents a given (human) user,
    and is associated with all of their logins, history, etc.
* `"AccountRoles"`:
  Associates `"Accounts"` with security roles: `USERS` or `ADMINS`.
* `"AuthTokens"`:
  Stores the authentication tokens that have been issued for `"Accounts"`.
* `"LoginIdentities"`:
  A common base table for `"GameLoginIdentities"` and `"GuestLoginIdentities"`.
* `"GameLoginIdentities"`:
  A "full" login, with an email address and a password.
* `"GuestLoginIdentities"`:
  An automatically-generated login for a user
    that has not (yet) created a `"GameLoginIdentities`" entry.
  Has no additional columns and is basically just a marker table.
* `"AuditAccountMerges"`:
  Records a merge of two `"Accounts"` entries that has been performed.
* `"AuditAccountLoginMerges"`:
  Records the `"LoginIdentities"` that were merged as part of an `"AuditAccountMerges"` entry.
* `"AuditAccountGameMerges"`:
  Records the `"Games"` entries that were modified as part of an account merge.

## Common Database Tasks

### How do I connect to the database?

The simplest way to connect is using the `psql` utility included with PostgreSQL installations.
On Debian derivatives (e.g.. Ubuntu) it can be installed via `sudo apt install postgresql-client`.
On RedHat derivatives (e.g. Amazon Linux) it can be installed via `sudo yum install postgresql`.
It can be used like this:

    $ psql --host=<db_server_ip_or_hostname> --dbname=rps --username=<db_username>

From here, you can either run SQL statements or use some of the builtin commands.
To see the builtin commands, type `\?`.
To exit, type `\q`.

### Given an Email Address, What's Their Player ID?

If the user has created an account **and** started or joined a game,
  run this query to find their `"Players"` entry:

    SELECT
        "Players"."id",
        "Accounts"."name",
        "GameLoginIdentities"."emailAddress"
      FROM "GameLoginIdentities"
      INNER JOIN "LoginIdentities" ON "GameLoginIdentities"."id" = "LoginIdentities"."id"
      INNER JOIN "Accounts" ON "LoginIdentities"."accountId" = "Accounts"."id"
      INNER JOIN "Players" ON "Accounts"."id" = "Players"."humanAccountId"
      WHERE
        "GameLoginIdentities"."emailAddress" = '<email_address>';

If they haven't started or joined a game yet,
  that query will return no results because the `"Players"` entry hasn't been created yet.
Either have them create a (throwaway) game or run this:

    INSERT INTO "Players" ("id", "humanAccountId")
      SELECT
          (SELECT nextval('players_id_seq')),
          "Accounts"."id"
        FROM "GameLoginIdentities"
        INNER JOIN "LoginIdentities" ON "GameLoginIdentities"."id" = "LoginIdentities"."id"
        INNER JOIN "Accounts" ON "LoginIdentities"."accountId" = "Accounts"."id"
        WHERE
          "GameLoginIdentities"."emailAddress" = '<email_address>';

### How Do I Create a New Game for Two Players?

Given two `"Players"."id"` values representing two players
  and a pre-generated random `<game_id>` (ten characters, letters only),
  you can run these two statements to create a new game between the players:

    INSERT INTO "Games" ("id", "createdTimestamp", "state", "maxRounds", "player1Id", "player2Id") VALUES
      (
        <game_id>,
        now(), -- "createdTimestamp"
        'WAITING_FOR_FIRST_THROW', -- initial "state"
        3, -- default "maxRounds", can change to any odd number >= 1
        <player_1_id>,
        <player_2_id>
      );
    INSERT INTO "GameRounds" ("gameid", "roundIndex", "adjustedRoundIndex", "throwForPlayer1", "throwForPlayer1Timestamp", "throwForPlayer2", "throwForPlayer2Timestamp") VALUES
      (
        <game_id>,
        0, -- "roundIndex"
        0, -- "adjustedRoundIndex"
        NULL, -- "throwForPlayer1"
        NULL, -- "throwForPlayer1Timestamp"
        NULL, -- "throwForPlayer2"
        NULL -- "throwForPlayer2Timestamp"
      );

Once created, the game will be accessible at `https://<domain>/game/<game_id>`.
For example, on the [rpstourney.com](https://rpstourney.com/) domain,
  with a random game ID of "`abcdefghij`",
  the URL would be: <https://rpstourney.com/game/abcdefghij>.

### Is a Game Complete?

For a given ten-character `<game_id>`, run this query:

    SELECT "state" FROM "Games" WHERE "id" = '<game_id>';

If the returned value is "`FINISHED`", then the game is complete.
If not, it isn't.

### For a Completed Game, Who Won?

For a given ten-character `<game_id>`, run this query:

    WITH "GameRoundsResults" AS (
      SELECT
          "GameRounds".*,
          CASE
            WHEN "throwForPlayer1" = 'ROCK' AND "throwForPlayer2" = 'PAPER' THEN 'PLAYER_2'
            WHEN "throwForPlayer1" = 'ROCK' AND "throwForPlayer2" = 'SCISSORS' THEN 'PLAYER_1'
            WHEN "throwForPlayer1" = 'PAPER' AND "throwForPlayer2" = 'ROCK' THEN 'PLAYER_1'
            WHEN "throwForPlayer1" = 'PAPER' AND "throwForPlayer2" = 'SCISSORS' THEN 'PLAYER_2'
            WHEN "throwForPlayer1" = 'SCISSORS' AND "throwForPlayer2" = 'ROCK' THEN 'PLAYER_2'
            WHEN "throwForPlayer1" = 'SCISSORS' AND "throwForPlayer2" = 'PAPER' THEN 'PLAYER_1'
            WHEN "throwForPlayer1" = "throwForPlayer2" THEN 'TIE'
            ELSE NULL
          END AS "winner"
        FROM "GameRounds"
    )
    SELECT DISTINCT
        "Games".*,
        COUNT("GameRoundsResults"."winner")
          FILTER (WHERE "GameRoundsResults"."winner" = 'PLAYER_1')
          AS "player1Score",
        COUNT("GameRoundsResults"."winner")
          FILTER (WHERE "GameRoundsResults"."winner" = 'PLAYER_2')
          AS "player2Score"
      FROM "Games"
      INNER JOIN "GameRoundsResults" ON "Games"."id" = "GameRoundsResults"."gameid"
      GROUP BY
        "Games"."id"
      WHERE
        "Games"."id" = '<game_id>';

If the value of `"state"` is `FINISHED`,
  then whichever player has the higher score won the game.

### For an Incomplete Game, Which Player(s) Is It Waiting On?

For a given ten-character `<game_id>`, run this query:

    SELECT
        last_value("Games"."state") OVER wnd AS "state",
        last_value("GameRounds"."roundIndex") OVER wnd AS "roundIndex",
        last_value("GameRounds"."adjustedRoundIndex") OVER wnd AS "adjustedRoundIndex",
        last_value("Games"."player1Id") OVER wnd AS "player1Id",
        last_value("GameRounds"."throwForPlayer1Timestamp") OVER wnd AS "throwForPlayer1Timestamp",
        last_value("Games"."player2Id") OVER wnd AS "player2Id",
        last_value("GameRounds"."throwForPlayer2Timestamp") OVER wnd AS "throwForPlayer2Timestamp"
      FROM "Games"
      INNER JOIN "GameRounds" ON "Games"."id" = "GameRounds"."gameid"
      WHERE
        "gameid" = '<game_id>'
      WINDOW wnd AS (
        PARTITION BY "GameRounds"."gameid" ORDER BY "GameRounds"."roundIndex"
      );

(Please note: the case on the `"GameRounds"."gameid"` column doesn't match other columns in the DB.)

This will retrieve the most recent `"GameRounds"` entry for the specified `<game_id>`,
  along with some extra information.
Whichever player or players have a `NULL` value for the throw timestamp
  have not made a move in the round.
