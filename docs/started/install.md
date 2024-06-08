# 安装
[![Maven Central](https://img.shields.io/maven-central/v/cn.erolc.mrouter/core?label=MavenCentral&logo=apache-maven)](https://search.maven.org/artifact/cn.erolc.mrouter/core)


## 添加仓库
=== "build.gradle.kts"

    ```kotlin
    repositories {
        mavenCentral()
        //code...
    }
    ```

=== "build.gradle"

    ```groovy
    repositories {
    mavenCentral()
    //code...
    }
    ```
=== "setting.gradle.kts"

    ```kotlin
    dependencyResolutionManagement {
        repositories {
            //code...
            mavenCentral()
        }
    }
    ```

## 添加依赖
库除了可以在compose-multiplatform的项目上使用之外，还可以单独在android上使用。

=== "compose-multiplatform"

    ```kotlin
    commonMain.dependencies {
        implementation("cn.erolc.mrouter:core:<version>")
    }
    ```

=== "android"

    ```kotlin
    dependencies{
        implementation("cn.erolc.mrouter:core:<version>")
    }
    ```
