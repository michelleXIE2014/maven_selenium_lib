#!/bin/bash
# this shell script should be ran in your local environment
current_path=`pwd`
jar_path=$current_path/target
jar_file=$jar_path/lazywork-test-framework-$1-SNAPSHOT.jar
lib_pom="$current_path/pom.xml"
test_repo_path=$(dirname $current_path)/automation_test_example
test_jar_file_path=$test_repo_path/src/test/resources
test_jar_file=$test_jar_file_path/lazywork-test-framework-$1-SNAPSHOT.jar
test_pom=$test_repo_path/pom.xml
test_install_file=$test_repo_path/scripts/install_local_lib.sh
               
if [[ -z "$1" ]]; then
    echo "Please enter the version number"
    exit 0;
else
    if [ -f "$jar_file" ]; then
        echo "$jar_file exists"
        exit 0;
    else
        echo "1. change the lib version"
        sed "/<artifactId>lazywork-test-framework<\/artifactId>/,/<modelVersion>4.0.0<\/modelVersion>/ s/<version>[0-9].[0-9]-SNAPSHOT<\/version>/<version>$1-SNAPSHOT<\/version>/g" $lib_pom > tmp;
        mv tmp $lib_pom
        
        echo "2. change packaging to jar"
        sed "s/<packaging>pom<\/packaging>/<packaging>jar<\/packaging>/g" $lib_pom > tmp;
        mv tmp $lib_pom
        
        echo "3. make package"
        mvn package -f $lib_pom
        ls $jar_path
        
        echo "4. copy the jar file to test repo"
        if ls $test_jar_file_path/lazywork-test-framework-*.jar 1> /dev/null 2>&1; then
            rm $test_jar_file_path/lazywork-test-framework-*.jar
            echo "yes"
        fi    
        ls -al
        cp $jar_file $test_jar_file
        
        echo "5. install the jar"
        mvn install:install-file -Dfile=$test_jar_file -DgroupId=com.lazywork.testframework -DartifactId=lazywork-test-framework -Dversion=$1-SNAPSHOT -Dpackaging=jar
        
        echo "6. change the test pom.xml version"
        sed "s/<testlib-version>[0-9].[0-9]-SNAPSHOT<\/testlib-version>/<testlib-version>$1-SNAPSHOT<\/testlib-version>/g" $test_pom > tmp;
        mv tmp $test_pom 
        sed "/<parent>/,/<\/parent>/ s/<version>[0-9].[0-9]-SNAPSHOT<\/version>/<version>$1-SNAPSHOT<\/version>/g" $test_pom > tmp;
        mv tmp $test_pom 
        
        echo "7. change packaging back to pom"
        sed "s/<packaging>jar<\/packaging>/<packaging>pom<\/packaging>/g" $lib_pom > tmp;
        mv tmp $lib_pom
        
        echo "8. copy the pom file to test repo"
        if [ -f "$test_jar_file_path/lib_pom.xml" ]; then
            rm $test_jar_file_path/lib_pom.xml
        fi
        cp pom.xml $test_jar_file_path/lib_pom.xml
    fi
fi
