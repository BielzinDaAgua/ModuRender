package br.edu.ifpb.pps.projeto.modumender.util;

import br.edu.ifpb.pps.projeto.modumender.annotations.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;

/**
 * Responsável por criar os relacionamentos
 * (FKs para ManyToOne, OneToOne, e tabelas de junção e FKs para ManyToMany).
 */
public class RelationshipCreator {

    public static void generateRelationships(Set<Class<?>> entityClasses) {
        System.out.println("🔹 Criando relacionamentos (FKs, ManyToMany joins)...");
        for (Class<?> entityClass : entityClasses) {
            createRelationshipsForClass(entityClass);
        }
        System.out.println("✅ Relacionamentos criados com sucesso!");
    }

    private static void createRelationshipsForClass(Class<?> clazz) {
        String thisTable = SQLUtils.getTableName(clazz);
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(ManyToOne.class)) {
                createManyToOne(field, thisTable);
            }
            else if (field.isAnnotationPresent(OneToOne.class)) {
                createOneToOne(field, thisTable);
            }
            else if (field.isAnnotationPresent(ManyToMany.class)) {
                createManyToMany(field, thisTable);
            }
        }
    }

    private static void createManyToOne(Field field, String thisTable) {
        ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);

        // Tabela referenciada
        Class<?> otherClass = field.getType();
        String otherTable = SQLUtils.getTableName(otherClass);

        // Nome da coluna FK
        String fkColumnName = field.getName() + "_id";

        // 1) Adiciona a coluna
        String addColSQL = String.format(
                "ALTER TABLE %s ADD COLUMN IF NOT EXISTS %s INT",
                thisTable, fkColumnName
        );
        SQLUtils.executeSQL(addColSQL, "Add column ManyToOne");

        // 2) Adiciona constraint
        String constraintSQL = String.format(
                "ALTER TABLE %s ADD CONSTRAINT fk_%s_%s FOREIGN KEY (%s) REFERENCES %s(%s)",
                thisTable, thisTable, fkColumnName, fkColumnName, otherTable, manyToOne.referencedColumnName()
        );
        SQLUtils.executeSQLSafeConstraint(constraintSQL);
    }

    private static void createOneToOne(Field field, String thisTable) {
        OneToOne oneToOne = field.getAnnotation(OneToOne.class);

        Class<?> otherClass = field.getType();
        String otherTable = SQLUtils.getTableName(otherClass);

        String fkColumnName = field.getName() + "_id";

        // Add column
        String addColSQL = String.format(
                "ALTER TABLE %s ADD COLUMN IF NOT EXISTS %s INT",
                thisTable, fkColumnName
        );
        SQLUtils.executeSQL(addColSQL, "Add column OneToOne");

        // Add FK constraint
        String constraintSQL = String.format(
                "ALTER TABLE %s ADD CONSTRAINT fk_%s_%s FOREIGN KEY (%s) REFERENCES %s(%s)",
                thisTable, thisTable, fkColumnName, fkColumnName, otherTable, oneToOne.referencedColumnName()
        );
        SQLUtils.executeSQLSafeConstraint(constraintSQL);

        // Unique constraint p/ 1:1
        String uniqueSQL = String.format(
                "ALTER TABLE %s ADD CONSTRAINT uk_%s_%s UNIQUE (%s)",
                thisTable, thisTable, fkColumnName, fkColumnName
        );
        SQLUtils.executeSQLSafeConstraint(uniqueSQL);
    }

    private static void createManyToMany(Field field, String thisTable) {
        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);

        String joinTable = manyToMany.joinTable();
        String joinColumn = manyToMany.joinColumn();
        String inverseJoinColumn = manyToMany.inverseJoinColumn();

        // O field deve ser List<?>. Precisamos pegar o tipo genérico
        Class<?> listType = getGenericListType(field);
        if (listType == null || !listType.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Relacionamento @ManyToMany inválido em " + field.getName()
                    + " - O tipo deve ser uma entidade anotada.");
        }
        String otherTable = SQLUtils.getTableName(listType);

        // 1) Cria a tabela de junção se não existir
        String createJoinSQL = String.format(
                "CREATE TABLE IF NOT EXISTS %s (%s INT NOT NULL, %s INT NOT NULL, PRIMARY KEY(%s, %s))",
                joinTable, joinColumn, inverseJoinColumn, joinColumn, inverseJoinColumn
        );
        SQLUtils.executeSQL(createJoinSQL, "Create join table ManyToMany");

        // 2) FK para 'thisTable'
        String fk1SQL = String.format(
                "ALTER TABLE %s ADD CONSTRAINT fk_%s_%s FOREIGN KEY (%s) REFERENCES %s(id)",
                joinTable, joinTable, joinColumn, joinColumn, thisTable
        );
        SQLUtils.executeSQLSafeConstraint(fk1SQL);

        // 3) FK para 'otherTable'
        String fk2SQL = String.format(
                "ALTER TABLE %s ADD CONSTRAINT fk_%s_%s FOREIGN KEY (%s) REFERENCES %s(id)",
                joinTable, joinTable, inverseJoinColumn, inverseJoinColumn, otherTable
        );
        SQLUtils.executeSQLSafeConstraint(fk2SQL);
    }

    /**
     * Retorna o tipo genérico da List<T>.
     * Se o campo for List<Curso>, retorna "Curso.class" (se possível).
     */
    private static Class<?> getGenericListType(Field field) {
        if (!List.class.isAssignableFrom(field.getType())) {
            return null;
        }
        try {
            if (field.getGenericType() instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) field.getGenericType();
                if (pt.getActualTypeArguments().length == 1) {
                    return Class.forName(pt.getActualTypeArguments()[0].getTypeName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
