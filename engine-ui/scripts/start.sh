#!/bin/bash

echo "Starting the DAGScheduleX UI Application...";

# TODO: check whether has installed the cup app before running 
echo "Install the server Application Cup";
npm install -g mini-cup;

echo "Starting the Application Server"

cup config;

