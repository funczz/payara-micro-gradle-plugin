# payara-micro-gradle-plugin

## Using Gradle

In your build.gradle.kts.

```kotlin
buildscript {
    repositories {
        maven {
            setUrl("https://funczz.github.io/payara-micro-gradle-plugin")
        }
    }
    dependencies {
        classpath("com.github.funczz:payara-micro-gradle-plugin:<VERSION>")
    }
}
apply(plugin = "payara-micro-gradle-plugin")
dependencies {
    testRuntimeOnly("fish.payara.extras:payara-micro:<VERSION>")
}
```

### options
```kotlin
payaraMicro {
    // java コマンドのフルパス
    // javaBin を指定した場合は優先して使用される
    // javaBin の指定がない場合は $JAVA_HOME/bin/java が使用される
    // $JAVA_HOME が未定義の場合は "java" が使用される
    //javaBin = "/PATH/TO/java"
    javaBin = ""

    // JRE バージョン情報取得までの最大待機時間(ミリ秒)
    javaVersionTimeout = 60_000L
    
    // payara micro 起動オプションを配列で指定
    //options = listOf("--nocluster", "--port", "8081")
    options = listOf()

    // war タスク完了までの最大待機時間(ミリ秒)
    archiveTimeout = 60_000L

    // uberJar 生成完了までの最大待機時間(ミリ秒)
    uberJarTimeout = 60_000L

    // payara micro バージョン情報取得までの最大待機時間(ミリ秒)
    payaraMicroVersionTimeout = 60_000L

    // payara micro プロセス開始前の待機時間(ミリ秒)
    processInitialDelay = 3_000L

    // payara micro プロセス状態確認の待機時間(ミリ秒)
    processPeriod = 1_000L

    // payara micro プロセスの最大待機時間(ミリ秒)
    // 0 以下で無制限
    processTimeout = 0L

    // payara micro プロセスの文字セット
    processCharset = Charset.defaultCharset()
}
```

## How to use

### uberJar ファイルを生成
```console
./gradlew payaraUberJar
```

### Payara Micro を開始
```console
./gradlew payaraStartWar
```

### Payara Micro に再デプロイ
```console
./gradlew payaraRedeployWar
```

### Payara Micro を停止
```console
./gradlew payaraStopWar
```

### Payara Micro バージョンを取得
```console
./gradlew payaraVersion
```

### JRE バージョンを取得
```console
./gradlew javaVersion
```

## Demo

### payaraUberJar
```console
cd demo
./gradlew payaraUberJar
java -jar build/libs/payara-micro-gradle-plugin-demo.jar
```

```console
curl http://localhost:8080
#=> hello world.
```

### payaraStartWar
```console
cd demo
./gradlew payaraStartWar
```

```console
curl http://localhost:8080
#=> hello world.

cd demo
./gradlew payaraStopWar
```
