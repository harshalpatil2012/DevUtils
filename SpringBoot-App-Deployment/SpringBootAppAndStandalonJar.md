Below is the script to create spring boot jar and traditional jar for the same project.
<!-- pom.xml -->

```xml
<project>
    <!-- Define project version -->
    <version>1.0.0-SNAPSHOT</version>

    <!-- Other configurations -->

    <profiles>
        <!-- Profile for standalone microservice JAR -->
        <profile>
            <id>standalone</id>
            <build>
                <plugins>
                    <plugin>
                        <!-- Configure the Spring Boot Maven plugin to create executable JAR -->
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-maven-plugin</artifactId>
                        <configuration>
                            <classifier>exec</classifier>
                        </configuration>
                        <executions>
                            <execution>
                                <goals>
                                    <goal>repackage</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>

        <!-- Profile for traditional JAR -->
        <profile>
            <id>traditional</id>
            <build>
                <plugins>
                    <plugin>
                        <!-- Configure Maven JAR plugin to exclude the embedded server -->
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-jar-plugin</artifactId>
                        <configuration>
                            <classifier>traditional</classifier>
                            <excludes>
                                <exclude>
                                    <groupId>org.springframework.boot</groupId>
                                    <artifactId>spring-boot-starter-tomcat</artifactId>
                                </exclude>
                                <!-- Exclude other server dependencies if using a different server -->
                            </excludes>
                        </configuration>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
</project>


In Jenkins job configuration:

```plaintext
clean install -DskipTests -P standalone,traditional
