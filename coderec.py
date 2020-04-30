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
    try:
        url = "http://localhost:8080/getcode?func=" + func_name
        req = request.Request(url)
        print(url)
        req.add_header('User-agent', 'Mozilla/5.0')
        with request.urlopen(req) as response:
            results = json.loads(response.read().decode("utf-8"))
            examples = []
            for result in results:
                examples.append(result["examples"])
            return examples
    except urllib.error.HTTPError as error:
        return json.loads(error.read().decode("utf-8"))

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

        # view.window().show_quick_panel(items = "0","1","2","3"], selected_index = 3, on_select = lambda x: print("s:%i"%x), on_highlight = lambda x: print("h:%i"%x))

        try:
            func_name = getFuncName(cur_line, pos[1])
            print("Func name: ", func_name)

            li_tree = ""
            final_data = get_data(func_name)
            for i in range(len(final_data)):
                li_tree += "<li>%s <a href='%s'>Copy</a></li>\n" %(final_data[i], final_data[i])

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
                            padding: 2px 5px;
                            text-align: center;
                            text-decoration: none;
                            display: inline-block;
                            font-size: 10px;
                            margin: 4px 2px;
                            cursor: pointer;
                        }
                    </style>

                    <ul>
                        %s
                    </ul>
                </body>
            """ %(li_tree)
            self.view.show_popup(html, max_width=512, on_navigate=lambda todo: self.copy_todo(todo))
            '''
            split = re.split(r'[\(]+', cur_line) # split by '('
            print("Splitted (", split)
            split = split[-2].strip()
            result = re.findall(r'\w+', split) # find by letters and numbers ([a-zA-Z0-9_])
            func_name = result[-1]
            print("Function name: ", func_name)
            '''

            '''
            # -------------------------Request to server---------------------------
            url = "http://localhost:8080/getcode"
            my_url = url + "?func=" + func_name
            req = request.Request(my_url)
            resp = request.urlopen(req)
            resp_lines = resp.readlines()
            for line in resp_lines:
                print(line)
                '''
        except Exception as ex:
            #print("Error! Can't find a function in this", line_begin+1, "line. Try again on other line.")
            #print(sys.exc_info()[1])
            print(ex)
            
    def copy_example(self, example, func_name):
        # Copies the todo to the clipboard.
        sublime.set_clipboard(example)
        self.view.hide_popup()
        sublime.status_message('Example of using ' + func_name + ' copied to clipboard !')