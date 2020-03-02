# CS4218 Project: Shell Application [![Build Status](https://travis-ci.com/nus-cs4218/cs4218-project-ay1920-s2-2020-team22.svg?token=jKdVBsb1h7BZ2PCTqrnm&branch=master)](https://travis-ci.com/nus-cs4218/cs4218-project-ay1920-s2-2020-team22)

## Table of Contents
  * [Overview](#overview)
  * [Technologies used](#technologies-used)
  * [Instructions to setup project](#instructions-to-setup-project)
  * [Instructions to run the Shell program](#instructions-to-run-the-shell-program)
  * [Instructions to run all our test cases](#instructions-to-run-all-our-test-cases)
  * [External libraries / plugins / tools / technologies used](#external-libraries-/-plugins-/-tools-/-technologies-used)
  * [Acknowledges / References](#acknowledges-/-references)

## Overview

A shell is a command interpreter. Its responsibility is to interpret commands that the user types and to run programs that the user specifies in her command lines.
Figure 1 shows the relationship between the shell, the kernel, and various applications/utilities in a UNIX- like operating system:

![CS4218 Shell Architecture](CS4218_Architecture.png)

Figure 1: relationship between the shell, the kernel, and various applications

Shell can be thought of as a programming language for running applications. From the user’s perspective, it will performs the following loop continuously as below:
1. Print prompt message.
2. Wait for user’s command.
3. Parse and interpret user’s command, run specified applications if any.
4. Print output.
5. Go to 2.

The goal of the project is to implement and test a shell and a set of applications. The shell and the applications must be implemented in JAVA programming language. The required functionality is a subset (or simplification) of the functionality provided by UNIX-like systems. Particularly, the specification was designed in such a way that it maximally resembles the behaviour of Bash shell in GNU/Linux and Mac OS. However, there are several important distinctions:
1. JVM is used instead of OS Kernel / drivers to provide required services.
2. Shell and all applications are run inside the same process.
3. Applications raise exceptions instead of writing to stderr and returning non-zero exit code in case of errors, as shown in Figure 4.

## Technologies used
- Java 8
- IntellJ Idea
- JUnit 5
- Mockito
- Maven

## Instructions to setup project
Step 1: Execute IntelliJIdea on your local machine.

Step 2: You can either clone this project into your local machine or use Get from version control **(recommended)**.

Step 3: Select Import Project from external Model option.

Step 4: Select Maven and Click finish.

## Instructions to run the Shell program
**Option 1: Run using IntellJIdea** (Recommended)

Step 1: Find `ShellImpl.java`

Step 2: Execute the main method

**Option 2: Run using CLI**

For Windows Users: Run `runShell.bat` inside CLI (This will not work if you do not have maven installed in Windows as maven is not installed by default in Windows)

For other users: Run `./runShell.sh` inside CLI

If there is any permission issues running the above files, you will need to provide execute permissions from the files above and run CLI with administrator privileges.

## Instructions to run all our test cases

**Option 1: Run All Tests in Java** (Recommended especially for Windows users where maven is usually not preinstalled as even using build in maven test run in IntelliJ test may cause errors in Windows. Note that it also runs excluded tests)

Step 1: In IntellJIdea, navigate to `src` > `test` > `java` folder

Step 2: Right click on the `java` folder and click `Run All Tests` to run all tests.

**Note:** `Run All Tests` runs all test including those that tests that are expected to not work (excluded in maven) due to unimplemented ef1 as not part of our milestone 1 or bugs in ef1. As it does not take into account maven's exclude configuration in `pom.xml` as it is not part of maven.

**Tests file to manually ignore the test cases that do not work if using option 1**

Refer to `pom.xml`'s `<exclude>` tag in `build` tag > `plugins` tag under `maven-sunfire-plugin` and `maven-failsafe-plugin` configurations for a list of test cases to ignore if not working due to unimplemented ef1 or ef1 bugs in milestone 1.

An example `build` tag content in `pom.xml` of a list of excluded `**Test.java` and `**IT.java`:

```xml
<build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.22.0</version>
                <configuration>
                    <excludes>
<!--                    The configuration below are excluding extended functionality 1 unit test from maven build-->
                        <exclude>**/CpApplicationTest.java</exclude>
                        <exclude>**/CdApplicationTest.java</exclude>
                        <exclude>**/DiffApplicationTest.java</exclude>
                        <exclude>**/GrepApplicationTest.java</exclude>
                        <exclude>**/WcApplicationTest.java</exclude>
                        <exclude>**/WcArgumentsTest.java</exclude>
                        <exclude>**/ArgumentResolverTest.java</exclude>
                    </excludes>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-failsafe-plugin</artifactId>
                <version>2.22.0</version>
                <configuration>
                    <excludes>
<!--                    The configuration below are excluding extended functionality 1 integration test from maven build-->
                        <exclude>**/WcApplicationIT.java</exclude>
                        <exclude>**/DiffApplicationIT.java</exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
```

**Option 2: Run using mvn test** (Requires maven to be installed in computer)

You will just need to run `mvn test` (Not recommended for Windows as Windows usually do not have mvn preinstalled).

## External libraries / plugins / tools / technologies used
(The below has been approved by Prof or is as stated in Project Specification) 
- Maven: For Build Automation
- Travis: For Continuous Integration
- Maven Surefire Plugin: Required by Maven in order to run unit test file (`**Test.java`) within Java Maven Project.
- Maven Failsafe Plugin: Required by Maven in order to run integration test file (`**IT.java`) within Java Maven Project.
- JUnit 5 for Java testing purposes
- Mockito for mocking and stubbing

## Acknowledges / References
[TBC]