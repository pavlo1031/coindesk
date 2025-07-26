package cathay.coindeskApi.commons.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import javax.persistence.Column;

public class JpaUtils {
	/**
	 * iterate時, 不進行filter
	 */
	public static <E> void foreachColumn(Class<E> entityClass, BiConsumer<Column, Field> eachColumn) {
		foreachColumn(entityClass, (BiPredicate<Column, Field>) null, eachColumn);
	}
	
	/**
	 * 進行filter時, 僅需column資訊
	 */
	public static <E> void foreachColumn(Class<E> entityClass, Predicate<Column> filter, BiConsumer<Column, Field> eachColumn) {
		foreachColumn(entityClass, (column, field) -> filter.test(column), eachColumn);
	}
	
	/**
	 * 進行filter時, 需column以及field資訊
	 */
	public static <E> void foreachColumn(Class<E> entityClass, BiPredicate<Column, Field> filter, BiConsumer<Column, Field> eachColumn) {
		if (entityClass == null)
			throw new NullPointerException("The argument 'entityClass' cannot be null");
		
		Field[] declaredFields = entityClass.getDeclaredFields();		
		for (Field f : declaredFields) {
			if (f.isAnnotationPresent(Column.class)) {
				Column column = f.getAnnotation(Column.class);
				if (filter != null && !filter.test(column, f))
					continue;
				
				if (eachColumn != null)
					eachColumn.accept(column, f);
			}
		}
	}
	
	public static <E> List<Column> getColumns(Class<E> entityClass) {
		if (entityClass == null)
			throw new NullPointerException("The argument 'entityClass' cannot be null");
		
		ArrayList<Column> columns = new ArrayList<Column>();  
		foreachColumn(entityClass, (column) -> !column.nullable(), (column, f) -> {
			columns.add(column);
		});
		return columns;
	}

	public static boolean isColumnRequired(Field field) {
		return !isColumnNullable(field);
	}
	
	public static boolean isColumnNullable(Field field) {
		if (field == null)
			throw new NullPointerException("The argument 'field' cannot be null");
		
		if (!field.isAnnotationPresent(Column.class)) {
			throw new IllegalArgumentException("The field is not annotated with 'Column'");
		}		
		Column c = field.getAnnotation(Column.class);
		return c.nullable();
	}
	
	public static boolean isColumnInsertable(Field field) {
		if (field == null)
			throw new NullPointerException("The argument 'field' cannot be null");
		
		if (!field.isAnnotationPresent(Column.class)) {
			throw new IllegalArgumentException("The field is not annotated with 'Column'");
		}
		Column c = field.getAnnotation(Column.class);
		return c.insertable();
	}
	
	public static boolean isColumnUpdatable(Field field) {
		if (field == null)
			throw new NullPointerException("The argument 'field' cannot be null");
		
		if (!field.isAnnotationPresent(Column.class)) {
			throw new IllegalArgumentException("The field is not annotated with 'Column'");
		}
		Column c = field.getAnnotation(Column.class);
		return c.updatable();
	}
}
