-- =============================================================================================
-- V3 - Eliminar la tabla huerfana usuarios y las claves foraneas que la sostenian
--
-- La base tenia dos tablas de usuarios: usuario, que es la que mapea la entidad Usuario y la que
-- usa toda la aplicacion, y usuarios en plural, que ningun mapeo referencia. La creo Hibernate
-- con ddl-auto=update en algun momento en que la entidad apuntaba a ese nombre, y quedo huerfana
-- al volver al nombre en singular: update sabe crear, nunca limpiar lo que deja de usarse.
--
-- El primer intento de esta migracion fallo con SQLSTATE 2BP01. agricultores y
-- compradores_comerciales conservaban una clave foranea cada una hacia usuarios
-- (fkh7mc22mrje3bv35p6tnyt0sxt y fks0woligtbh165ljoehufq06v9), tambien dejadas por Hibernate y
-- nunca retiradas. Las dos cuelgan de la misma columna id_usuario que ya tiene su clave foranea
-- correcta hacia usuario, de modo que son duplicados que apuntan a una tabla vacia.
--
-- Esto no era solo suciedad en el esquema. Se consulto el catalogo y las dos restricciones
-- estaban validadas (convalidated = true), asi que Postgres las exigia: insertar un agricultor o
-- un comprador obligaba a que el mismo id existiera ademas en usuarios, que tiene 0 filas. El
-- registro de ambos perfiles era imposible contra esta base. No lo habiamos detectado porque las
-- pruebas de integracion corren sobre H2, que se construye desde cero en cada ejecucion y nunca
-- heredo estas restricciones. Coherente con el sintoma: agricultores y compradores_comerciales
-- estaban en 0 filas al momento de escribir esta migracion.
--
-- Las restricciones se eliminan consultando el catalogo y no por nombre, porque esos nombres son
-- hashes que genera Hibernate y no queremos depender de ellos. El bloque solo toca restricciones
-- cuya tabla referenciada es usuarios, de modo que las claves foraneas hacia usuario quedan
-- intactas.
--
-- El DROP TABLE sigue sin CASCADE a proposito: una vez retiradas esas dos restricciones no debe
-- quedar nada dependiendo de la tabla, y si quedara, la migracion debe fallar y avisarnos en vez
-- de arrastrar dependencias por delante.
-- =============================================================================================

DO $$
DECLARE
    restriccion RECORD;
BEGIN
    IF to_regclass('public.usuarios') IS NULL THEN
        RAISE NOTICE 'La tabla usuarios no existe; no hay restricciones que retirar.';
        RETURN;
    END IF;

    FOR restriccion IN
        SELECT con.conname AS nombre, cls.relname AS tabla
        FROM pg_constraint con
        JOIN pg_class cls ON cls.oid = con.conrelid
        WHERE con.contype = 'f'
          AND con.confrelid = 'public.usuarios'::regclass
    LOOP
        EXECUTE format('ALTER TABLE public.%I DROP CONSTRAINT %I',
                       restriccion.tabla, restriccion.nombre);
        RAISE NOTICE 'Retirada la clave foranea % de la tabla %',
                     restriccion.nombre, restriccion.tabla;
    END LOOP;
END $$;

DROP TABLE IF EXISTS usuarios;
