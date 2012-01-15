#!/bin/bash

JAVA_HOME=$HOME/tools/jvm/sun-jdk-1.7.0
MVN=$HOME/bin/mvn

targetRepo=$HOME/eclipse/targets/eclipse-rcp-indigo-sr1
includePaths="ralfstx.eclipse.jshint ralfstx.eclipse.jshint.feature"

# make sure we're in the git repository root
if [ ! -d ".git" ]; then
  echo "must be run from the repository root"
  exit 1
fi

# make sure the local repository is clean
stat="$(git status -s)"
if [ -n "$stat" ]; then
  echo "uncommitted changes, run 'git status'"
  exit 1
fi

# determine last commit
echo "Find latest commit date in paths:"
for path in $includePaths; do
  echo "* $path"
done
commit_hash=$(git log -1 --format='%h' -- $includePaths)
commit_subject=$(git log -1 --format='%s' -- $includePaths)
commit_date=$(date -u -d "$(git log -1 --format='%ci' -- $includePaths)" +"%Y%m%d-%H%M")
echo "-> $commit_date $commit_subject [$commit_hash]"

cd "ralfstx.eclipse.jshint.releng" || exit 1

$MVN -DtargetRepo=$targetRepo clean package || exit 1

# copy resulting repository
version=`ls -1 repository/target/repository/features/*.jar | sed -e 's/.*_\([0-9\.-]*\)\.jar/\1/'`
if [ -z "$version" ]; then
  echo "Could not determine feature version"
  exit 1
fi
echo "Version: $version"

mkdir -p /tmp/jshint-eclipse
rsync -av repository/target/repository/ /tmp/jshint-eclipse/jshint-eclipse-$version
