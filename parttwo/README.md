# Hash Cracking

The code within this folder allows a user to use a dictionary to attempt to
crack a series of hashes. The dictionary is comprised of three files in the
`dictionary` subfolder along with the following combinations: a
case-sensitive name from `girl_names.txt` or `boy_names.txt` with a number
between 1 and 9999 after it, e.g. `BoB1992` or `amElIA23`. Or a 4-character
case-sensitive alphanumeric string which also contains a few special symbols,
e.g. `aY@2` or `c*lO`.

In order to build this program, the two maven commands:

    mvn clean install
    mvn jar:jar

can be used in order to create the project jar file from the classes. This jar
will be deposited in the local target/ directory for the parttwo folder. The
user is required to enter:

    java -jar parttwo-1.0-SNAPSHOT.jar -i [hashes_input] -o [password_output]

Which will read each line of the [hashes_input] and try to crack each hash
against our dictionary. Any matching hashes that are found will be placed into
the [password_output] file in the format of:

    Hash: [hash] Password: [password]

This program, tested with the coursework `hashes.txt` file, took roughly 24
seconds before it found the solution to eight hashes, testing them against
roughly 35.5 million different passwords.

__NOTE:__ The user must execute the program while they are in a directory with
the dictionary folder. If this folder and the three files within are not found,
then this program will not be able to generate the dictionary. This program
does work as downloaded if the user executes the above command while they are
in the parttwo folder but will not work if they are in the folder above or
below that.
