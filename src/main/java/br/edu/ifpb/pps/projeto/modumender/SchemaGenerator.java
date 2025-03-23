package br.edu.ifpb.pps.projeto.modumender;

import br.edu.ifpb.pps.projeto.modumender.util.TableGenerator;
import br.edu.ifpb.pps.projeto.modumender.util.RelationshipCreator;
import org.reflections.Reflections;
import br.edu.ifpb.pps.projeto.modumender.annotations.Entity;

import java.util.Set;

/**
 * Classe principal (fachada) para gerar o schema.
 * 1) Cria tabelas.
 * 2) Cria relacionamentos (FKs).
 */
public class SchemaGenerator {

    public static void generateAllSchemas() {
        System.out.println("🔸 Iniciando geração do schema (tabelas + relacionamentos)...");

        // Identifica todas as entidades no pacote de modelos
        Reflections reflections = new Reflections("br.edu.ifpb.pps.projeto.modumender.model");
        Set<Class<?>> entityClasses = reflections.getTypesAnnotatedWith(Entity.class);

        // 1) Cria as tabelas
        TableGenerator.generateTables(entityClasses);

        // 2) Cria os relacionamentos
        RelationshipCreator.generateRelationships(entityClasses);

        System.out.println("✅ Geração de schema concluída.\n");
    }

    // -------------------------------------------------------
    // Métodos estáticos para CRUD genérico (opcional)
    // -------------------------------------------------------
    /**
     * Gera o SQL de INSERT (ex.: "INSERT INTO tabela (col1, col2) VALUES (?, ?)")
     * Chamado pelo DAO/Service.
     */
    public static String generateInsert(Object entity) {
        return br.edu.ifpb.pps.projeto.modumender.util.SQLUtils.generateInsert(entity);
    }

    public static String generateFindAll(Class<?> clazz) {
        return br.edu.ifpb.pps.projeto.modumender.util.SQLUtils.generateFindAll(clazz);
    }

    public static String generateFindById(Class<?> clazz) {
        return br.edu.ifpb.pps.projeto.modumender.util.SQLUtils.generateFindById(clazz);
    }

    public static <T> String generateUpdate(T entity) {
        Class<?> clazz = entity.getClass();
        return br.edu.ifpb.pps.projeto.modumender.util.SQLUtils.generateUpdate(clazz, entity);
    }

}
