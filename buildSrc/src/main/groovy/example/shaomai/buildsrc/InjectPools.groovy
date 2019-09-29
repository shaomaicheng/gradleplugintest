package example.shaomai.buildsrc

import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project

class InjectPools {
    //初始化类池
    private final static ClassPool pool = ClassPool.getDefault();

    public static void inject(String path, Project project) {
        pool.appendClassPath(path)
        pool.appendClassPath(project.android.bootClasspath[0].toString())
        pool.importPackage("android.os.Bundle");

        File file = new File(path)

        if (file.isDirectory()){
            file.eachFileRecurse {File f ->
                if (f.getName().equals("MainActivity.class")) {
                    println f.absolutePath

                    CtClass ctClass = pool.getCtClass("example.shaomai.wuhengtest.Add");
                    if (ctClass.isFrozen()) {
                        ctClass.defrost()
                    }
                    CtMethod ctMethod = ctClass.getDeclaredMethod("add")
                    String insetBeforeStr = """ System.out.println("我是被插入的代码");
                                            """
                    ctMethod.insertBefore(insetBeforeStr)
                    ctClass.writeFile(path)
                    ctClass.detach()
//                    CtClass ctClass = pool.getCtClass("example.shaomai.wuhengtest.MainActviity")
//                    if (ctClass.isFrozen()) {
//                        ctClass.defrost()
//                    }
//                    CtMethod ctMethod = ctClass.getDeclaredMethod("onCreate")
//                    String insetBeforeStr = """ android.widget.Toast.makeText(this,"WTF emmmmmmm.....我是被插入的Toast代码~!!",android.widget.Toast.LENGTH_LONG).show();
//                                            """
//                    ctMethod.insertBefore(insetBeforeStr)
//                    ctClass.writeFile(path)
//                    ctClass.detach()
                }
            }
        }
    }
}
