package io.openapitools.swagger.custom;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.swagger.v3.core.util.ReflectionUtils;

public class AnnotationsUtils {

  public static <T> List<T> getAnnotation(Class<T> cls,  Annotation... annotations) {
    return getAnnotation(cls, true, annotations);
  }
  public static <T> List<T> getAnnotation(Class<T> cls, boolean nextLevel, Annotation... annotations) {
    if (annotations == null) {
      return null;
    } else {
      List<T> res = new ArrayList<>();
      Annotation[] var2 = annotations;
      int var3 = annotations.length;

      for(int var4 = 0; var4 < var3; ++var4) {
        Annotation annotation = var2[var4];
        if (cls.isAssignableFrom(annotation.getClass())) {
          res.add((T)annotation);
        }
        else if(nextLevel){
          Annotation[] annotationAnnotations = annotation.annotationType().getAnnotations();
          if(ArrayUtils.isNotEmpty(annotationAnnotations)){
            res.addAll(getAnnotation(cls, false, annotationAnnotations));
          }
        }
      }
      return res;
    }
  }


  public static <A extends Annotation> List<A> getRepeatableAnnotations(Method method, Class<A> annotationClass) {
    List<A> annotations = new ArrayList<>(Arrays.asList(method.getAnnotationsByType(annotationClass)));
      Annotation[] var3 = method.getAnnotations();
      int var4 = var3.length;

      for (int var5 = 0; var5 < var4; ++var5) {
        Annotation metaAnnotation = var3[var5];
        annotations.addAll(Arrays.asList(metaAnnotation.annotationType().getAnnotationsByType(annotationClass)));
      }

      if(annotations.isEmpty()){
        Method superclassMethod = ReflectionUtils.getOverriddenMethod(method);
        if (superclassMethod!=null) {
          return getRepeatableAnnotations(superclassMethod, annotationClass);
        }
      }


    return annotations;
  }

}
