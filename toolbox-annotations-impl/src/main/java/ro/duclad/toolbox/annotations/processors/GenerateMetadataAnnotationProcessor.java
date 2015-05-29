package ro.duclad.toolbox.annotations.processors;


import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Created by duclad on 5/25/15.
 */
@SupportedAnnotationTypes("ro.duclad.toolbox.annotations.GenerateMetadata")
public class GenerateMetadataAnnotationProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            for (TypeElement typeElement : annotations) {
                for (Element annotatedClass : roundEnv.getElementsAnnotatedWith(typeElement)) {
                    TypeElement clazz = (TypeElement) annotatedClass;
                    String classQualifiedName = clazz.getQualifiedName().toString();
                    String packageName = "";
                    if (classQualifiedName.lastIndexOf(".") > 0) {
                        packageName = classQualifiedName.substring(0, classQualifiedName.lastIndexOf("."));
                    }
                    String metadataClassName = clazz.getSimpleName() + "Metadata";
                    JavaFileObject javaFile = processingEnv.getFiler().createSourceFile(metadataClassName);
                    BufferedWriter javaFileWriter = new BufferedWriter(javaFile.openWriter());
                    if (packageName.trim().length() > 0) {
                        javaFileWriter.append("package " + packageName + ";");
                        javaFileWriter.newLine();
                        javaFileWriter.newLine();
                    }
                    javaFileWriter.append("public final class " + metadataClassName + " {");
                    javaFileWriter.newLine();
                    for (Element field : annotatedClass.getEnclosedElements()) {
                        if (field.getKind() == ElementKind.FIELD) {
                            javaFileWriter.append("\t").append("public static final String " + field.getSimpleName().toString() + " = \"" + field.getSimpleName().toString() + "\";");
                            javaFileWriter.newLine();
                        }
                    }
                    javaFileWriter.append("}");
                    javaFileWriter.flush();
                    javaFileWriter.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
