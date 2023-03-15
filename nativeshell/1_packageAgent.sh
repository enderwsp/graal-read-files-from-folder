echo $(date "+%Y%m%d_%H:%M:%SS")'=======================mvn clean package exec:exec -Pjava_agent=================sh 1_packageAgent.sh==starting:'
mvn clean package exec:exec -Pjava_agent
echo $(date "+%Y%m%d_%H:%M:%SS")'=======================mvn -X clean -DskipTests package=================sh 1_packageAgent.sh==end:'