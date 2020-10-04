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
  and a pre-generated random game ID (ten characters, letters only),
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

TODO

### For a Completed Game, Who Won?

TODO

### For an Incomplete Game, Which Player(s) Is It Waiting On?

TODO: and also how long has it been waiting
