#!/usr/bin/env python3

# This script parses the semi-formatted work-log.md file, and writes some of
# the data out to a SQLite DB.
#
# The resulting SQLite DB will contain all of the time log entries with
# durations. Where noted, it will also tie together issue numbers to those time
# log entries.
#
# Configuration:
# * The `GITHUB_REPO` constant in this file specifies which project issues will
#   be pulled from.
# * The directory this script is run from should have a `github.secret`
#   file, containing a GitHub personal access token with `repo:public_repo`
#   scope.
#
# Usage:
# $ ./work-log-parser.py work-log.md work-log.sqlite3

import sys
import os
import collections
import re
import datetime
import sqlite3
import contextlib

# The GitHub repo that issues will be pulled from.
GITHUB_REPO = 'karlmdavis/rps-tourney'

def main():
    """
    The main function for this script.
    """

    # Parse and verify the script args.
    if len(sys.argv) != 3:
    	raise ValueError("Input and output files not specified");
    work_log_path = sys.argv[1]
    if not os.path.exists(work_log_path):
    	raise ValueError("File to parse not found: " + work_log_path)
    output_path = sys.argv[2]

    # Read in the GitHub access token.
    github_token_path = 'github.secret'
    if not os.path.exists(github_token_path):
        raise ValueError("File to parse not found: " + github_token_path)
    with open (github_token_path, 'r') as github_token_file:
        github_token = github_token_file.read().strip()

    # Parse the file.
    work_log_entries = parse_work_log(work_log_path)

    # Create the output DB.
    connection = create_db(output_path)

    try:
        # Insert all of the project's GitHub issues to the DB.
        insert_github_issues(connection, github_token)

        # Insert all of the time log entries to the DB.
        insert_work_log_entries(connection, work_log_entries)

        # Create some analysis utilities in the DB.
        create_analysis_utils(connection)
    finally:
        connection.close()

def parse_work_log(work_log_path):
    """
    Parses the specified work log file.

    Args:
        work_log_path (str): The local path of the file to parse.

    Returns:
        A list of LogEntry tuples, one for every line in the specified file.
    """

    LogEntry = collections.namedtuple('LogEntry', ['date', 'duration', 'times',
            'issue_number', 'line_number', 'text'])
    log_entries = []
    date_pattern = re.compile('^### (\d\d\d\d-\d\d-\d\d)')
    duration_entry_pattern = re.compile('^ *\* (\d+(?:[\.\:]\d+)?)(?:h| hr)( \((.+?)\))?')
    issue_number_pattern = re.compile('.*Issue #(\d+)')
    issue_entry_pattern = re.compile('^ *\* (\d+\.\d+)(?:h| hr)( (.+?): \[Issue #(\d+): .+\]\(.+\))?')
    last_date = None
    with open(work_log_path, 'r') as work_log_file:
        for line_number, line in enumerate(work_log_file):
            line = line.replace('\n', '').replace('\r', '')
            date_match = date_pattern.match(line)
            if date_match is not None:
                last_date = datetime.datetime.strptime(
                        date_match.group(1), "%Y-%m-%d").date()
            duration_entry_match = duration_entry_pattern.match(line)
            if duration_entry_match is not None:
                duration = duration_entry_match.group(1)
                if ':' in duration:
                    duration_tokens = duration.split(':')
                    duration_token_hours = int(duration_tokens[0])
                    duration_token_minutes = int(duration_tokens[1])
                    duration_minutes = (duration_token_hours * 60) + duration_token_minutes
                elif '.' in duration:
                    duration_tokens = duration.split('.')
                    duration_token_hours = int(duration_tokens[0])
                    duration_token_minutes_fraction = float('0.' + duration_tokens[1])
                    duration_minutes = (duration_token_hours * 60) + (duration_token_minutes_fraction * 60)
                else:
                    duration_minutes = int(duration) * 60

                times = duration_entry_match.group(3)

                issue_number_match = issue_number_pattern.match(line)
                if issue_number_match is not None:
                    issue_number = issue_number_match.group(1)
                else:
                    issue_number = None

                log_entries.append(LogEntry(last_date, duration_minutes, times,
                        issue_number, line_number, line))
            else:
                log_entries.append(LogEntry(None, None, None, None, line_number, line))
    return log_entries

def create_db(output_path):
    """
    Creates/recreates a blank SQLite DB at the specified path.

    Args:
        output_path (str): The path to create the DB at.

    Returns:
        A Connection handle for the new SQLite DB.
    """

    # Create/recreate the output DB.
    with contextlib.suppress(FileNotFoundError):
        os.remove(output_path)
    conn = sqlite3.connect(output_path)

    return conn

def create_cursor(connection):
    """
    Creates a Cursor handle to the specified SQLite DB.

    Args:
        connection: The SQLite DB Connection to get a Cursor for.

    Returns:
        A Cursor handle to the specified SQLite DB.
    """

    # Create the Cursor.
    cursor = connection.cursor()

    # Enable FKs, because horrifyingly, they aren't by default.
    cursor.execute('PRAGMA foreign_keys = ON')

    return cursor

def insert_github_issues(connection, github_token):
    """
    Inserts the project's GitHub issues to the specified DB.

    Args:
        connection: The SQLite DB Connection to save data to.
        github_token (str): The GitHub personal access token to use.

    Returns:
        (nothing)
    """

    with contextlib.closing(create_cursor(connection)) as cursor:
        # Create DB schema.
        cursor.execute('''CREATE TABLE github_issues (
                         id INTEGER PRIMARY KEY,
                         title TEXT NOT NULL,
                         created_at TEXT NOT NULL,
                         closed_at TEXT,
                         is_bug BOOLEAN NOT NULL
                     )''')

        # Retrieve all of the issues from GitHub.
        from github import Github
        github_client = Github(github_token)
        repo = github_client.get_repo(GITHUB_REPO)
        issues = repo.get_issues(state='all')

        # INSERT all of the entries and issue refs.
        for issue in issues:
            cursor.execute('INSERT INTO github_issues VALUES (?,?,?,?,?)',
                      (issue.number, issue.title, issue.created_at,
                       issue.closed_at,
                       any("bug" == label.name for label in issue.labels)))
    # Commit all of that.
    connection.commit()

def insert_work_log_entries(connection, work_log_entries):
    """
    Inserts the specified work log data to the specified DB.

    Args:
        connection: The SQLite DB Connection to save data to.
        work_log_entries (str): The list of LogEntry tuples to insert.

    Returns:
        (nothing)
    """

    with contextlib.closing(create_cursor(connection)) as cursor:
        # Create DB schema.
        cursor.execute('''CREATE TABLE work_log_entries (
                         id INTEGER PRIMARY KEY,
                         date TEXT NOT NULL,
                         duration_minutes INTEGER NOT NULL,
                         issue_id INTEGER,
                         FOREIGN KEY(issue_id) REFERENCES github_issues(id)
                     )''')

        # INSERT all of the entries and issue refs.
        for log_entry in work_log_entries:
            if log_entry.date and log_entry.duration:
                cursor.execute('INSERT INTO work_log_entries VALUES (?,?,?,?)',
                          (None, log_entry.date, log_entry.duration,
                           log_entry.issue_number))
    # Commit all of that.
    connection.commit()

def create_analysis_utils(connection):
    """
    Creates some analysis utilities (e.g. views) in the specified DB.

    Args:
        connection: The SQLite DB Connection to use..

    Returns:
        (nothing)
    """

    with contextlib.closing(create_cursor(connection)) as cursor:
        # Create view for issue summaries.
        cursor.execute('''CREATE VIEW issue_analysis
                              (id, title, is_closed, is_bug, duration_minutes,
                               date_worked_first, date_worked_last,
                               dates_worked_count, dates_worked_elapsed)
                          AS
                          SELECT
                            github_issues.id,
                            github_issues.title,
                            CASE github_issues.closed_at
                              WHEN NULL THEN 0
                              ELSE 1
                            END,
                            github_issues.is_bug,
                            SUM(work_log_entries.duration_minutes),
                            MIN(work_log_entries.date),
                            MAX(work_log_entries.date),
                            COUNT(DISTINCT work_log_entries.date),
                            CAST((julianday(MAX(work_log_entries.date))
                                 - julianday(MIN(work_log_entries.date)))
                                 AS INT) + 1
                          FROM work_log_entries
                          LEFT JOIN github_issues
                            ON work_log_entries.issue_id = github_issues.id
                          GROUP BY github_issues.id
                          UNION ALL
                          SELECT
                            github_issues.id,
                            github_issues.title,
                            CASE github_issues.closed_at
                              WHEN NULL THEN 0
                              ELSE 1
                            END,
                            github_issues.is_bug,
                            SUM(work_log_entries.duration_minutes),
                            MIN(work_log_entries.date),
                            MAX(work_log_entries.date),
                            COUNT(DISTINCT work_log_entries.date),
                            CAST((julianday(MAX(work_log_entries.date))
                                 - julianday(MIN(work_log_entries.date)))
                                 AS INT) + 1
                          FROM github_issues
                          LEFT JOIN work_log_entries
                            ON work_log_entries.issue_id = github_issues.id
                          WHERE work_log_entries.issue_id IS NULL
                          GROUP BY github_issues.id
                          ORDER BY github_issues.id ASC
                          ''')

        # Create view for overal summary.
        cursor.execute('''CREATE VIEW overall_analysis
                              (date_worked_first, date_worked_elapsed,
                               dates_worked_count, dates_worked_elapsed,
                               total_duration_minutes, total_duration_hours,
                               average_worked_minutes_per_day_worked)
                          AS
                          SELECT
                            MIN(date),
                            MAX(date),
                            COUNT(DISTINCT date),
                            CAST((julianday(MAX(date))
                                 - julianday(MIN(date))) AS INT) + 1,
                            SUM(duration_minutes),
                            (SUM(duration_minutes) / 60.0),
                            (SUM(duration_minutes) * 1.0 / COUNT(DISTINCT date))
                          FROM work_log_entries
                          ''')
    # Commit all of that.
    connection.commit()


# If this file is being run as a standalone script, call the main() function.
# (Otherwise, do nothing.)
if __name__ == "__main__":
    main()
