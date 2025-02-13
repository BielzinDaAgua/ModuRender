package br.edu.ifpb.pps.projeto.modumender.util;

import br.edu.ifpb.pps.projeto.modumender.annotations.*;
import java.lang.reflect.Field;
import java.util.Set;

public class RelationshipCreator {

    public static void generateRelationships(Set<Class<?>> entityClasses) {
        System.out.println("🔹 Criando relacionamentos...");
        for (Class<?> entityClass : entityClasses) {
            createRelationships(entityClass);
        }
        System.out.println("✅ Relacionamentos criados.");
    }

    private static void createRelationships(Class<?> clazz) {
        String thisTable = SQLUtils.getTableName(clazz);
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(ManyToOne.class)) {
                createManyToOne(field, thisTable);
            } else if (field.isAnnotationPresent(OneToOne.class)) {
                createOneToOne(field, thisTable);
            } else if (field.isAnnotationPresent(ManyToMany.class)) {
                createManyToMany(field, thisTable);
            }
        }
    }

    private static void createManyToOne(Field field, String thisTable) {
        ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
        String otherTable = SQLUtils.getTableName(field.getType());
        String fkColumnName = field.getName() + "_id";

        SQLUtils.executeSQL(String.format("ALTER TABLE %s ADD COLUMN IF NOT EXISTS %s INT", thisTable, fkColumnName), "Add column ManyToOne");
        SQLUtils.executeSQLSafeConstraint(String.format("ALTER TABLE %s ADD CONSTRAINT fk_%s_%s FOREIGN KEY (%s) REFERENCES %s(id)", thisTable, thisTable, fkColumnName, fkColumnName, otherTable));
    }

    private static void createOneToOne(Field field, String thisTable) {
        OneToOne oneToOne = field.getAnnotation(OneToOne.class);
        String otherTable = SQLUtils.getTableName(field.getType());
        String fkColumnName = field.getName() + "_id";

        SQLUtils.executeSQL(String.format("ALTER TABLE %s ADD COLUMN IF NOT EXISTS %s INT", thisTable, fkColumnName), "Add column OneToOne");
        SQLUtils.executeSQLSafeConstraint(String.format("ALTER TABLE %s ADD CONSTRAINT fk_%s_%s FOREIGN KEY (%s) REFERENCES %s(id)", thisTable, thisTable, fkColumnName, fkColumnName, otherTable));
        SQLUtils.executeSQLSafeConstraint(String.format("ALTER TABLE %s ADD CONSTRAINT uk_%s_%s UNIQUE (%s)", thisTable, thisTable, fkColumnName, fkColumnName));
    }

    private static void createManyToMany(Field field, String thisTable) {
        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
        String joinTable = manyToMany.joinTable();
        String joinColumn = manyToMany.joinColumn();
        String inverseJoinColumn = manyToMany.inverseJoinColumn();

        SQLUtils.executeSQL(String.format("CREATE TABLE IF NOT EXISTS %s (%s INT NOT NULL, %s INT NOT NULL, PRIMARY KEY(%s, %s))", joinTable, joinColumn, inverseJoinColumn, joinColumn, inverseJoinColumn), "Create join table ManyToMany");
    }
}
