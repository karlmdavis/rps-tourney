CREATE FUNCTION check_GameRounds_throws() RETURNS trigger AS $check_GameRounds_throws$
	BEGIN
		-- Ensure that throwForPlayer1 isn't being modified (if already set).
		IF OLD."throwForPlayer1" IS NOT NULL AND (OLD."throwForPlayer1" IS DISTINCT FROM NEW."throwForPlayer1") THEN
			RAISE EXCEPTION 'throwForPlayer1 already set to % and cannot be modified to %', COALESCE(oldRow."throwForPlayer1", 'NULL'), COALESCE(newRow."throwForPlayer1", 'NULL');
		END IF;
		
		-- Ensure that throwForPlayer2 isn't being modified (if already set).
		IF OLD."throwForPlayer2" IS NOT NULL AND (OLD."throwForPlayer2" IS DISTINCT FROM NEW."throwForPlayer2") THEN
			RAISE EXCEPTION 'throwForPlayer2 already set to % and cannot be modified to %', COALESCE(oldRow."throwForPlayer2", 'NULL'), COALESCE(newRow."throwForPlayer2", 'NULL');
		END IF;
		
		RETURN NEW;
	END;
$check_GameRounds_throws$ LANGUAGE plpgsql;

CREATE TRIGGER check_throws
	BEFORE UPDATE ON "GameRounds"
	FOR EACH ROW
	EXECUTE PROCEDURE check_GameRounds_throws();
