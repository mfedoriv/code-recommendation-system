import sublime
import sublime_plugin

import string
import re
from urllib import request, parse
import sys
import json


def get_func_name(line, column):
    right_index = column
    left_index = column - 1
    while right_index < len(line):
        if re.match(r'\w', line[right_index]):
            char = line[right_index]
            right_index += 1
        else:
            break
    while line[left_index] == " ":
        left_index -= 1
    while left_index > 0:
        if re.match(r'\w', line[left_index]) and line[left_index] != " ":
            char = line[left_index]
            left_index -= 1
        else:
            break

    print("left: ", left_index, "right: ", right_index)
    func_name = line[left_index+1:right_index]
    print("In func: [" + func_name + "]")
    while line[right_index] == " ":
        right_index += 1
    if re.match(r'\(', line[right_index]):
        # print("Func Name: ", func_name)
        return func_name.strip()
    else:
        raise Exception("Can\'t find function name in this line. Move the cursor to another position or line.")


def get_data(func_name):
    sublime.status_message('Searching examples of using ' + func_name + ' (it may take up to 5 sec)...')
    try:
        url = "http://localhost:8080/getcode?func=" + func_name
        req = request.Request(url)
        print(url)
        req.add_header('User-agent', 'Mozilla/5.0')
        with request.urlopen(req) as response:
            results = json.loads(response.read().decode("utf-8"))
            return results
    except urllib.error.HTTPError as error:
        return json.loads(error.read().decode("utf-8"))


def get_const_data(func_name):
    input_file = open ("C:/Users/mf050/AppData/Roaming/Sublime Text 3/Packages/User/data_printf.json")
    results = json.load(input_file)
    return results


def escape_html(s):
    out = ""
    i = 0
    while i < len(s):
        c = s[i]
        number = ord(c)
        if number > 127 or c == '"' or c == '\'' or c == '<' or c == '>' or c == '&':
            out += "&#"
            out += str(number)
            out += ";"
        else:
            out += c
        i += 1
    out = out.replace(" ", "&nbsp;")
    out = out.replace("\n", "<br>")
    return out


def unescape_html(s):
    s = s.replace("<br>", "\n")
    s = s.replace("&nbsp;", " ")
    out = ""
    i = 0
    while i < len(s):
        if s[i] == "&" and s[i+1] == "#":
            i += 2
            number = ""
            while s[i] != ";":
                number += s[i]
                i += 1
            out += chr(int(number))
            i += 1
        else:
            out += s[i]
            i += 1
    return out

def dumb_escape_html(s):
    entities = [["&", "&amp;"], ["<", "&lt;"], [">", "&gt;"], ["\n", "<br>"],
                [" ", "&nbsp;"]]
    for entity in entities:
        s = s.replace(entity[0], entity[1])
    return s

def dumb_unescape_html(s):
    entities = [["&lt;", "<"], ["&gt;", ">"], ["<br>", "\n"],
                ["&nbsp;", " "], ["&amp;", "&"]]
    for entity in entities:
        s = s.replace(entity[0], entity[1])
    return s


class CoderecsysCommand(sublime_plugin.TextCommand):
    def run(self, edit):
        # self.view.insert(edit, 0, "Hello, World!")
        v = self.view
        sel_text = v.substr(v.sel()[0])
        cur_line = v.substr(v.line(v.sel()[0]))

        print("Filename: ", v.file_name())
        print ("Selected text: ", sel_text)
        # cpp-name identificator ([a-zA-Z_][a-zA-Z0-9_]*)
        for sel in v.sel():
            line_begin = v.rowcol(sel.begin())[0]
            line_end = v.rowcol(sel.end())[0]
            print("Line number: ", line_begin + 1)
            print("End number: ", line_end + 1)

        print ("Line: ", cur_line)
        pos = v.rowcol(v.sel()[0].begin()) # (row, column)
        print("Position of cursor in line: ", pos[1])

        try:
            func_name = get_func_name(cur_line, pos[1])
            print(func_name)
            li_tree = ""
            final_data = get_data(func_name)
            #final_data = get_const_data(func_name)
            for i in range(len(final_data)):
                source = "source: " + final_data[i]["source"]
                escaped = dumb_escape_html(final_data[i]["code"])
                #escaped = final_data[i]["code"]
                divider = "<b>____________________________________________________</b>"
                li_tree += "<li><p>%s</p>%s <a href='%s'>Copy</a></li><p>%s</p>" %(source, escaped, escaped, divider)
        # The html to be shown.
            html = """
                <head></head>
                                    <style>
                        ul {
                            margin: 0;
                        }

                        a {
                            background-color: #1c87c9;
                            border: none;
                            color: white;
                            padding: 3px 6px;
                            text-align: center;
                            text-decoration: none;
                            display: inline-block;
                            font-size: 12px;
                            margin: 4px 2px;
                            cursor: pointer;
                        }

                        p {
                            color: #1c87c9;
                        }

                        b {
                            color: #1c87c9;
                        }
                    </style>
                <body id=copy-multiline>
                    Examples of using <b>%s</b> function.
                    <ul>
                        %s
                    </ul>
                </body>
            """ %(func_name, li_tree)
            self.view.show_popup(html, max_width=700, on_navigate=lambda example: self.copy_example(example, func_name, source))
        except Exception as ex:
            self.view.show_popup("<b style=\"color:#1c87c9\">CodeRec Error:</b> " + str(ex), max_width=700)
            # print(ex)
            

    def copy_example(self, example, func_name, source):
        # Copies the code to the clipboard.
        unescaped = dumb_unescape_html(example)
        unescaped = "// " + source + unescaped
        sublime.set_clipboard(unescaped)
        self.view.hide_popup()
        sublime.status_message('Example of using ' + func_name + ' copied to clipboard !')