JSHint Integration for Eclipse
==============================

JSHint (http://www.jshint.com) is a popular tool for JavaScript code analysis.
This integration provides JSHint validation for the Eclipse IDE.

Features
--------

JSHint validation can be enabled separately for every project.
Whenever a *.js file changes, it is validated and a warning marker is added for every problem found.
The configuration options for JSHint (http://www.jshint.com/options/) can also be set on project level and are persisted in the projects itself.

Requirements
------------

Eclipse 3.6 (Helios) or newer.

Installation
------------

Install from this Eclipse update site: http://ralfstx.github.com/update/jshint-eclipse/

* Eclipse main menu: _Help_ -> _Install New Software..._

Usage
-----

* Open the context menu on a project and select _Properties_
* On the JSHint page, enable JSHint for this project
* Exclude non-source files and folders from JSHint validation in the context menu of the respective files/folders

License
-------

The code is published under the terms of the [Eclipse Public License, version 1.0](http://www.eclipse.org/legal/epl-v10.html).

Includes code from [jshint](https://github.com/jshint/jshint/), version r03, which is published under the terms of the MIT license with the addition "The Software shall be used for Good, not Evil."

