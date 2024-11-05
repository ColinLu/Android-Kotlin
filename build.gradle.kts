// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.dagger.android) apply false
}


//源码加入以及替换
//allprojects {
//    gradle.projectsEvaluated {
//        tasks.withType<JavaCompile> {
//            val path = "xxx/framework.jar"
//            println(path)
//            val fileSet = options.bootstrapClasspath?.files?: setOf<File>()
//            val newFileList = mutableListOf<File>()
//            // 相对位置，根据存放的位置修改路径
//            newFileList.add(File(path))
//            newFileList.addAll(fileSet)
//            options.bootstrapClasspath = files(newFileList.toTypedArray()).also {
//                println(it.joinToString("\n"))
//            }
//        }
//    }
//}