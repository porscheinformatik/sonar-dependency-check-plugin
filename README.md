Dependency Check Sonar Plugin
===================

[![Build Status](https://travis-ci.org/porscheinformatik/sonar-dependency-check-plugin.png?branch=master)](https://travis-ci.org/porscheinformatik/sonar-dependency-check-plugin)

This Sonar plugin ensures that projects in an organization adhere to a set of standard libraries and versions. This enables the governance of the used libraries and licences.

## License

This software is licensed under the Apache Software License, Version 2.0, http://www.apache.org/licenses/LICENSE-2.0.txt

## Compatibility

SonarQube >= 4.4 and < 5.0

## Features

### Analysis

During the project analysis all dependencies are checked against the defined list of allowed libraries. If a project uses an unlisted dependency, a violation with level "Blocker" will be added. If a project uses a listed dependency out of version range, a violation with level "Critical" will be added.

### Dependecy Check Page

The plugin contains a page with all libraries (in form library:version), the licenses used and the status of this dependency. The view also displays a list of all used licences.

Example:
<table>
  <tr><th>Lib</th><th>Version</th><th>Status</th></tr>
  <tr><td>org.springframework:spring-core:3.2.1.RELEASE</td><td>Apache 2.0</td><td>OK</td></tr>
  <tr><td>org.springframework:spring-beans:3.2.1.RELEASE</td><td>Apache 2.0</td><td>OK</td></tr>
  <tr><td>commons-logging:commons-logging:1.1.1</td><td>Apache 2.0</td><td><b>Wrong version</b></td></tr>
  <tr><td>net.sf.jopt-simple:jopt-simple:3.0</td><td>MIT</td><td><b>Unlisted</b></td></tr>
</table>

Licenses:
<table>
  <tr><th>License</th><th>Description</th><th>URL</th></tr>
  <tr><td>Apache 2.0</td><td>The Apache Software License, Version 2.0</td><td>http://www.apache.org/licenses/LICENSE-2.0.txt</td></tr>
  <tr><td>MIT</td><td>The MIT License</td><td>http://www.opensource.org/licenses/mit-license.php</td></tr>
</table>

### Configuration

#### Licenses

A list of licenses can be configured via Settings > Configuration.

<table>
  <tr><th>License</th><th>Description</th><th>URL</th></tr>
  <tr><td>Apache 2.0</td><td>The Apache Software License, Version 2.0</td><td>http://www.apache.org/licenses/LICENSE-2.0.txt</td></tr>
  <tr><td>MIT</td><td>The MIT License</td><td>http://www.opensource.org/licenses/mit-license.php</td></tr>
  <tr><td>LGPL 2.1</td><td>GNU Lesser General Public License (LGPL), Version 2.1</td><td>http://www.fsf.org/licensing/licenses/lgpl.txt</td></tr>
</table>

#### Libraries

The list of allowed dependencies with a given version range can be configured via Settings > Configuration.

Here is an example:
<table>
  <tr><th>Lib</th><th>Allowed Versions</th><th>License</th></tr>
  <tr><td>org.springframework:spring-core</td><td>[3.0,4.0)</td><td>Apache 2.0</td></tr>
  <tr><td>commons-logging:commons-logging</td><td>1.1.3</td><td>Apache 2.0</td></tr>
</table>

The version ranges have to be specified in Maven syntax (see http://maven.apache.org/enforcer/enforcer-rules/versionRanges.html).

This list can be configured in general settings and can be customizable on a per-project basis (if only one project uses a certain library).
