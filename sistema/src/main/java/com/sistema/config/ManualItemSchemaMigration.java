package com.sistema.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Ajusta instalaciones existentes, creadas cuando todo detalle debía estar
 * vinculado obligatoriamente a un producto del catálogo.
 */
@Component
public class ManualItemSchemaMigration implements ApplicationRunner {

    private static final List<String> ITEM_TABLES = List.of(
            "presupuesto_detalle",
            "venta_item",
            "remito_item"
    );

    private final JdbcTemplate jdbcTemplate;

    public ManualItemSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (String table : ITEM_TABLES) {
            permitirProductoNulo(table);
            ampliarDescripcion(table);
        }
    }

    private void permitirProductoNulo(String table) {
        List<String> nullability = jdbcTemplate.queryForList(
                "SELECT IS_NULLABLE FROM information_schema.COLUMNS "
                        + "WHERE TABLE_SCHEMA = DATABASE() "
                        + "AND TABLE_NAME = ? AND COLUMN_NAME = 'producto_id'",
                String.class,
                table);

        if (!nullability.isEmpty() && "NO".equalsIgnoreCase(nullability.get(0))) {
            jdbcTemplate.execute(
                    "ALTER TABLE `" + table
                            + "` MODIFY COLUMN `producto_id` BIGINT NULL");
        }
    }

    private void ampliarDescripcion(String table) {
        List<String> dataTypes = jdbcTemplate.queryForList(
                "SELECT DATA_TYPE FROM information_schema.COLUMNS "
                        + "WHERE TABLE_SCHEMA = DATABASE() "
                        + "AND TABLE_NAME = ? AND COLUMN_NAME = 'descripcion'",
                String.class,
                table);

        if (!dataTypes.isEmpty()
                && !"longtext".equalsIgnoreCase(dataTypes.get(0))) {
            jdbcTemplate.execute(
                    "ALTER TABLE `" + table
                            + "` MODIFY COLUMN `descripcion` LONGTEXT NULL");
        }
    }
}
