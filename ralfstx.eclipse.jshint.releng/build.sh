#!/bin/bash

JAVA_HOME=$HOME/tools/jvm/sun-jdk-1.7.0
MVN=$HOME/bin/mvn

targetRepo=$HOME/eclipse/targets/eclipse-rcp-indigo-sr1

# build

cd "$HOME/git/jshint-eclipse/ralfstx.eclipse.jshint.releng"

stat="$(git status -s)"
if [ -n "$stat" ]; then
  echo "uncommitted changes, run 'git status'"
  exit 1
fi

commit_date=$(date -u -d "$(git log -1 --format='%ci')" +"%Y%m%d-%H%M")
// TODO use commit date for build

$MVN -DtargetRepo=$targetRepo clean package || exit 1

# publish

version=`ls -1 repository/target/repository/features/*.jar | sed -e 's/.*_\([0-9\.-]*\)\.jar/\1/'`
if [ -z "$version" ]; then
  echo "Could not determine feature version"
  exit 1
fi
echo "Version: $version"

rsync -av repository/target/repository/ $HOME/git/ralfstx.github.com/update/jshint-eclipse/jshint-eclipse-$version
