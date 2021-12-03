#!/bin/sh
java -cp `dirname $0`/apps/gforge-java-cli.jar:`dirname $0`/lib com.alcatel_lucent.acos.gforgeJavaCli.FRS "$@"
