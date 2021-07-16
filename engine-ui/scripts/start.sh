#!/bin/bash

echo "Starting the DAGScheduleX UI Application...";

res=`npm list -g mini-cup`
sub='mini-cup'

if [[ !($res =~ $sub) ]]
then
    echo "Install the server Application Cup";
    npm install -g mini-cup;
fi

echo "Starting the Application Server"

cup config;

