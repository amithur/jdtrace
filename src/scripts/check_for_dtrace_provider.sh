#!/usr/bin/sh

echo "in launchdtrace.sh $*"

keep_waiting=1

while [ $keep_waiting != 0 ]; do
  dtrace -l | grep Vvm > /dev/null
  keep_waiting=$?
  sleep 1
done

echo 7 > .stop_wait_jdtrace
