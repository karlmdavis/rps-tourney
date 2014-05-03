CREATE TRIGGER GameRounds_throwProtect BEFORE UPDATE ON "PUBLIC"."GameRounds"
REFERENCING OLD AS oldRow NEW AS newRow
FOR EACH ROW
BEGIN ATOMIC
	DECLARE errorText varchar(200);
	IF oldRow."throwForPlayer1" IS NOT NULL AND (oldRow."throwForPlayer1" IS DISTINCT FROM newRow."throwForPlayer1") THEN
		SET errorText = 'throwForPlayer1 already set to ' + COALESCE(oldRow."throwForPlayer1", 'NULL') + ' and cannot be modified to ' + COALESCE(newRow."throwForPlayer1", 'NULL');
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = errorText;
	END IF;
	IF oldRow."throwForPlayer2" IS NOT NULL AND (oldRow."throwForPlayer2" IS DISTINCT FROM newRow."throwForPlayer2") THEN
		SET errorText = 'throwForPlayer2 already set to ' + COALESCE(oldRow."throwForPlayer2", 'NULL') + ' and cannot be modified to ' + COALESCE(newRow."throwForPlayer2", 'NULL');
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = errorText;
	END IF;
END
