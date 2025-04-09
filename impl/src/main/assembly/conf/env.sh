APP_PORT=22005
APP_NAME=ifte-investment-adviser-dubbo-service

if [ -f "/opt/applications/tingyun/tingyun-agent-java.jar" ];then
  JAVA_AGENT_OPTS=" -javaagent:/opt/applications/tingyun/tingyun-agent-java.jar -Dtingyun.app_name=ifte-financial_${APP_NAME} "
fi

PROFILE=$(echo ${FOUNDERSC_ENV} | tr '[A-Z]' '[a-z]')
case ${PROFILE} in
  dev)
      JAVA_OPT=" ${JAVA_AGENT_OPTS} -server -Xmx800m -Xms800m -Xmn256m -XX:MetaspaceSize=128m -Xss512k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:-OmitStackTraceInFastThrow -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/opt/logs/${APP_NAME}/oom.hprof "
      ;;
  qa)
      JAVA_OPT=" ${JAVA_AGENT_OPTS} -server -Xmx800m -Xms800m -Xmn256m -XX:MetaspaceSize=128m -Xss512k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:-OmitStackTraceInFastThrow -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/opt/logs/${APP_NAME}/oom.hprof "
      ;;
  pre)
      JAVA_OPT=" ${JAVA_AGENT_OPTS} -server -Xmx1g -Xms1g -Xmn500m -XX:MetaspaceSize=128m -Xss512k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:-OmitStackTraceInFastThrow -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/opt/logs/${APP_NAME}/oom.hprof "
      ;;
  product)
      JAVA_OPT=" ${JAVA_AGENT_OPTS} -server -Xmx2g -Xms2g -Xmn768m -XX:MetaspaceSize=128m -Xss512k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:-OmitStackTraceInFastThrow -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/opt/logs/${APP_NAME}/oom.hprof "
      ;;
  *)
      ;;
esac