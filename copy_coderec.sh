ECHO "Copying coderec.py file into Sublime Text 3 plugins folder"

SRC_DIR="coderec.py"
DST_DIR=~"/.config/sublime-text-3/Packages/User"
cp "$SRC_DIR" "$DST_DIR"

ECHO "Copying is complete."
read -p "Press enter to continue"