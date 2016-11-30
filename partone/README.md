# File Encryption and Decryption

The code here allows for a user to encrypt and decrypt files using an AES-128
blockcipher that goes through CBC in order to generate a securely encrypted
file with associated HMAC that acts as a signature.

In order to build this program, the two maven commands:

    mvn clean install
    mvn jar:jar

can be used in order to create the project jar file from the classes. This jar
will be deposited in the local target/ directory for the partone folder. Two
commands can be used on this jar file. In order to encrypt data, a user can
input:

    java -jar partone-1.0-SNAPSHOT.jar -e [filename]

This will prompt the user for a password that the file will be encrypted with,
and the encrypted contents of [filename] will be placed into [filename].8102
for later decryption. If a file which already has the .8102 file extention is
passed to the program, it will refuse service.

In order to decrypt that file later down the line, the user can run:

    java -jar partone-1.0-SNAPSHOT.jar -d [filename].8102

This will prompt the user for the password initially used to encrypt this file,
then will attempt to decrypt this file with that password and put the decrypted
data into [filename], deleting [filename].8102 afterwards. If the provided file
does not have the .8102 extention, then the program will refuse service. The
same happens if the password does not match the password that was originally
used to encrypt the given file.
