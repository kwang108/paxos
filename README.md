# Prerequisite
1. Assume running on Mac
2. Install JDK 1.8 (Download from https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

# Structure of repo
All source code is in Java and is located under src/com/paxos. Problem 1 is in package com/paxos/messages, and the main
logic in in MessageHandler.java. MessageServer.java contains code that sets up and starts up the http server.

Problem 2 is in package com/paxos/gifts and there is only one class BuyGifts.java. 

# How to Run Problem 1 from Commmand Line
0. Make you have installed JDK 1.8 (latest version is fine)
1. cd into folder "paxos".
2. Run: mkdir out
3. Run: mkdir out/production
4. javac -cp lib/netty-all-4.1.33.Final.jar:lib/gson-2.8.5.jar src/com/paxos/messages/*.java -d out/production/
5. java -cp lib/netty-all-4.1.33.Final.jar:lib/gson-2.8.5.jar:out/production/ com.paxos.messages/MessageServer
6. You should see output "Server now running on port 8080" 
7. Ctrl+C to terminate

# How to Run Problem 2 from Command Line
0. Make you have installed JDK 1.8 (latest version is fine)
1. cd into folder "paxos".
2. Run ls and you should see these files and directories: README.md, lib, resources, src
3. Run if haven't: mkdir out
4. Run if haven't: mkdir out/production
5. Run: javac -cp lib src/com/paxos/gifts/BuyGifts.java -d out/production/
6. Run: java -cp lib:out/production/ com.paxos/gifts/BuyGifts resources/prices.txt 2500
7. If you want to change the input, you can either modify "resources/prices.txt" or provide an absolute path to an input file.
8. 2500 is the budget, and must follow the price file path separated by a space.

# Project Setup in IntelliJ
1. Down IntelliJ community edition from https://www.jetbrains.com/idea/download
2. In IntelliJ, create a new Java project by File -> New -> Project from Existing Sources, and select "paxos" directory
3. In IntelliJ, File -> Project Structure, on the popup navigate to Libraries (under Project Settings)
4. Click the "+" button to add a library, and select the "lib" folder from "paxos". This will add the netty-all-4.1.33.Final.jar and gson-2.8.5.jar

# How to Run Problem 1 in IntelliJ
1. The code for Problem 1 is located under src/com/paxos/messages.
2. The main driver is MessageServer.java
3. Right-click on the MessageServer and click Run MessageServer.main(). This will start the http server on port 8080.
4. To terminate, just click the red square button.

# How to Run Problem 2 in IntelliJ
1. The code for Problem 1 is located under src/com/paxos/gifts.
2. The main driver is BuyGifts.java
3. Right-click on the MessageServer and click Run BuyGifts.main(). This will run the code with default input from prices.txt and budget 2500.
4. You can easily modify the default input which is at the beginning at the static main() method.
5. To pass in a file and budget to the driver, you need to go to the IntelliJ menu Run -> Edit Configurations...
6. In the popup, click "+" then choose "Application".
7. In the right-hand-side section, for field "Main Class", navigate to and select com.paxos.gifts.BuyGifts.
   For field "Program Arguments", enter "<absolute path to input file> <budget>".