#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.${artifactId}.helper;

import javax.persistence.Column;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Automtically map columns for SQL2o query
 */
public class SQL2oColumnMapping {

    /**
     * Get List column database name / entity field name
     * call .setColumnMappings(SQL2oColumnMapping.getMapping(YourDto.class)) in createQuery
     * @param  entity class
     * @return HashMap
     */
    @SuppressWarnings("Convert2Diamond")
    public static Map<String, String> getMapping(Class<?> entity) {
        Map<String, String> result = null;
        if (entity != null) {
            result = new HashMap<String, String>(entity.getDeclaredFields().length);
            for (Field field : entity.getDeclaredFields()) {
                String name = null;
                Annotation[] annotations = field.getAnnotations();
                for (Annotation a : annotations) {
                    if (a.annotationType() == Column.class) {
                        name = ((Column) a).name();
                        break;
                    }
                }
                if (name == null || name.isEmpty()) {
                    name = field.getName().toLowerCase();
                }
                result.put(name, field.getName());
            }
        }
        return result;
    }


}
