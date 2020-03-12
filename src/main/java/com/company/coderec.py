import sublime
import sublime_plugin

import string
import re
from urllib import request, parse
import sys

class CoderecsysCommand(sublime_plugin.TextCommand):
	def run(self, edit):
		# self.view.insert(edit, 0, "Hello, World!")
		# line_number = self.view.lines(self.Region.begin(), self.Region.end())
		
		v = self.view
		sel_text = v.substr(v.sel()[0])
		cur_line = v.substr(v.line(v.sel()[0]))
		# print (v.sel()[0])
		# print (v.line(v.sel()[0]))
		print("Filename: ", v.file_name())
		print ("Selected text: ", sel_text)

		for sel in v.sel():
			line_begin = v.rowcol(sel.begin())[0]
			line_end = v.rowcol(sel.end())[0]
			print("Line number: ", line_begin + 1)
			# self.view.insert(edit, sel.end(), str(line_begin + 1))

		print ("Line: ", cur_line)

		# split = cur_line.split('(')[0]
		try:
			split = re.split(r'[\(]+', cur_line) # split by '('
			# print("Splitted (", split)
			split = split[-2].strip()
			result = re.findall(r'\w+', split) # find by letters and numbers ([a-zA-Z0-9_])
			func_name = result[-1]
			print("Function name: ", func_name)
			# -------------------------Request to server---------------------------
			url = "http://localhost:8080/getcode"
			my_url = url + "?func=" + func_name
			req = request.Request(my_url)
			resp = request.urlopen(req)
			resp_lines = resp.readlines()
			for line in resp_lines:
				print(line)
		except Exception:
			print("Error! Can't find a function in this", line_begin+1, "line. Try again on other line.")
			# print(sys.exc_info()[1])