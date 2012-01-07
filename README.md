JSHint Integration for Eclipse
==============================

JSHint (http://www.jshint.com) is a popular tool for JavaScript code analysis.
This project provides JSHint validation for the Eclipse IDE.

JSHint validation can be enabled separately for every project.
The configuration options for JSHint (http://www.jshint.com/options/) can also be set on project level and are persisted in the projects itself.
Whenever a file changes, the JSHint validation will check the changed files and add a warning marker for every problems found.

Installation
------------

Install from this Eclipse update site: http://ralfstx.github.com/update/eclipse-jslint/
* Eclipse Menu -> Help -> Install New Software...

Usage
-----

* Open the context menu on a project and select Properties
* On the JSHint page, enable JSHint for this project
* Exclude non-source files from JSHint validation in the context menu of the respective files

License
-------

The code is published under the terms of the Eclipse Public License, version 1.0.

Includes code from jshint (http://www.jshint.com), version r03, which is published under the terms of the MIT license with the addition "The Software shall be used for Good, not Evil."

