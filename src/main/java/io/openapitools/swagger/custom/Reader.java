//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.openapitools.swagger.custom;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.ParameterProcessor;
import io.swagger.v3.core.util.PathUtils;
import io.swagger.v3.core.util.ReflectionUtils;
import io.swagger.v3.jaxrs2.OperationParser;
import io.swagger.v3.jaxrs2.ReaderListener;
import io.swagger.v3.jaxrs2.ResolvedParameter;
import io.swagger.v3.jaxrs2.SecurityParser;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtension;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtensions;
import io.swagger.v3.jaxrs2.util.ReaderUtils;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.callbacks.Callback;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.integration.ContextUtils;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenAPIConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiReader;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Encoding;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.Encoding.StyleEnum;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.tags.Tag;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reader implements OpenApiReader {
  private static final Logger LOGGER = LoggerFactory.getLogger(Reader.class);
  public static final String DEFAULT_MEDIA_TYPE_VALUE = "*/*";
  public static final String DEFAULT_DESCRIPTION = "default response";
  protected OpenAPIConfiguration config;
  private Application application;
  private OpenAPI openAPI;
  private Components components;
  private Paths paths;
  private Set<Tag> openApiTags;
  private static final String GET_METHOD = "get";
  private static final String POST_METHOD = "post";
  private static final String PUT_METHOD = "put";
  private static final String DELETE_METHOD = "delete";
  private static final String PATCH_METHOD = "patch";
  private static final String TRACE_METHOD = "trace";
  private static final String HEAD_METHOD = "head";
  private static final String OPTIONS_METHOD = "options";

  public Reader() {
    this.openAPI = new OpenAPI();
    this.paths = new Paths();
    this.openApiTags = new LinkedHashSet();
    this.components = new Components();
  }

  public Reader(OpenAPI openAPI) {
    this();
    this.setConfiguration((new SwaggerConfiguration()).openAPI(openAPI));
  }

  public Reader(OpenAPIConfiguration openApiConfiguration) {
    this();
    this.setConfiguration(openApiConfiguration);
  }

  public OpenAPI getOpenAPI() {
    return this.openAPI;
  }

  public OpenAPI read(Class<?> cls) {
    return this.read(cls, this.resolveApplicationPath(), (String)null, false, (RequestBody)null, (ApiResponses)null, new LinkedHashSet(), new ArrayList(), new HashSet());
  }

  public OpenAPI read(Set<Class<?>> classes) {
    Set<Class<?>> sortedClasses = new TreeSet<Class<?>>(( Class<?> class1, Class<?> class2) -> {
      if (class1.equals(class2)) {
        return 0;
      } else if (class1.isAssignableFrom(class2)) {
        return -1;
      } else {
        return class2.isAssignableFrom(class1) ? 1 : class1.getName().compareTo(class2.getName());
      }
    });
    sortedClasses.addAll(classes);
    Map<Class<?>, ReaderListener> listeners = new HashMap();
    Iterator var4 = sortedClasses.iterator();

    Class cls;
    while(var4.hasNext()) {
      cls = (Class)var4.next();
      if (ReaderListener.class.isAssignableFrom(cls) && !listeners.containsKey(cls)) {
        try {
          listeners.put(cls, (ReaderListener)cls.newInstance());
        } catch (Exception var9) {
          LOGGER.error("Failed to create ReaderListener", var9);
        }
      }
    }

    var4 = listeners.values().iterator();

    ReaderListener listener;
    while(var4.hasNext()) {
      listener = (ReaderListener)var4.next();

      try {
        listener.beforeScan(this, this.openAPI);
      } catch (Exception var8) {
        LOGGER.error("Unexpected error invoking beforeScan listener [" + listener.getClass().getName() + "]", var8);
      }
    }

    var4 = sortedClasses.iterator();

    while(var4.hasNext()) {
      cls = (Class)var4.next();
      this.read(cls, this.resolveApplicationPath(), (String)null, false, (RequestBody)null, (ApiResponses)null, new LinkedHashSet(), new ArrayList(), new HashSet());
    }

    var4 = listeners.values().iterator();

    while(var4.hasNext()) {
      listener = (ReaderListener)var4.next();

      try {
        listener.afterScan(this, this.openAPI);
      } catch (Exception var7) {
        LOGGER.error("Unexpected error invoking afterScan listener [" + listener.getClass().getName() + "]", var7);
      }
    }

    return this.openAPI;
  }

  public void setConfiguration(OpenAPIConfiguration openApiConfiguration) {
    if (openApiConfiguration != null) {
      this.config = ContextUtils.deepCopy(openApiConfiguration);
      if (openApiConfiguration.getOpenAPI() != null) {
        this.openAPI = this.config.getOpenAPI();
        if (this.openAPI.getComponents() != null) {
          this.components = this.openAPI.getComponents();
        }
      }
    }

  }

  public OpenAPI read(Set<Class<?>> classes, Map<String, Object> resources) {
    return this.read(classes);
  }

  protected String resolveApplicationPath() {
    if (this.application != null) {
      ApplicationPath applicationPath;
      for(Class<?> applicationToScan = this.application.getClass(); (applicationPath = (ApplicationPath)applicationToScan.getAnnotation(ApplicationPath.class)) == null && !applicationToScan.getSuperclass().equals(Application.class); applicationToScan = applicationToScan.getSuperclass()) {
      }

      if (applicationPath != null && StringUtils.isNotBlank(applicationPath.value())) {
        return applicationPath.value();
      }

      try {
        Application innerApp = this.application;

        Application retrievedApp;
        for(Method m = this.application.getClass().getMethod("getApplication"); m != null; m = retrievedApp.getClass().getMethod("getApplication")) {
          retrievedApp = (Application)m.invoke(innerApp);
          if (retrievedApp == null || retrievedApp.getClass().equals(innerApp.getClass())) {
            break;
          }

          innerApp = retrievedApp;
          applicationPath = (ApplicationPath)retrievedApp.getClass().getAnnotation(ApplicationPath.class);
          if (applicationPath != null && StringUtils.isNotBlank(applicationPath.value())) {
            return applicationPath.value();
          }
        }
      } catch (NoSuchMethodException var6) {
      } catch (Exception var7) {
      }
    }

    return "";
  }

  public OpenAPI read(Class<?> cls, String parentPath, String parentMethod, boolean isSubresource, RequestBody parentRequestBody, ApiResponses parentResponses, Set<String> parentTags, List<Parameter> parentParameters, Set<Class<?>> scannedResources) {
    Hidden hidden = (Hidden)cls.getAnnotation(Hidden.class);
    Path apiPath = (Path)ReflectionUtils.getAnnotation(cls, Path.class);
    if (hidden != null) {
      return this.openAPI;
    } else {
      ApiResponse[] classResponses = (ApiResponse[])ReflectionUtils.getRepeatableAnnotationsArray(cls, ApiResponse.class);
      List<SecurityScheme> apiSecurityScheme = ReflectionUtils.getRepeatableAnnotations(cls, SecurityScheme.class);
      List<SecurityRequirement> apiSecurityRequirements = ReflectionUtils.getRepeatableAnnotations(cls, SecurityRequirement.class);
      ExternalDocumentation apiExternalDocs = (ExternalDocumentation)ReflectionUtils.getAnnotation(cls, ExternalDocumentation.class);
      io.swagger.v3.oas.annotations.tags.Tag[] apiTags = (io.swagger.v3.oas.annotations.tags.Tag[])ReflectionUtils.getRepeatableAnnotationsArray(cls, io.swagger.v3.oas.annotations.tags.Tag.class);
      Server[] apiServers = (Server[])ReflectionUtils.getRepeatableAnnotationsArray(cls, Server.class);
      Consumes classConsumes = (Consumes)ReflectionUtils.getAnnotation(cls, Consumes.class);
      Produces classProduces = (Produces)ReflectionUtils.getAnnotation(cls, Produces.class);
      boolean classDeprecated = ReflectionUtils.getAnnotation(cls, Deprecated.class) != null;
      OpenAPIDefinition openAPIDefinition = (OpenAPIDefinition)ReflectionUtils.getAnnotation(cls, OpenAPIDefinition.class);
      if (openAPIDefinition != null) {
        AnnotationsUtils.getInfo(openAPIDefinition.info()).ifPresent((info) -> {
          this.openAPI.setInfo(info);
        });
        SecurityParser.getSecurityRequirements(openAPIDefinition.security()).ifPresent((s) -> {
          this.openAPI.setSecurity(s);
        });
        AnnotationsUtils.getExternalDocumentation(openAPIDefinition.externalDocs()).ifPresent((docs) -> {
          this.openAPI.setExternalDocs(docs);
        });
        AnnotationsUtils.getTags(openAPIDefinition.tags(), false).ifPresent((tags) -> {
          this.openApiTags.addAll(tags);
        });
        AnnotationsUtils.getServers(openAPIDefinition.servers()).ifPresent((servers) -> {
          this.openAPI.setServers(servers);
        });
        if (openAPIDefinition.extensions().length > 0) {
          this.openAPI.setExtensions(AnnotationsUtils.getExtensions(openAPIDefinition.extensions()));
        }
      }

      if (apiSecurityScheme != null) {
        Iterator var22 = apiSecurityScheme.iterator();

        label352:
        while(true) {
          while(true) {
            Optional securityScheme;
            HashMap securitySchemeMap;
            do {
              do {
                if (!var22.hasNext()) {
                  break label352;
                }

                SecurityScheme securitySchemeAnnotation = (SecurityScheme)var22.next();
                securityScheme = SecurityParser.getSecurityScheme(securitySchemeAnnotation);
              } while(!securityScheme.isPresent());

              securitySchemeMap = new HashMap();
            } while(!StringUtils.isNotBlank(((SecurityParser.SecuritySchemePair)securityScheme.get()).key));

            securitySchemeMap.put(((SecurityParser.SecuritySchemePair)securityScheme.get()).key, ((SecurityParser.SecuritySchemePair)securityScheme.get()).securityScheme);
            if (this.components.getSecuritySchemes() != null && this.components.getSecuritySchemes().size() != 0) {
              this.components.getSecuritySchemes().putAll(securitySchemeMap);
            } else {
              this.components.setSecuritySchemes(securitySchemeMap);
            }
          }
        }
      }

      List<io.swagger.v3.oas.models.security.SecurityRequirement> classSecurityRequirements = new ArrayList();
      if (apiSecurityRequirements != null) {
        Optional<List<io.swagger.v3.oas.models.security.SecurityRequirement>> requirementsObject = SecurityParser.getSecurityRequirements((SecurityRequirement[])apiSecurityRequirements.toArray(new SecurityRequirement[apiSecurityRequirements.size()]));
        if (requirementsObject.isPresent()) {
          classSecurityRequirements = (List)requirementsObject.get();
        }
      }

      Set<String> classTags = new LinkedHashSet();
      if (apiTags != null) {
        AnnotationsUtils.getTags(apiTags, false).ifPresent((tags) -> {
          tags.stream().map(Tag::getName).forEach(classTags::add);
        });
      }

      if (isSubresource && parentTags != null) {
        classTags.addAll(parentTags);
      }

      List<io.swagger.v3.oas.models.servers.Server> classServers = new ArrayList();
      if (apiServers != null) {
        AnnotationsUtils.getServers(apiServers).ifPresent(classServers::addAll);
      }

      Optional<io.swagger.v3.oas.models.ExternalDocumentation> classExternalDocumentation = AnnotationsUtils.getExternalDocumentation(apiExternalDocs);
      JavaType classType = TypeFactory.defaultInstance().constructType(cls);
      BeanDescription bd = Json.mapper().getSerializationConfig().introspect(classType);
      List<Parameter> globalParameters = new ArrayList();
      globalParameters.addAll(ReaderUtils.collectConstructorParameters(cls, this.components, classConsumes, (JsonView)null));
      globalParameters.addAll(ReaderUtils.collectFieldParameters(cls, this.components, classConsumes, (JsonView)null));
      List<Method> methods = (List)Arrays.stream(cls.getMethods()).sorted(new MethodComparator()).collect(Collectors.toList());
      Iterator var30 = methods.iterator();

      while(true) {
        while(true) {
          Method method;
          AnnotatedMethod annotatedMethod;
          Consumes methodConsumes;
          boolean methodDeprecated;
          String operationPath;
          Class subResource;
          String httpMethod;
          JsonView jsonViewAnnotation;
          JsonView jsonViewAnnotationForRequestBody;
          Operation operation;
          do {
            Produces methodProduces;
            Object returnType;
            do {
              do {
                do {
                  do {
                    do {
                      do {
                        do {
                          if (!var30.hasNext()) {
                            if (!this.isEmptyComponents(this.components) && this.openAPI.getComponents() == null) {
                              this.openAPI.setComponents(this.components);
                            }

                            AnnotationsUtils.getTags(apiTags, true).ifPresent((tags) -> {
                              this.openApiTags.addAll(tags);
                            });
                            if (!this.openApiTags.isEmpty()) {
                              Set<Tag> tagsSet = new LinkedHashSet();
                              Iterator var60;
                              Tag tag;
                              if (this.openAPI.getTags() != null) {
                                var60 = this.openAPI.getTags().iterator();

                                while(var60.hasNext()) {
                                  tag = (Tag)var60.next();
                                  Tag finalTag = tag;
                                  if (tagsSet.stream().noneMatch((t) -> {
                                    return t.getName().equals(finalTag.getName());
                                  })) {
                                    tagsSet.add(tag);
                                  }
                                }
                              }

                              var60 = this.openApiTags.iterator();

                              while(var60.hasNext()) {
                                tag = (Tag)var60.next();
                                Tag finalTag1 = tag;
                                if (tagsSet.stream().noneMatch((t) -> {
                                  return t.getName().equals(finalTag1.getName());
                                })) {
                                  tagsSet.add(tag);
                                }
                              }

                              this.openAPI.setTags(new ArrayList(tagsSet));
                            }

                            return this.openAPI;
                          }

                          method = (Method)var30.next();
                        } while(this.isOperationHidden(method));

                        annotatedMethod = bd.findMethod(method.getName(), method.getParameterTypes());
                        methodProduces = (Produces)ReflectionUtils.getAnnotation(method, Produces.class);
                        methodConsumes = (Consumes)ReflectionUtils.getAnnotation(method, Consumes.class);
                      } while(this.isMethodOverridden(method, cls));

                      methodDeprecated = ReflectionUtils.getAnnotation(method, Deprecated.class) != null;
                      Path methodPath = (Path)ReflectionUtils.getAnnotation(method, Path.class);
                      operationPath = ReaderUtils.getPath(apiPath, methodPath, parentPath, isSubresource);
                    } while(this.ignoreOperationPath(operationPath, parentPath) && !isSubresource);

                    Map<String, String> regexMap = new LinkedHashMap();
                    operationPath = PathUtils.parsePath(operationPath, regexMap);
                  } while(operationPath == null);
                } while(this.config != null && ReaderUtils.isIgnored(operationPath, this.config));

                subResource = this.getSubResourceWithJaxRsSubresourceLocatorSpecs(method);
                httpMethod = ReaderUtils.extractOperationMethod(method, OpenAPIExtensions.chain());
                httpMethod = httpMethod == null && isSubresource ? parentMethod : httpMethod;
              } while(StringUtils.isBlank(httpMethod) && subResource == null);

              if (!StringUtils.isBlank(httpMethod) || subResource == null) {
                break;
              }

              returnType = method.getGenericReturnType();
              if (annotatedMethod != null && annotatedMethod.getType() != null) {
                returnType = annotatedMethod.getType();
              }
            } while(this.shouldIgnoreClass(((Type)returnType).getTypeName()) && !method.getGenericReturnType().equals(subResource));

            io.swagger.v3.oas.annotations.Operation apiOperation = (io.swagger.v3.oas.annotations.Operation)ReflectionUtils.getAnnotation(method, io.swagger.v3.oas.annotations.Operation.class);
            if (apiOperation != null && apiOperation.ignoreJsonView()) {
              jsonViewAnnotation = null;
              jsonViewAnnotationForRequestBody = null;
            } else {
              jsonViewAnnotation = (JsonView)ReflectionUtils.getAnnotation(method, JsonView.class);
              jsonViewAnnotationForRequestBody = (JsonView)Arrays.stream(ReflectionUtils.getParameterAnnotations(method)).filter((arr) -> {
                return Arrays.stream(arr).anyMatch((annotation) -> {
                  return annotation.annotationType().equals(io.swagger.v3.oas.annotations.parameters.RequestBody.class);
                });
              }).flatMap(Arrays::stream).filter((annotation) -> {
                return annotation.annotationType().equals(JsonView.class);
              }).reduce((a, b) -> {
                return null;
              }).orElse(jsonViewAnnotation);
            }

            operation = this.parseMethod(method, globalParameters, methodProduces, classProduces, methodConsumes, classConsumes, (List)classSecurityRequirements, classExternalDocumentation, classTags, classServers, isSubresource, parentRequestBody, parentResponses, jsonViewAnnotation, classResponses, annotatedMethod);
          } while(operation == null);

          if (classDeprecated || methodDeprecated) {
            operation.setDeprecated(true);
          }

          List<Parameter> operationParameters = new ArrayList();
          List<Parameter> formParameters = new ArrayList();
          Annotation[][] paramAnnotations = ReflectionUtils.getParameterAnnotations(method);
          JavaType type;
          io.swagger.v3.oas.annotations.Parameter paramAnnotation;
          Object paramType;
          ResolvedParameter resolvedParameter;
          if (annotatedMethod == null) {
            Type[] genericParameterTypes = method.getGenericParameterTypes();

            for(int i = 0; i < genericParameterTypes.length; ++i) {
              type = TypeFactory.defaultInstance().constructType(genericParameterTypes[i], cls);
              paramAnnotation = (io.swagger.v3.oas.annotations.Parameter)AnnotationsUtils.getAnnotation(io.swagger.v3.oas.annotations.Parameter.class, paramAnnotations[i]);
              paramType = ParameterProcessor.getParameterType(paramAnnotation, true);
              if (paramType == null) {
                paramType = type;
              } else if (!(paramType instanceof Class)) {
                paramType = type;
              }

              resolvedParameter = this.getParameters((Type)paramType, Arrays.asList(paramAnnotations[i]), operation, classConsumes, methodConsumes, jsonViewAnnotation);
              operationParameters.addAll(resolvedParameter.parameters);
              formParameters.addAll(resolvedParameter.formParameters);
              if (resolvedParameter.requestBody != null) {
                this.processRequestBody(resolvedParameter.requestBody, operation, methodConsumes, classConsumes, operationParameters, paramAnnotations[i], type, jsonViewAnnotationForRequestBody, (Map)null);
              }
            }
          } else {
            for(int i = 0; i < annotatedMethod.getParameterCount(); ++i) {
              AnnotatedParameter param = annotatedMethod.getParameter(i);
              type = TypeFactory.defaultInstance().constructType(param.getParameterType(), cls);
             List<io.swagger.v3.oas.annotations.Parameter> paramAnnotationList =  io.openapitools.swagger.custom.AnnotationsUtils.getAnnotation(io.swagger.v3.oas.annotations.Parameter.class, paramAnnotations[i]);

              for (io.swagger.v3.oas.annotations.Parameter annot:
              paramAnnotationList) {
                manageOneParam(classConsumes, methodConsumes, jsonViewAnnotation, jsonViewAnnotationForRequestBody, operation, operationParameters, formParameters, paramAnnotations, type, annot, i);

              }
            }
          }

          if (!formParameters.isEmpty()) {
            Schema mergedSchema = new ObjectSchema();
            Map<String, Encoding> encoding = new LinkedHashMap();
            Iterator var69 = formParameters.iterator();

            while(true) {
              if (!var69.hasNext()) {
                Parameter merged = (new Parameter()).schema(mergedSchema);
                this.processRequestBody(merged, operation, methodConsumes, classConsumes, operationParameters, new Annotation[0], (Type)null, jsonViewAnnotationForRequestBody, encoding);
                break;
              }

              Parameter formParam = (Parameter)var69.next();
              if (formParam.getExplode() != null || formParam.getStyle() != null && StyleEnum.fromString(formParam.getStyle().toString()) != null) {
                Encoding e = new Encoding();
                if (formParam.getExplode() != null) {
                  e.explode(formParam.getExplode());
                }

                if (formParam.getStyle() != null && StyleEnum.fromString(formParam.getStyle().toString()) != null) {
                  e.style(StyleEnum.fromString(formParam.getStyle().toString()));
                }

                encoding.put(formParam.getName(), e);
              }

              mergedSchema.addProperties(formParam.getName(), formParam.getSchema());
              if (formParam.getSchema() != null && StringUtils.isNotBlank(formParam.getDescription()) && StringUtils.isBlank(formParam.getSchema().getDescription())) {
                formParam.getSchema().description(formParam.getDescription());
              }

              if (null != formParam.getRequired() && formParam.getRequired()) {
                mergedSchema.addRequiredItem(formParam.getName());
              }
            }
          }

          Iterator chain;
          Parameter parentParameter;
          if (!operationParameters.isEmpty()) {
            chain = operationParameters.iterator();

            while(chain.hasNext()) {
              parentParameter = (Parameter)chain.next();
              operation.addParametersItem(parentParameter);
            }
          }

          if (parentParameters != null) {
            chain = parentParameters.iterator();

            while(chain.hasNext()) {
              parentParameter = (Parameter)chain.next();
              operation.addParametersItem(parentParameter);
            }
          }

          if (subResource != null && !scannedResources.contains(subResource)) {
            scannedResources.add(subResource);
            this.read(subResource, operationPath, httpMethod, true, operation.getRequestBody(), operation.getResponses(), classTags, operation.getParameters(), scannedResources);
            scannedResources.remove(subResource);
          } else {
            chain = OpenAPIExtensions.chain();
            if (chain.hasNext()) {
              OpenAPIExtension extension = (OpenAPIExtension)chain.next();
              extension.decorateOperation(operation, method, chain);
            }

            PathItem pathItemObject;
            if (this.openAPI.getPaths() != null && this.openAPI.getPaths().get(operationPath) != null) {
              pathItemObject = (PathItem)this.openAPI.getPaths().get(operationPath);
            } else {
              pathItemObject = new PathItem();
            }

            if (!StringUtils.isBlank(httpMethod)) {
              this.setPathItemOperation(pathItemObject, httpMethod, operation);
              this.paths.addPathItem(operationPath, pathItemObject);
              if (this.openAPI.getPaths() != null) {
                this.paths.putAll(this.openAPI.getPaths());
              }

              this.openAPI.setPaths(this.paths);
            }
          }
        }
      }
    }
  }

  private void manageOneParam(Consumes classConsumes, Consumes methodConsumes, JsonView jsonViewAnnotation, JsonView jsonViewAnnotationForRequestBody, Operation operation, List<Parameter> operationParameters, List<Parameter> formParameters, Annotation[][] paramAnnotations, JavaType type, io.swagger.v3.oas.annotations.Parameter paramAnnotation, int i) {
    Object paramType;
    ResolvedParameter resolvedParameter;
    paramType = ParameterProcessor.getParameterType(paramAnnotation, true);
    if (paramType == null) {
      paramType = type;
    } else if (!(paramType instanceof Class)) {
      paramType = type;
    }

    resolvedParameter = this.getParameters((Type)paramType, Arrays.asList(paramAnnotations[i]), operation, classConsumes, methodConsumes, jsonViewAnnotation);
    operationParameters.addAll(resolvedParameter.parameters);
    formParameters.addAll(resolvedParameter.formParameters);
    if (resolvedParameter.requestBody != null) {
      this.processRequestBody(resolvedParameter.requestBody, operation, methodConsumes, classConsumes, operationParameters, paramAnnotations[i], type, jsonViewAnnotationForRequestBody, (Map)null);
    }
  }

  protected Content processContent(Content content, Schema schema, Consumes methodConsumes, Consumes classConsumes) {
    if (content == null) {
      content = new Content();
    }

    String[] var5;
    int var6;
    int var7;
    String value;
    if (methodConsumes != null) {
      var5 = methodConsumes.value();
      var6 = var5.length;

      for(var7 = 0; var7 < var6; ++var7) {
        value = var5[var7];
        this.setMediaTypeToContent(schema, content, value);
      }
    } else if (classConsumes != null) {
      var5 = classConsumes.value();
      var6 = var5.length;

      for(var7 = 0; var7 < var6; ++var7) {
        value = var5[var7];
        this.setMediaTypeToContent(schema, content, value);
      }
    } else {
      this.setMediaTypeToContent(schema, content, "*/*");
    }

    return content;
  }

  protected void processRequestBody(Parameter requestBodyParameter, Operation operation, Consumes methodConsumes, Consumes classConsumes, List<Parameter> operationParameters, Annotation[] paramAnnotations, Type type, JsonView jsonViewAnnotation, Map<String, Encoding> encoding) {
    io.swagger.v3.oas.annotations.parameters.RequestBody requestBodyAnnotation = this.getRequestBody(Arrays.asList(paramAnnotations));
    RequestBody requestBody;
    MediaType mediaType;
    Content content;
    if (requestBodyAnnotation != null) {
      Optional<RequestBody> optionalRequestBody = OperationParser.getRequestBody(requestBodyAnnotation, classConsumes, methodConsumes, this.components, jsonViewAnnotation);
      if (optionalRequestBody.isPresent()) {
        requestBody = (RequestBody)optionalRequestBody.get();
        if (StringUtils.isBlank(requestBody.get$ref()) && (requestBody.getContent() == null || requestBody.getContent().isEmpty())) {
          if (requestBodyParameter.getSchema() != null) {
            content = this.processContent(requestBody.getContent(), requestBodyParameter.getSchema(), methodConsumes, classConsumes);
            requestBody.setContent(content);
          }
        } else if (StringUtils.isBlank(requestBody.get$ref()) && requestBody.getContent() != null && !requestBody.getContent().isEmpty() && requestBodyParameter.getSchema() != null) {
          Iterator var13 = requestBody.getContent().values().iterator();

          while(var13.hasNext()) {
            mediaType = (MediaType)var13.next();
            if (mediaType.getSchema() == null) {
              if (requestBodyParameter.getSchema() == null) {
                mediaType.setSchema(new Schema());
              } else {
                mediaType.setSchema(requestBodyParameter.getSchema());
              }
            }

            if (StringUtils.isBlank(mediaType.getSchema().getType())) {
              mediaType.getSchema().setType(requestBodyParameter.getSchema().getType());
            }
          }
        }

        operation.setRequestBody(requestBody);
      }
    } else if (operation.getRequestBody() == null) {
      boolean isRequestBodyEmpty = true;
      requestBody = new RequestBody();
      if (StringUtils.isNotBlank(requestBodyParameter.get$ref())) {
        requestBody.set$ref(requestBodyParameter.get$ref());
        isRequestBodyEmpty = false;
      }

      if (StringUtils.isNotBlank(requestBodyParameter.getDescription())) {
        requestBody.setDescription(requestBodyParameter.getDescription());
        isRequestBodyEmpty = false;
      }

      if (Boolean.TRUE.equals(requestBodyParameter.getRequired())) {
        requestBody.setRequired(requestBodyParameter.getRequired());
        isRequestBodyEmpty = false;
      }

      if (requestBodyParameter.getSchema() != null) {
        content = this.processContent((Content)null, requestBodyParameter.getSchema(), methodConsumes, classConsumes);
        requestBody.setContent(content);
        isRequestBodyEmpty = false;
      }

      if (!isRequestBodyEmpty) {
        operation.setRequestBody(requestBody);
      }
    }

    if (operation.getRequestBody() != null && operation.getRequestBody().getContent() != null && encoding != null && !encoding.isEmpty()) {
      Content contentRequestBody = operation.getRequestBody().getContent();
      Iterator var16 = contentRequestBody.keySet().iterator();

      while(true) {
        String mediaKey;
        do {
          if (!var16.hasNext()) {
            return;
          }

          mediaKey = (String)var16.next();
        } while(!mediaKey.equals("application/x-www-form-urlencoded") && !mediaKey.equals("multipart/form-data"));

        mediaType = (MediaType)contentRequestBody.get(mediaKey);
        mediaType.encoding(encoding);
      }
    }
  }

  private io.swagger.v3.oas.annotations.parameters.RequestBody getRequestBody(List<Annotation> annotations) {
    if (annotations == null) {
      return null;
    } else {
      Iterator var2 = annotations.iterator();

      Annotation a;
      do {
        if (!var2.hasNext()) {
          return null;
        }

        a = (Annotation)var2.next();
      } while(!(a instanceof io.swagger.v3.oas.annotations.parameters.RequestBody));

      return (io.swagger.v3.oas.annotations.parameters.RequestBody)a;
    }
  }

  private void setMediaTypeToContent(Schema schema, Content content, String value) {
    MediaType mediaTypeObject = new MediaType();
    mediaTypeObject.setSchema(schema);
    content.addMediaType(value, mediaTypeObject);
  }

  public Operation parseMethod(Method method, List<Parameter> globalParameters, JsonView jsonViewAnnotation) {
    JavaType classType = TypeFactory.defaultInstance().constructType(method.getDeclaringClass());
    return this.parseMethod(classType.getClass(), method, globalParameters, (Produces)null, (Produces)null, (Consumes)null, (Consumes)null, new ArrayList(), Optional.empty(), new HashSet(), new ArrayList(), false, (RequestBody)null, (ApiResponses)null, jsonViewAnnotation, (ApiResponse[])null, (AnnotatedMethod)null);
  }

  public Operation parseMethod(Method method, List<Parameter> globalParameters, Produces methodProduces, Produces classProduces, Consumes methodConsumes, Consumes classConsumes, List<io.swagger.v3.oas.models.security.SecurityRequirement> classSecurityRequirements, Optional<io.swagger.v3.oas.models.ExternalDocumentation> classExternalDocs, Set<String> classTags, List<io.swagger.v3.oas.models.servers.Server> classServers, boolean isSubresource, RequestBody parentRequestBody, ApiResponses parentResponses, JsonView jsonViewAnnotation, ApiResponse[] classResponses) {
    JavaType classType = TypeFactory.defaultInstance().constructType(method.getDeclaringClass());
    return this.parseMethod(classType.getClass(), method, globalParameters, methodProduces, classProduces, methodConsumes, classConsumes, classSecurityRequirements, classExternalDocs, classTags, classServers, isSubresource, parentRequestBody, parentResponses, jsonViewAnnotation, classResponses, (AnnotatedMethod)null);
  }

  public Operation parseMethod(Method method, List<Parameter> globalParameters, Produces methodProduces, Produces classProduces, Consumes methodConsumes, Consumes classConsumes, List<io.swagger.v3.oas.models.security.SecurityRequirement> classSecurityRequirements, Optional<io.swagger.v3.oas.models.ExternalDocumentation> classExternalDocs, Set<String> classTags, List<io.swagger.v3.oas.models.servers.Server> classServers, boolean isSubresource, RequestBody parentRequestBody, ApiResponses parentResponses, JsonView jsonViewAnnotation, ApiResponse[] classResponses, AnnotatedMethod annotatedMethod) {
    JavaType classType = TypeFactory.defaultInstance().constructType(method.getDeclaringClass());
    return this.parseMethod(classType.getClass(), method, globalParameters, methodProduces, classProduces, methodConsumes, classConsumes, classSecurityRequirements, classExternalDocs, classTags, classServers, isSubresource, parentRequestBody, parentResponses, jsonViewAnnotation, classResponses, annotatedMethod);
  }

  protected Operation parseMethod(Class<?> cls, Method method, List<Parameter> globalParameters, Produces methodProduces, Produces classProduces, Consumes methodConsumes, Consumes classConsumes, List<io.swagger.v3.oas.models.security.SecurityRequirement> classSecurityRequirements, Optional<io.swagger.v3.oas.models.ExternalDocumentation> classExternalDocs, Set<String> classTags, List<io.swagger.v3.oas.models.servers.Server> classServers, boolean isSubresource, RequestBody parentRequestBody, ApiResponses parentResponses, JsonView jsonViewAnnotation, ApiResponse[] classResponses, AnnotatedMethod annotatedMethod) {
    Operation operation = new Operation();
    io.swagger.v3.oas.annotations.Operation apiOperation = (io.swagger.v3.oas.annotations.Operation)ReflectionUtils.getAnnotation(method, io.swagger.v3.oas.annotations.Operation.class);
    List<SecurityRequirement> apiSecurity = ReflectionUtils.getRepeatableAnnotations(method, SecurityRequirement.class);
    List<Callback> apiCallbacks = ReflectionUtils.getRepeatableAnnotations(method, Callback.class);
    List<Server> apiServers = ReflectionUtils.getRepeatableAnnotations(method, Server.class);
    List<io.swagger.v3.oas.annotations.tags.Tag> apiTags = ReflectionUtils.getRepeatableAnnotations(method, io.swagger.v3.oas.annotations.tags.Tag.class);
    List<io.swagger.v3.oas.annotations.Parameter> apiParameters = io.openapitools.swagger.custom.AnnotationsUtils.getRepeatableAnnotations(method, io.swagger.v3.oas.annotations.Parameter.class);
    List<ApiResponse> apiResponses = ReflectionUtils.getRepeatableAnnotations(method, ApiResponse.class);
    io.swagger.v3.oas.annotations.parameters.RequestBody apiRequestBody = (io.swagger.v3.oas.annotations.parameters.RequestBody)ReflectionUtils.getAnnotation(method, io.swagger.v3.oas.annotations.parameters.RequestBody.class);
    ExternalDocumentation apiExternalDocumentation = (ExternalDocumentation)ReflectionUtils.getAnnotation(method, ExternalDocumentation.class);
    Map<String, io.swagger.v3.oas.models.callbacks.Callback> callbacks = new LinkedHashMap();
    Iterator var29;
    if (apiCallbacks != null) {
      var29 = apiCallbacks.iterator();

      while(var29.hasNext()) {
        Callback methodCallback = (Callback)var29.next();
        Map<String, io.swagger.v3.oas.models.callbacks.Callback> currentCallbacks = this.getCallbacks(methodCallback, methodProduces, classProduces, methodConsumes, classConsumes, jsonViewAnnotation);
        callbacks.putAll(currentCallbacks);
      }
    }

    if (callbacks.size() > 0) {
      operation.setCallbacks(callbacks);
    }

    classSecurityRequirements.forEach(operation::addSecurityItem);
    if (apiSecurity != null) {
      Optional<List<io.swagger.v3.oas.models.security.SecurityRequirement>> requirementsObject = SecurityParser.getSecurityRequirements((SecurityRequirement[])apiSecurity.toArray(new SecurityRequirement[apiSecurity.size()]));
      if (requirementsObject.isPresent()) {
        ((List)requirementsObject.get()).stream().filter((r) -> {
          return operation.getSecurity() == null || !operation.getSecurity().contains(r);
        }).forEach(elt -> operation.addSecurityItem((io.swagger.v3.oas.models.security.SecurityRequirement) elt));
      }
    }

    if (classServers != null) {
      classServers.forEach(operation::addServersItem);
    }

    if (apiServers != null) {
      AnnotationsUtils.getServers((Server[])apiServers.toArray(new Server[apiServers.size()])).ifPresent((servers) -> {
        servers.forEach(operation::addServersItem);
      });
    }

    AnnotationsUtils.getExternalDocumentation(apiExternalDocumentation).ifPresent(operation::setExternalDocs);
    if (apiTags != null) {
      apiTags.stream().filter((t) -> {
        return operation.getTags() == null || operation.getTags() != null && !operation.getTags().contains(t.name());
      }).map(io.swagger.v3.oas.annotations.tags.Tag::name).forEach(operation::addTagsItem);
      AnnotationsUtils.getTags((io.swagger.v3.oas.annotations.tags.Tag[])apiTags.toArray(new io.swagger.v3.oas.annotations.tags.Tag[apiTags.size()]), true).ifPresent((tags) -> {
        this.openApiTags.addAll(tags);
      });
    }

    if (globalParameters != null) {
      var29 = globalParameters.iterator();

      while(var29.hasNext()) {
        Parameter globalParameter = (Parameter)var29.next();
        operation.addParametersItem(globalParameter);
      }
    }

    if (apiParameters != null) {
      this.getParametersListFromAnnotation((io.swagger.v3.oas.annotations.Parameter[])apiParameters.toArray(new io.swagger.v3.oas.annotations.Parameter[apiParameters.size()]), classConsumes, methodConsumes, operation, jsonViewAnnotation).ifPresent((p) -> {
        p.forEach(operation::addParametersItem);
      });
    }

    if (apiRequestBody != null && operation.getRequestBody() == null) {
      OperationParser.getRequestBody(apiRequestBody, classConsumes, methodConsumes, this.components, jsonViewAnnotation).ifPresent(operation::setRequestBody);
    }

    if (StringUtils.isBlank(operation.getOperationId())) {
      operation.setOperationId(this.getOperationId(method.getName()));
    }

    if (classResponses != null && classResponses.length > 0) {
      OperationParser.getApiResponses(classResponses, classProduces, methodProduces, this.components, jsonViewAnnotation).ifPresent((responses) -> {
        if (operation.getResponses() == null) {
          operation.setResponses(responses);
        } else {
          ApiResponses var10001 = operation.getResponses();
          responses.forEach(var10001::addApiResponse);
        }

      });
    }

    if (apiOperation != null) {
      this.setOperationObjectFromApiOperationAnnotation(operation, apiOperation, methodProduces, classProduces, methodConsumes, classConsumes, jsonViewAnnotation);
    }

    if (apiResponses != null && !apiResponses.isEmpty()) {
      OperationParser.getApiResponses((ApiResponse[])apiResponses.toArray(new ApiResponse[apiResponses.size()]), classProduces, methodProduces, this.components, jsonViewAnnotation).ifPresent((responses) -> {
        if (operation.getResponses() == null) {
          operation.setResponses(responses);
        } else {
          ApiResponses var10001 = operation.getResponses();
          responses.forEach(var10001::addApiResponse);
        }

      });
    }

    if (classTags != null) {
      classTags.stream().filter((t) -> {
        return operation.getTags() == null || operation.getTags() != null && !operation.getTags().contains(t);
      }).forEach(operation::addTagsItem);
    }

    if (operation.getExternalDocs() == null) {
      classExternalDocs.ifPresent(operation::setExternalDocs);
    }

    if (isSubresource && parentRequestBody != null) {
      if (operation.getRequestBody() == null) {
        operation.requestBody(parentRequestBody);
      } else {
        Content content = operation.getRequestBody().getContent();
        if (content == null) {
          content = parentRequestBody.getContent();
          operation.getRequestBody().setContent(content);
        } else if (parentRequestBody.getContent() != null) {
          Iterator var41 = parentRequestBody.getContent().keySet().iterator();

          while(var41.hasNext()) {
            String parentMediaType = (String)var41.next();
            if (content.get(parentMediaType) == null) {
              content.addMediaType(parentMediaType, (MediaType)parentRequestBody.getContent().get(parentMediaType));
            }
          }
        }
      }
    }

    Type returnType = method.getGenericReturnType();
    if (annotatedMethod != null && annotatedMethod.getType() != null) {
      returnType = annotatedMethod.getType();
    }

    Class<?> subResource = this.getSubResourceWithJaxRsSubresourceLocatorSpecs(method);
    if (!this.shouldIgnoreClass(((Type)returnType).getTypeName()) && !method.getGenericReturnType().equals(subResource)) {
      ResolvedSchema resolvedSchema = ModelConverters.getInstance().resolveAsResolvedSchema((new AnnotatedType((Type)returnType)).resolveAsRef(true).jsonViewAnnotation(jsonViewAnnotation));
      if (resolvedSchema.schema != null) {
        Schema returnTypeSchema = resolvedSchema.schema;
        Content content = new Content();
        MediaType mediaType = (new MediaType()).schema(returnTypeSchema);
        AnnotationsUtils.applyTypes(classProduces == null ? new String[0] : classProduces.value(), methodProduces == null ? new String[0] : methodProduces.value(), content, mediaType);
        if (operation.getResponses() == null) {
          operation.responses((new ApiResponses())._default((new io.swagger.v3.oas.models.responses.ApiResponse()).description("default response").content(content)));
        }

        if (operation.getResponses().getDefault() != null && StringUtils.isBlank(operation.getResponses().getDefault().get$ref())) {
          if (operation.getResponses().getDefault().getContent() == null) {
            operation.getResponses().getDefault().content(content);
          } else {
            Iterator var35 = operation.getResponses().getDefault().getContent().keySet().iterator();

            while(var35.hasNext()) {
              String key = (String)var35.next();
              if (((MediaType)operation.getResponses().getDefault().getContent().get(key)).getSchema() == null) {
                ((MediaType)operation.getResponses().getDefault().getContent().get(key)).setSchema(returnTypeSchema);
              }
            }
          }
        }

        Map<String, Schema> schemaMap = resolvedSchema.referencedSchemas;
        if (schemaMap != null) {
          schemaMap.forEach((keyx, schema) -> {
            this.components.addSchemas(keyx, schema);
          });
        }
      }
    }

    if (operation.getResponses() == null || operation.getResponses().isEmpty()) {
      Content content = new Content();
      MediaType mediaType = new MediaType();
      AnnotationsUtils.applyTypes(classProduces == null ? new String[0] : classProduces.value(), methodProduces == null ? new String[0] : methodProduces.value(), content, mediaType);
      io.swagger.v3.oas.models.responses.ApiResponse apiResponseObject = (new io.swagger.v3.oas.models.responses.ApiResponse()).description("default response").content(content);
      operation.setResponses((new ApiResponses())._default(apiResponseObject));
    }

    return operation;
  }

  private boolean shouldIgnoreClass(String className) {
    if (StringUtils.isBlank(className)) {
      return true;
    } else {
      boolean ignore = false;
      String rawClassName = className;
      if (className.startsWith("[")) {
        rawClassName = className.replace("[simple type, class ", "");
        rawClassName = rawClassName.substring(0, rawClassName.length() - 1);
      }

      ignore = rawClassName.startsWith("javax.ws.rs.");
      ignore = ignore || rawClassName.equalsIgnoreCase("void");
      ignore = ignore || ModelConverters.getInstance().isRegisteredAsSkippedClass(rawClassName);
      return ignore;
    }
  }

  private Map<String, io.swagger.v3.oas.models.callbacks.Callback> getCallbacks(Callback apiCallback, Produces methodProduces, Produces classProduces, Consumes methodConsumes, Consumes classConsumes, JsonView jsonViewAnnotation) {
    Map<String, io.swagger.v3.oas.models.callbacks.Callback> callbackMap = new HashMap();
    if (apiCallback == null) {
      return callbackMap;
    } else {
      io.swagger.v3.oas.models.callbacks.Callback callbackObject = new io.swagger.v3.oas.models.callbacks.Callback();
      if (StringUtils.isNotBlank(apiCallback.ref())) {
        callbackObject.set$ref(apiCallback.ref());
        callbackMap.put(apiCallback.name(), callbackObject);
        return callbackMap;
      } else {
        PathItem pathItemObject = new PathItem();
        io.swagger.v3.oas.annotations.Operation[] var10 = apiCallback.operation();
        int var11 = var10.length;

        for(int var12 = 0; var12 < var11; ++var12) {
          io.swagger.v3.oas.annotations.Operation callbackOperation = var10[var12];
          Operation callbackNewOperation = new Operation();
          this.setOperationObjectFromApiOperationAnnotation(callbackNewOperation, callbackOperation, methodProduces, classProduces, methodConsumes, classConsumes, jsonViewAnnotation);
          this.setPathItemOperation(pathItemObject, callbackOperation.method(), callbackNewOperation);
        }

        callbackObject.addPathItem(apiCallback.callbackUrlExpression(), pathItemObject);
        callbackMap.put(apiCallback.name(), callbackObject);
        return callbackMap;
      }
    }
  }

  private void setPathItemOperation(PathItem pathItemObject, String method, Operation operation) {
    switch (method) {
      case "post":
        pathItemObject.post(operation);
        break;
      case "get":
        pathItemObject.get(operation);
        break;
      case "delete":
        pathItemObject.delete(operation);
        break;
      case "put":
        pathItemObject.put(operation);
        break;
      case "patch":
        pathItemObject.patch(operation);
        break;
      case "trace":
        pathItemObject.trace(operation);
        break;
      case "head":
        pathItemObject.head(operation);
        break;
      case "options":
        pathItemObject.options(operation);
    }

  }

  private void setOperationObjectFromApiOperationAnnotation(Operation operation, io.swagger.v3.oas.annotations.Operation apiOperation, Produces methodProduces, Produces classProduces, Consumes methodConsumes, Consumes classConsumes, JsonView jsonViewAnnotation) {
    if (StringUtils.isNotBlank(apiOperation.summary())) {
      operation.setSummary(apiOperation.summary());
    }

    if (StringUtils.isNotBlank(apiOperation.description())) {
      operation.setDescription(apiOperation.description());
    }

    if (StringUtils.isNotBlank(apiOperation.operationId())) {
      operation.setOperationId(this.getOperationId(apiOperation.operationId()));
    }

    if (apiOperation.deprecated()) {
      operation.setDeprecated(apiOperation.deprecated());
    }

    ReaderUtils.getStringListFromStringArray(apiOperation.tags()).ifPresent((tags) -> {
      tags.stream().filter((t) -> {
        return operation.getTags() == null || operation.getTags() != null && !operation.getTags().contains(t);
      }).forEach(operation::addTagsItem);
    });
    if (operation.getExternalDocs() == null) {
      AnnotationsUtils.getExternalDocumentation(apiOperation.externalDocs()).ifPresent(operation::setExternalDocs);
    }

    OperationParser.getApiResponses(apiOperation.responses(), classProduces, methodProduces, this.components, jsonViewAnnotation).ifPresent((responses) -> {
      if (operation.getResponses() == null) {
        operation.setResponses(responses);
      } else {
        ApiResponses var10001 = operation.getResponses();
        responses.forEach(var10001::addApiResponse);
      }

    });
    AnnotationsUtils.getServers(apiOperation.servers()).ifPresent((servers) -> {
      servers.forEach(operation::addServersItem);
    });
    this.getParametersListFromAnnotation(apiOperation.parameters(), classConsumes, methodConsumes, operation, jsonViewAnnotation).ifPresent((p) -> {
      p.forEach(operation::addParametersItem);
    });
    Optional<List<io.swagger.v3.oas.models.security.SecurityRequirement>> requirementsObject = SecurityParser.getSecurityRequirements(apiOperation.security());
    if (requirementsObject.isPresent()) {
      ((List<io.swagger.v3.oas.models.security.SecurityRequirement>)requirementsObject.get()).stream().filter((r) -> {
        return operation.getSecurity() == null || !operation.getSecurity().contains(r);
      }).forEach(operation::addSecurityItem);
    }

    if (apiOperation.requestBody() != null && operation.getRequestBody() == null) {
      OperationParser.getRequestBody(apiOperation.requestBody(), classConsumes, methodConsumes, this.components, jsonViewAnnotation).ifPresent(operation::setRequestBody);
    }

    if (apiOperation.extensions().length > 0) {
      Map<String, Object> extensions = AnnotationsUtils.getExtensions(apiOperation.extensions());
      if (extensions != null) {
        extensions.forEach(operation::addExtension);
      }
    }

  }

  protected String getOperationId(String operationId) {
    boolean operationIdUsed = this.existOperationId(operationId);
    String operationIdToFind = null;

    for(int counter = 0; operationIdUsed; operationIdUsed = this.existOperationId(operationIdToFind)) {
      Object[] var10001 = new Object[]{operationId, null};
      ++counter;
      var10001[1] = counter;
      operationIdToFind = String.format("%s_%d", var10001);
    }

    if (operationIdToFind != null) {
      operationId = operationIdToFind;
    }

    return operationId;
  }

  private boolean existOperationId(String operationId) {
    if (this.openAPI == null) {
      return false;
    } else if (this.openAPI.getPaths() != null && !this.openAPI.getPaths().isEmpty()) {
      Iterator var2 = this.openAPI.getPaths().values().iterator();

      Set pathOperationIds;
      do {
        if (!var2.hasNext()) {
          return false;
        }

        PathItem path = (PathItem)var2.next();
        pathOperationIds = this.extractOperationIdFromPathItem(path);
      } while(!pathOperationIds.contains(operationId));

      return true;
    } else {
      return false;
    }
  }

  protected Optional<List<Parameter>> getParametersListFromAnnotation(io.swagger.v3.oas.annotations.Parameter[] parameters, Consumes classConsumes, Consumes methodConsumes, Operation operation, JsonView jsonViewAnnotation) {
    if (parameters == null) {
      return Optional.empty();
    } else {
      List<Parameter> parametersObject = new ArrayList();
      io.swagger.v3.oas.annotations.Parameter[] var7 = parameters;
      int var8 = parameters.length;

      for(int var9 = 0; var9 < var8; ++var9) {
        io.swagger.v3.oas.annotations.Parameter parameter = var7[var9];
        ResolvedParameter resolvedParameter = this.getParameters(ParameterProcessor.getParameterType(parameter), Collections.singletonList(parameter), operation, classConsumes, methodConsumes, jsonViewAnnotation);
        parametersObject.addAll(resolvedParameter.parameters);
      }

      return parametersObject.isEmpty() ? Optional.empty() : Optional.of(parametersObject);
    }
  }

  protected ResolvedParameter getParameters(Type type, List<Annotation> annotations, Operation operation, Consumes classConsumes, Consumes methodConsumes, JsonView jsonViewAnnotation) {
    Iterator<OpenAPIExtension> chain = OpenAPIExtensions.chain();
    if (!chain.hasNext()) {
      return new ResolvedParameter();
    } else {
      LOGGER.debug("getParameters for {}", type);
      Set<Type> typesToSkip = new HashSet();
      OpenAPIExtension extension = (OpenAPIExtension)chain.next();
      LOGGER.debug("trying extension {}", extension);
      return extension.extractParameters(annotations, type, typesToSkip, this.components, classConsumes, methodConsumes, true, jsonViewAnnotation, chain);
    }
  }

  private Set<String> extractOperationIdFromPathItem(PathItem path) {
    Set<String> ids = new HashSet();
    if (path.getGet() != null && StringUtils.isNotBlank(path.getGet().getOperationId())) {
      ids.add(path.getGet().getOperationId());
    }

    if (path.getPost() != null && StringUtils.isNotBlank(path.getPost().getOperationId())) {
      ids.add(path.getPost().getOperationId());
    }

    if (path.getPut() != null && StringUtils.isNotBlank(path.getPut().getOperationId())) {
      ids.add(path.getPut().getOperationId());
    }

    if (path.getDelete() != null && StringUtils.isNotBlank(path.getDelete().getOperationId())) {
      ids.add(path.getDelete().getOperationId());
    }

    if (path.getOptions() != null && StringUtils.isNotBlank(path.getOptions().getOperationId())) {
      ids.add(path.getOptions().getOperationId());
    }

    if (path.getHead() != null && StringUtils.isNotBlank(path.getHead().getOperationId())) {
      ids.add(path.getHead().getOperationId());
    }

    if (path.getPatch() != null && StringUtils.isNotBlank(path.getPatch().getOperationId())) {
      ids.add(path.getPatch().getOperationId());
    }

    return ids;
  }

  private boolean isEmptyComponents(Components components) {
    if (components == null) {
      return true;
    } else if (components.getSchemas() != null && components.getSchemas().size() > 0) {
      return false;
    } else if (components.getSecuritySchemes() != null && components.getSecuritySchemes().size() > 0) {
      return false;
    } else if (components.getCallbacks() != null && components.getCallbacks().size() > 0) {
      return false;
    } else if (components.getExamples() != null && components.getExamples().size() > 0) {
      return false;
    } else if (components.getExtensions() != null && components.getExtensions().size() > 0) {
      return false;
    } else if (components.getHeaders() != null && components.getHeaders().size() > 0) {
      return false;
    } else if (components.getLinks() != null && components.getLinks().size() > 0) {
      return false;
    } else if (components.getParameters() != null && components.getParameters().size() > 0) {
      return false;
    } else if (components.getRequestBodies() != null && components.getRequestBodies().size() > 0) {
      return false;
    } else {
      return components.getResponses() == null || components.getResponses().size() <= 0;
    }
  }

  protected boolean isOperationHidden(Method method) {
    io.swagger.v3.oas.annotations.Operation apiOperation = (io.swagger.v3.oas.annotations.Operation)ReflectionUtils.getAnnotation(method, io.swagger.v3.oas.annotations.Operation.class);
    if (apiOperation != null && apiOperation.hidden()) {
      return true;
    } else {
      Hidden hidden = (Hidden)method.getAnnotation(Hidden.class);
      if (hidden != null) {
        return true;
      } else {
        return this.config != null && !Boolean.TRUE.equals(this.config.isReadAllResources()) && apiOperation == null;
      }
    }
  }

  protected boolean isMethodOverridden(Method method, Class<?> cls) {
    return ReflectionUtils.isOverriddenMethod(method, cls);
  }

  public void setApplication(Application application) {
    this.application = application;
  }

  protected boolean ignoreOperationPath(String path, String parentPath) {
    if (StringUtils.isBlank(path) && StringUtils.isBlank(parentPath)) {
      return true;
    } else if (StringUtils.isNotBlank(path) && StringUtils.isBlank(parentPath)) {
      return false;
    } else if (StringUtils.isBlank(path) && StringUtils.isNotBlank(parentPath)) {
      return false;
    } else {
      if (parentPath != null && !"".equals(parentPath) && !"/".equals(parentPath)) {
        if (!parentPath.startsWith("/")) {
          parentPath = "/" + parentPath;
        }

        if (parentPath.endsWith("/")) {
          parentPath = parentPath.substring(0, parentPath.length() - 1);
        }
      }

      if (path != null && !"".equals(path) && !"/".equals(path)) {
        if (!path.startsWith("/")) {
          path = "/" + path;
        }

        if (path.endsWith("/")) {
          path = path.substring(0, path.length() - 1);
        }
      }

      return path.equals(parentPath);
    }
  }

  protected Class<?> getSubResourceWithJaxRsSubresourceLocatorSpecs(Method method) {
    Class<?> rawType = method.getReturnType();
    Class type;
    if (Class.class.equals(rawType)) {
      type = getClassArgument(method.getGenericReturnType());
      if (type == null) {
        return null;
      }
    } else {
      type = rawType;
    }

    return method.getAnnotation(Path.class) != null && ReaderUtils.extractOperationMethod(method, (Iterator)null) == null ? type : null;
  }

  private static Class<?> getClassArgument(Type cls) {
    if (cls instanceof ParameterizedType) {
      ParameterizedType parameterized = (ParameterizedType)cls;
      Type[] args = parameterized.getActualTypeArguments();
      if (args.length != 1) {
        LOGGER.error("Unexpected class definition: {}", cls);
        return null;
      } else {
        Type first = args[0];
        return first instanceof Class ? (Class)first : null;
      }
    } else {
      LOGGER.error("Unknown class definition: {}", cls);
      return null;
    }
  }

  private static class MethodComparator implements Comparator<Method> {
    private MethodComparator() {
    }

    public int compare(Method m1, Method m2) {
      int val = m1.getName().compareTo(m2.getName());
      if (val == 0) {
        val = m1.getParameterTypes().length - m2.getParameterTypes().length;
        if (val == 0) {
          Class<?>[] types1 = m1.getParameterTypes();
          Class<?>[] types2 = m2.getParameterTypes();

          for(int i = 0; i < types1.length; ++i) {
            val = types1[i].getName().compareTo(types2[i].getName());
            if (val != 0) {
              break;
            }
          }
        }
      }

      return val;
    }
  }
}
