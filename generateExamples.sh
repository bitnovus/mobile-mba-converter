#!/bin/bash

java -jar target/mmba-jar-with-dependencies.jar examples/sprint_demo_data examples/results , no no no
java -jar target/mmba-jar-with-dependencies.jar examples/tmobile_demo_data examples/results , no no no
java -jar target/mmba-jar-with-dependencies.jar examples/vzwireless_demo_data examples/results , no no no

java -jar target/mmba-jar-with-dependencies.jar examples/sprint_demo_data examples/results , yes no yes 
java -jar target/mmba-jar-with-dependencies.jar examples/tmobile_demo_data examples/results , yes no yes 
java -jar target/mmba-jar-with-dependencies.jar examples/vzwireless_demo_data examples/results , yes no yes
