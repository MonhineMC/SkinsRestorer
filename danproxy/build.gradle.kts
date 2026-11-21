plugins {
    id("sr.shadow-logic")
}

base {
    archivesName = "SkinsRestorer-danproxy"
}

val generateDanProxyVersion = tasks.register("generateDanProxyVersion") {
    val output = layout.buildDirectory.dir("generated/sources/danproxy")
    val version = project.version.toString()
    inputs.property("version", version)
    outputs.dir(output)
    doLast {
        val file = output.get().file("net/skinsrestorer/danproxy/SRDanProxyVersion.java").asFile
        file.parentFile.mkdirs()
        file.writeText(
            "package net.skinsrestorer.danproxy;\n\n" +
                    "final class SRDanProxyVersion {\n\n" +
                    "    static final String VERSION = \"$version\";\n\n" +
                    "    private SRDanProxyVersion() {\n" +
                    "    }\n" +
                    "}\n"
        )
    }
}

sourceSets {
    main {
        java.srcDir(generateDanProxyVersion)
    }
}

repositories {
    mavenLocal {
        content {
            includeGroup("me.dwyur")
        }
    }
}

val danproxyVersion = providers.gradleProperty("danproxyVersion").getOrElse("0.3.0")

dependencies {
    compileOnly(projects.skinsrestorerShared)
    runtimeOnly(project(":skinsrestorer-shared", "shadow"))

    compileOnly("me.dwyur:danproxy-api:$danproxyVersion")
}
