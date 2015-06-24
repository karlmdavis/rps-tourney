#!/usr/bin/env python3

# This script parses the semi-formatted work-log.md file, and writes some of 
# the data out to a spreadsheet.
#
# The resulting spreadsheet will be structured as follows:
#  - Sheet: Issues by Days
#    - Columns: GitHub Issues
#    - Rows: Days
#
# Usage:
# $ ./work-log-parser.py work-log.md work-log.csv

import sys
import os
import collections
import re
import datetime

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
    
    # Parse the file.
    work_log_entries = parse_work_log(work_log_path)

    # Write out the file as a spreadsheet.
    write_data(work_log_entries, output_path)

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
    duration_entry_pattern = re.compile('^ *\* (\d+\.\d+)(?:h| hr)( (.+?))?')
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
            	times = duration_entry_match.group(3)
            	
            	issue_number_match = issue_number_pattern.match(line)
            	if issue_number_match is not None:
            	    issue_number = issue_number_match.group(1)
            	else:
            	    issue_number = None
            	
            	log_entries.append(LogEntry(last_date, duration, times, 
            	        issue_number, line_number, line))
            else:
            	log_entries.append(LogEntry(None, None, None, None, line_number, line))
    return log_entries

def write_data(work_log_entries, output_path):
    """
    Outputs the specified work log data to the specified file.
    
    Args:
        work_log_entries (str): The list of LogEntry tuples to write out.
        output_path (str): The file to write out to.
    
    Returns:
        (nothing)
    """
    
    with open(output_path, 'w') as output_file:
        output_file.write("date,issue_number,duration,times,line_number,text\n")
        for log_entry in work_log_entries:
            output_file.write("{},{},{},\"{}\",{},\"{}\"\n".format(
                    log_entry.date or "", log_entry.issue_number or "", 
                    log_entry.duration or "", log_entry.times or "", 
                    log_entry.line_number, log_entry.text))


# If this file is being run as a standalone script, call the main() function.
# (Otherwise, do nothing.)
if __name__ == "__main__":
    main()

