
# Code Recommendation System
Sublime Text 3 plugin which allows you to get examples of how to use C functions with one press of the shortcut.

### Installation
1. Copy coderec.py file in Sublime Text 3 plugins directory:
    - If you're using **Windows** run *copy_coderec.***bat****.
    - If you're using **Linux** run *copy_coderec.***sh****
2. To add shortcut:
    1. Go to Preferences &#8594;Key bindings.
    2. In the file on the right *Default (...).sublime-keymap – User* add the following line: `{ "keys": ["alt+shift+c"], "command": "coderecsys" }`

#### Create a jar archive of the server
In directory with *pom.xml* run `mvn clean install`

### Using
1. Run the server from the **/target** folder with command: `java -jar code-recommendation-system-1.0-SNAPSHOT-jar-with-dependencies.jar`. Running the server is **REQUIRED** to use the plugin - without it, nothing will work. To stop server press `Ctrl+C` in terminal window.
2. In **Browser**:
    1. You can check the server status by going to the address in the browser [http://localhost:8080/status](http://localhost:8080/status).
    2. You can configure the server and select the sites to be searched on by going to the address in the browser [http://localhost:8080/settings](http://localhost:8080/settings).  Note: to search on StackOverflow you need access_token! Information about how to get it can be found on the Settings page.
    3. You can search for examples of using the functions in your browser by going to [http://localhost:8080/search](http://localhost:8080/search).
    4. To get the result as JSON go to the address [http://localhost:8080/getcode](http://localhost:8080/getcode) and send the name of the C function in the **func** parameter. For example [http://localhost:8080/getcode?func=fopen](http://localhost:8080/getcode?func=fopen)
3. In **Sublime Text 3**:
    1. Place your cursor on the name of the C function;
    2. Press `Alt+Shift+C`
    3. In a few seconds (~5) a pop-up window will show examples of using the function.
    4. To copy the required example, press the **Copy** button below it (the code will be copied to the clipboard), and then paste with the `Ctrl+V` key combination where you want.