#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.${artifactId}.helper;


import javax.persistence.Column;
import javax.persistence.Transient;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Automtically map columns for SQL2o query
 */
public class SQL2oColumnMapping {


    private SQL2oColumnMapping() {

    }

    @SuppressWarnings("CanBeFinal")
    private static Map<Class<?>, Map<String, String>> mappedClass = new HashMap<>();


    /**
     * Get List column database name / entity field name
     * call .setColumnMappings(SQL2oColumnMapping.getMapping(YourDto.class)) in createQuery
     *
     * @param entity class
     * @return HashMap
     */
    public static Map<String, String> getMapping(Class<?> entity) {
        if (mappedClass.containsKey(entity))
            return mappedClass.get(entity);
        Map<String, String> result = null;
        if (entity != null) {
            result = new HashMap<>(entity.getDeclaredFields().length);
            for (Field field : entity.getDeclaredFields()) {
                AbstractMap.SimpleEntry<String, String> mapEntry = getKeyValueFromField(field);
                if (mapEntry != null) {
                    result.put(mapEntry.getKey(), mapEntry.getValue());
                }
            }
        }
        mappedClass.put(entity, result);
        return result;
    }

    private static AbstractMap.SimpleEntry<String, String> getKeyValueFromField(Field field) {

        AbstractMap.SimpleEntry<String, String> result = null;
        String name = null;
        boolean ignore = false;
        Annotation[] annotations = field.getAnnotations();
        for (Annotation a : annotations) {
            boolean brk = false;//Crazy... SonarLint
            if (a.annotationType() == Column.class) {
                name = ((Column) a).name();
                brk = true;//Crazy... SonarLint
            }
            if (a.annotationType() == Transient.class) {
                ignore = true;
                brk = true;//Crazy... SonarLint
            }
            if (brk) {//Crazy... SonarLint
                break;
            }
        }
        if (name == null || name.isEmpty()) {
            name = field.getName().toLowerCase();
        }
        if (!ignore) {
            result = new AbstractMap.SimpleEntry<>(name, field.getName());
        }
        return result;
    }


    private static String getColumnName(String property, Class<?> entity) {
        Map<String, String> map = getMapping(entity);
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getValue().equals(property)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public static String prepareSort(String sort, Class<?> entity) {
        StringBuilder orders = new StringBuilder();
        String[] sorts;
        if (sort.contains("|")) {
            sorts = sort.split("\\|");
        } else {
            sorts = new String[1];
            sorts[0] = sort;
        }
        for (String order : sorts) {
            String property;
            String direction = "ASC";
            if (order.contains(" ")) {
                property = order.split(" ")[0].trim();
                direction = order.split(" ")[1].trim().toUpperCase();
                if (!direction.equals("ASC") && !direction.equals("DESC")) {
                    direction = "ASC";
                }
            } else {
                property = order.trim();
            }
            String columnName = SQL2oColumnMapping.getColumnName(property, entity);
            if (columnName != null) {
                if (orders.length() > 0) {
                    orders.append(", ");
                }
                orders.append(columnName);
                orders.append(" ");
                orders.append(direction);
            }
        }
        return orders.toString();
    }

}
