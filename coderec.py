import sublime
import sublime_plugin

import string
import re
from urllib import request, parse
import sys
import json


def getFuncName(line, column):
    # test: file = (char**)calloc12(500, sizeof(char*));
    # split = cur_line.split('(')[0]
    right_index = column
    left_index = column
    while right_index < len(line):
        if re.match(r'\w', line[right_index]):
            char = line[right_index]
            right_index += 1
        else:
            break
    while left_index >= 0:
        if re.match(r'\w', line[left_index]):
            char = line[left_index]
            left_index -= 1
        else:
            break
    print("left: ", left_index, "right: ", right_index)
    func_name = line[left_index+1:right_index]
    if re.match(r'\(', line[right_index]):
        # print("Func Name: ", func_name)
        return func_name
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
    print("GET DATA\n")
    input_file = open ("C:/Users/mf050/AppData/Roaming/Sublime Text 3/Packages/User/data_fopen_new.json")
    results = json.load(input_file)
    return results

class CoderecsysCommand(sublime_plugin.TextCommand):

    def run(self, edit):
        # self.view.insert(edit, 0, "Hello, World!")
        # line_number = self.view.lines(self.Region.begin(), self.Region.end())
        v = self.view
        sel_text = v.substr(v.sel()[0])
        cur_line = v.substr(v.line(v.sel()[0]))
        #print (v.sel()[0])
        #print (v.line(v.sel()[0]))
        print("Filename: ", v.file_name())
        print ("Selected text: ", sel_text)
        # cpp-name identificator ([a-zA-Z_][a-zA-Z0-9_]*)
        for sel in v.sel():
            line_begin = v.rowcol(sel.begin())[0]
            line_end = v.rowcol(sel.end())[0]
            print("Line number: ", line_begin + 1)
            print("End number: ", line_end + 1)
            #self.view.insert(edit, sel.end(), str(line_begin + 1))

        print ("Line: ", cur_line)
        pos = v.rowcol(v.sel()[0].begin()) # (row, column)
        print("Position of cursor in line: ", pos[1])

        try:
            func_name = getFuncName(cur_line, pos[1])

            li_tree = ""
            final_data = get_data(func_name)
            #final_data = get_const_data(func_name)
            print(type(final_data[0]))
            for i in range(len(final_data)):
                source = "source: " + final_data[i]["source"]
                escaped = final_data[i]["code"].replace("<","&lt;").replace(">","&gt;")
                escaped = escaped.replace("\n", "<br>").replace(" ", "&nbsp;")
                divider = "<b>____________________________________________________</b>"
                li_tree += "<li><p>%s</p>%s <a href='%s'>Copy</a></li><p>%s</p>" %(source, escaped, escaped, divider)
        # The html to be shown.
            html = """
                <body id=copy-multiline>
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
        # Copies the todo to the clipboard.
        print("COPY")
        de_escaped = example.replace("&lt;", "<").replace("&gt;", ">")
        de_escaped = de_escaped.replace("<br>", "\n").replace("&nbsp;", " ")
        de_escaped = "// " + source + de_escaped
        sublime.set_clipboard(de_escaped)
        self.view.hide_popup()
        sublime.status_message('Example of using ' + func_name + ' copied to clipboard !')