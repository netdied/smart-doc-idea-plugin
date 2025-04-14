package com.ly.smartdoc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;

import java.lang.reflect.Type;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonConverter {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(TemporalAccessor.class, new TemporalSerializer())
            .create();

    public static String convertReturnTypeToJson(PsiType returnType) {
        try {
            if (returnType == null) {
                return "null";
            }
            return GSON.toJson(createSampleValue(returnType));
        } catch (Exception e) {
            e.printStackTrace();
            return "转换失败: " + e.getMessage();
        }
    }

    private static Object createSampleValue(PsiType psiType) {
        if (psiType instanceof PsiClassType classType) {
            PsiClass resolvedClass = classType.resolve();
            if (resolvedClass != null) {
                return createObjectFromFields(resolvedClass.getFields());
            }
        } else if (psiType.getCanonicalText().equals("java.util.List")) {
            PsiType componentType = ((PsiClassType) psiType).getParameters()[0];
            List<Object> list = new ArrayList<>();
            list.add(createSampleValue(componentType));
            return list;
        } else if (psiType.getCanonicalText().equals("java.util.Map")) {
            PsiType[] parameters = ((PsiClassType) psiType).getParameters();
            Map<String, Object> map = new HashMap<>();
            map.put("key", createSampleValue(parameters[1]));
            return map;
        } else if (psiType.getCanonicalText().startsWith("[")) {
            PsiType componentType = psiType.getDeepComponentType();
            return new Object[]{createSampleValue(componentType)};
        } else {
            return createPrimitiveValue(psiType.getCanonicalText());
        }
        return null;
    }

    /**
     * 根据字段列表创建对象
     *
     * @param fields 字段列表
     * @return 示例对象
     */
    private static Object createObjectFromFields(PsiField[] fields) {
        Map<String, Object> sampleObject = new HashMap<>();
        for (PsiField field : fields) {
            PsiType fieldType = field.getType();
            sampleObject.put(field.getName(), createSampleValue(fieldType));
        }
        return sampleObject;
    }


    private static Object createPrimitiveValue(String canonicalText) {
        return switch (canonicalText) {
            case "int", "java.lang.Integer" -> 123;
            case "double", "java.lang.Double" -> 123.45;
            case "boolean", "java.lang.Boolean" -> true;
            case "java.lang.String" -> "sampleString";
            default -> null;
        };
    }


    private static class TemporalSerializer implements JsonSerializer<TemporalAccessor> {
        @Override
        public JsonElement serialize(TemporalAccessor src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }
}