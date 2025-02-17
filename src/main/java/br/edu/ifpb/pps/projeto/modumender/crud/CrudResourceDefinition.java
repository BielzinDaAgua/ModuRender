        package br.edu.ifpb.pps.projeto.modumender.crud;

        /**
         * Armazena path base e a classe da entidade
         * para gerar CRUD automático.
         */
        public class CrudResourceDefinition {
            private final String basePath;
            private final Class<?> entityClass;

            public CrudResourceDefinition(String basePath, Class<?> entityClass) {
                this.basePath = basePath;
                this.entityClass = entityClass;
            }

            public String getBasePath() {
                return basePath;
            }

            public Class<?> getEntityClass() {
                return entityClass;
            }
        }
