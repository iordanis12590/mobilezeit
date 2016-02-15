# Update client libs if necessary
#./gradlew build

# Move/replace and unzip libs in current directory
rm -rf wahlzeitApi
rm -rf wahlzeitApi wahlzeitApi-v1-java.zip
cp build/client-libs/wahlzeitApi-v1-java.zip ./
unzip *.zip

# Move/replace libs to android application
rm -rf ../Wahlzeit/app/src/main/java/com/appspot/
mv wahlzeitApi/src/main/java/com/appspot/ ../Wahlzeit/app/src/main/java/com/
