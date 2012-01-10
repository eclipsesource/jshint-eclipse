JSHint integration for the Eclipse IDE
======================================

[JSHint](http://www.jshint.com/about/) is a popular, community-driven tool to detect
errors and potential problems in JavaScript code.

[jshint-eclipse](http://ralfstx.github.com/jshint-eclipse/) integrates JSHint into
the Eclipse IDE.  It automatically validates *.js files and adds warning markers for
every problem found by JSHint.
For details, see the [project page](http://ralfstx.github.com/jshint-eclipse/).

Features
--------

JSHint validation can be enabled separately for every project in the IDE.  Whenever
a *.js file changes, it is validated. For every project, a separate set of
[configuration options](http://www.jshint.com/options/) can be defined.
The options are stored in the projects itself and can easily be shared over an SCM.

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

