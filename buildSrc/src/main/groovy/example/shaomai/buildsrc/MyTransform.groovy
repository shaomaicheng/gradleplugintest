package example.shaomai.buildsrc

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.gradle.api.Project

class MyTransform extends Transform {
    private Project project;

    public MyTransform(Project project) {
        this.project = project;
    }

    @Override
    public String getName() {
        return "MyTransform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        this.transform(transformInvocation.getContext(), transformInvocation.getInputs(),
                transformInvocation.getReferencedInputs(),
                transformInvocation.getOutputProvider(),
                transformInvocation.isIncremental());
    }

    @Override
    public void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        System.out.println("======进入transform======")
        def injectPools = new InjectPools()
        injectPools.appendAllClasses(inputs, project)
        inputs.each {
            it.directoryInputs.each {
                injectPools.inject(it.getFile().absolutePath)
                File dest = outputProvider.getContentLocation(
                        it.getName(), it.getContentTypes(), it.getScopes(), Format.DIRECTORY
                )
                println 'file->'+dest.absolutePath
                FileUtils.copyDirectory(it.getFile(), dest)
            }
            it.jarInputs.each {
                File dest = outputProvider.getContentLocation(
                        it.getFile().getAbsolutePath(), it.getContentTypes(), it.getScopes(), Format.JAR
                )
                println 'jar->'+dest.absolutePath
                FileUtils.copyFile(it.getFile(), dest)
            }
        }
        injectPools = null
        System.out.println("======结束transform======");
    }
}
