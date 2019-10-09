package example.shaomai.buildsrc

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

class AsmTransform extends  Transform {

    private Project project

    AsmTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return 'asmtransform'
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        println '==========进去第二个transform=========='
        inputs.each {
            it.directoryInputs.each {
                println "第二个transform里NeacyRouterWriter面的class文件路径: ${it.file.absolutePath}"  // 应该是 build文件夹下面 MyTransform 的输出目录

                it.file.eachFileRecurse {file ->
                    def  name = file.name
                    if (name.endsWith('.class')) {
                        ClassReader classReader = new ClassReader(file.bytes)
                        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                        ClassVisitor cv = new AddTryCatchClassAdapter(classWriter)
                        classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                        byte[] code = classWriter.toByteArray()
                        FileOutputStream fos = new FileOutputStream(
                                file.parentFile.absolutePath + File.separator + name
                        )
                        fos.write(code)
                        fos.close()
                    }
                }

                def dist = outputProvider.getContentLocation(it.name, it.contentTypes, it.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(it.getFile(), dist)

            }
            it.jarInputs.each {
                println "第二个transform里面的jar文件路径：${it.file.absolutePath}"
                def dist = outputProvider.getContentLocation(it.name, it.contentTypes, it.scopes, Format.JAR)
                FileUtils.copyFile(it.getFile(), dist)
            }
        }
        println '==========结束第二个transform=========='
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        this.transform(
                transformInvocation.context,
                transformInvocation.inputs,
                transformInvocation.referencedInputs,
                transformInvocation.outputProvider,
                transformInvocation.incremental
        )
    }
}