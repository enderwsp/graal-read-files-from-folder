
echo $(date "+%Y%m%d_%H:%M:%SS")'========================================sh 3_nativeRun.sh==starting:'

echo "[-->] Detect artifactId from pom.xml"
ARTIFACT=$(mvn -q \
-Dexec.executable=echo \
-Dexec.args='${project.artifactId}' \
--non-recursive \
exec:exec);
echo "artifactId is '$ARTIFACT'"

./target/$ARTIFACT
