# CLICKBAIT-FILTER-ML

[![ClickBaitSite](https://click-bait-filtering-plugin.com/assets/images/icon-128-122x122.png)](https://click-bait-filtering-plugin.com/index.html)

## Description

This application is a part of a group of services who plot to rid the web of clickbait by relying on user input and machine learning. The completed application functions by storing it’s user clicked items and using them to disseminate what is clickbait and what is legitimate news, stories, etc. This is done in conjunction with a machine learning classificator. The full application functions on all sites and thus can allow you to be more productive while browsing the web. This happens by providing you user with a slider giving him possibility to filter content, deemed clickbait and at the same time highlight content that is deemed not. In addition it can show it’s user the topology of the most clickbaity content of each domain.
</br>
</br>
This service is a Tensorflow Model generator and DB Source Updater. For more info visit the application [CLICKBAIT-PORTAL] and download the build of this plugin from the [CHROME-STORE].

## Technologies

CLICKBAIT-FILTER-ML uses a number of open source projects:

  * [KERAS] - MACHINE LEARNING FRAMEWORK
  * [NUMPY] - SCIENTIFIC COMPUTING LIB
  * [PY-MONGO] - PYTHON MONGO DB DRIVER
  * [TENSORFLOW-JS] - PYTHON TO JS MODEL CONVERTOR
  * [MATPLOT-LIB] - PYTHON PLOTTING LIBRARY


## Applications Scopes

This service is a part of a multi application project that features the following git repositories:

| Service Name                                  | Description                         | Maintainer              |
| ----------------------------------------      |:------------------------------------|:------------------------|
| [click_bait_filter_extension]                 | Chrome Extensions Plugin            | [LeadShuriken]          |
| [click_bait_filter_be]\(TEST_SERVER)           | Node Application Test Server        | [LeadShuriken]          |
| [click_bait_filter_j]                         | Spring Production Server            | [LeadShuriken]          |
| [click_bait_filter_tflow]                     | Java Tensor Flow Server             | [LeadShuriken]          |
| [click_bait_filter_ml]                        | TensorFlow Model Generator/Updater  | [LeadShuriken]          |
| [click_bait_filter_portal]                    | Service and Information Portal      | [LeadShuriken]          |


For development the application should have the following structure:
```sh
 | .
 | +-- click_bait_filter_extension
 | +-- click_bait_filter_be
 | +-- click_bait_filter_j
 | +-- click_bait_filter_tflow
 | +-- click_bait_filter_ml
 | +-- click_bait_filter_portal
```
This is as the 'click_bait_filter_ml' uses the 'click_bait_filter_be' api's for filtering links. 'click_bait_filter_portal' is just static html which can preside anywhere.

## Installation

CLICKBAIT-FILTER-ML requires [Python](https://www.python.org) v3.6+ to run.

To setup the python environments install `virtualenv` (venv) for the python and:

1. Create a virtual environment `$ virtualenv venv`
2. Activate virtual environment `$ source venv/bin/activate`

To install the python dependancies:

1. Make sure virtual environment is activated
2. `$ pip install -r requirements.txt`
3. Done

## Running the service

### 1. Runing the model generator service
---

* **WITH MICROSOFT VISUAL STUDIO CODE**

  To run the application, open the project in Microsoft VS Code and navigate to the .vscode folder.
  
  There you will see the **launch.json** file. And create this run configuration:
  
  ```sh
  {
      "name": "LAUNCH MODEL:GENERATE",
      "type": "python",
      "request": "launch",
      "program": "${workspaceFolder}/src/main.py",
      "justMyCode": false,
      "cwd": "${workspaceFolder}/src",
      "env": {
          "MONGO_CLIENT": "<MONGO CONNECTION STRING>",
          "MONGODB_POSITIVE": "<CLICKBAIT DATEBASE>",
          "MONGODB_NEGATIVE": "<NONE CLICKBAIT DATEBASE>"
      }
  }
  ```
  More information on [MONGO CONNECTION STRING] formats.

* **WITH CLI COMMANDS**

  Open the terminal and navigate to the root project folder.

  ```sh
  $ export MONGO_CLIENT=<MONGO CONNECTION STRING> && export MONGODB_POSITIVE=<CLICKBAIT DATEBASE> && export MONGODB_NEGATIVE=<NONE CLICKBAIT DATEBASE> && python ./src/main.py
  ```

This builds the model and exports the result as well as the word indexing (needed to index incoming links) to the ./model folder.

### 2. Runing the assets update service
---

* **WITH MICROSOFT VISUAL STUDIO CODE**

  To run the application, open the project in Microsoft VS Code and navigate to the .vscode folder.
  
  There you will see the **launch.json** file. And create this run configuration:
  
  ```sh
  {
      "name": "LAUNCH MODEL_DATA:UPDATE",
      "type": "python",
      "request": "launch",
      "program": "${workspaceFolder}/src/update.py",
      "justMyCode": false,
      "cwd": "${workspaceFolder}/src",
      "env": {
          "PORT": "<SERVER PORT>",
          "MONGO_CLIENT": "<MONGO CONNECTION STRING>",
          "MONGODB_POSITIVE": "<CLICKBAIT DATEBASE>",
          "MONGODB_NEGATIVE": "<NONE CLICKBAIT DATEBASE>"
          "MONGODB_RUNTIME": "<RUNTIME DATEBASE / CLICKBAIT-BE GENERATED>",
      }
  }
  ```

* **WITH CLI COMMANDS**

  Open the terminal and navigate to the root project folder.

  ```sh
  $ export PORT=<SERVER PORT> && export MONGO_CLIENT=<MONGO CONNECTION STRING> && export MONGODB_POSITIVE=<CLICKBAIT DATEBASE> && export MONGODB_NEGATIVE=<NONE CLICKBAIT DATEBASE> && export MONGODB_RUNTIME=<RUNTIME DATEBASE> && python ./src/update.py
  ```

  This starts a update server on: **http://localhost:SERVER PORT** 

### Todos

Model Consider with:

- K-Fold Cross Validation
- K-Fold Validation With Shuffeling
- Mini Batcht Tests


  [PY-MONGO]: <https://github.com/mher/pymongo>
  [KERAS]: <https://github.com/keras-team/keras>
  [NUMPY]: <https://github.com/numpy/numpy>
  [TENSORFLOW-JS]: <https://github.com/tensorflow/tfjs/tree/master/tfjs-converter/python>
  [MATPLOT-LIB]: <https://github.com/matplotlib/matplotlib>

  [click_bait_filter_extension]: <https://github.com/LeadShuriken/click_bait_filter_extension>
  [click_bait_filter_be]: <https://github.com/LeadShuriken/click_bait_filter_be>
  [click_bait_filter_ml]: <https://github.com/LeadShuriken/click_bait_filter_ml>
  [click_bait_filter_portal]: <https://github.com/LeadShuriken/click_bait_filter_portal>
  [click_bait_filter_j]: <https://github.com/LeadShuriken/click_bait_filter_j>
  [click_bait_filter_tflow]: <https://github.com/LeadShuriken/click_bait_filter_tflow>

  [LeadShuriken]: <https://github.com/LeadShuriken>

  [CHROME-STORE]: <https://chrome.google.com/webstore/detail/clickbait-filtering-plugi/mgebfihfmenffogbbjlcljgaedfciogm>
  [CLICKBAIT-PORTAL]: <https://click-bait-filtering-plugin.com>

  [MONGO CONNECTION STRING]: <https://docs.mongodb.com/manual/reference/connection-string>

![alt text](https://github.com/LeadShuriken/click_bait_filter_ml/blob/develop/model.png?raw=true)
