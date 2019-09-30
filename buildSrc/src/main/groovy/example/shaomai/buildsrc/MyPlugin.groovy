package example.shaomai.buildsrc

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public class MyPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        System.out.println("===========注册transform==========");
        AppExtension android = project.getExtensions().getByType(AppExtension.class);
        android.registerTransform(new MyTransform(project))
        android.registerTransform(new AsmTransform(project))
        System.out.println("===========结束注册==========");
    }

}