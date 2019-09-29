package example.shaomai.buildsrc

import com.android.build.api.transform.TransformInput
import javassist.ClassPool
import javassist.CtClass
import org.gradle.api.Project

class InjectPools {
    //初始化类池
    private ClassPool pool = ClassPool.getDefault()

    void appendAllClasses(Collection<TransformInput> inputs, Project project) {
        pool.appendClassPath("androidx.appcompat.app.AppCompatActivity")
        pool.appendClassPath('android.os.Bundle')
        project.android.bootClasspath.each {
            println '添加路径：' + it.absolutePath
            pool.appendClassPath((String) it.absolutePath)
        }
        inputs.each {
            it.directoryInputs.each {
                def dirPath = it.file.absolutePath
                pool.appendClassPath(dirPath)
            }
            it.jarInputs.each {
                pool.appendClassPath(it.file.absolutePath)
            }
        }
    }

    void inject(String path) {

        File file = new File(path)


        if (file.isDirectory()) {
            file.eachFileRecurse { File f ->
                if (f.absolutePath.endsWith(".class")) {
//                    println f.absolutePath
                    def stufix = ".class".length()
                    def paths = f.absolutePath.substring(0, f.absolutePath.length() - stufix).split("/")
                    def index = 0
                    if (paths.contains("classes")) {
                        index = paths.findIndexOf {
                            (it == "classes")
                        }
                    } else {
                        index = paths.findIndexOf {
                            it == 'kotlin-classes'
                        }
                        index++
                    }
                    def className = ''
                    for (int i = index + 1; i < paths.size(); i++) {
                        if (i != paths.size() - 1) {
                            className += paths[i] + "."
                        } else {
                            className += paths[i]
                        }
                    }

//                    println className

                    if (className.startsWith("example.shaomai")) {
                        CtClass ctClass = pool.getCtClass(className)
                        if (ctClass.isFrozen()) {
                            ctClass.defrost()
                        }
                        ctClass.declaredMethods.each {
                            def ctMethod = it
//                        println ctMethod.name
                            if (ctMethod != null) {
                                def classSimpleName = className
                                def methodName = ctMethod.name
                                def completeClassMethodName = classSimpleName + '#' + methodName
                                println completeClassMethodName

                                ctMethod.addLocalVariable("begin", CtClass.longType)
                                String insetBeforeStr = """ 
                                    begin = System.currentTimeMillis();
                        android.util.Log.e("${completeClassMethodName}开始时间", String.valueOf(begin));
                                            """

                                ctMethod.insertBefore(insetBeforeStr)


                                String insertAfterStr = """
                            long end = System.currentTimeMillis();
                            android.util.Log.e("${completeClassMethodName}结束时间", String.valueOf(end));
                           android.util.Log.e("${completeClassMethodName}耗时", String.valueOf(end-begin));
                            """

                                ctMethod.insertAfter(insertAfterStr)
                                ctClass.writeFile(path)
                                if (ctClass.isFrozen()) {
                                    ctClass.defrost()
                                }
                            }
                        }
                        ctClass.detach()
                    }

                }
            }
        }

    }
}
