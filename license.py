import os

file_list=[]

def gci(filepath):
  files = os.listdir(filepath)
  for fi in files:
    fi_d = os.path.join(filepath,fi)
    if os.path.isdir(fi_d):
      gci(fi_d)
    else:
      if fi_d.endswith(('.java')):
        readfile(os.path.join(filepath, fi_d))

lister = ['Licensed to the Apache Software Foundation (ASF) under one']
def readfile(filename):
    try:
        fopen = open(filename,'r')
        fileread=fopen.read()
        fopen.close()
        for lis in lister:
            if lis not in fileread:
                file_list.append(filename)
                writefile(filename)
    except Exception as e:
        print e

license ='''/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'''

def writefile(filename):
    try:
        with open(filename, "r+") as f:
            old = f.read()
            f.seek(0)
            f.write(license)
            f.write(old)
    except Exception as e:
        print e

gci(os.getcwd())
print "file_list ",file_list
