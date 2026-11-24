-- HU-05 · Contacto directo sobre una oferta
--
-- La tabla mensajes exigia id_orden NOT NULL y no referenciaba al producto, de modo que un comprador
-- no podia escribirle al agricultor antes de emitir una orden, que es justamente lo que pide HU-05.
-- Este script alinea la base existente con el schema.sql actualizado. Se ejecuta una sola vez, desde
-- el editor SQL de Supabase. Mientras Flyway siga diferido (AV-023), las migraciones se aplican a mano
-- y quedan versionadas en este directorio.

ALTER TABLE mensajes ADD COLUMN IF NOT EXISTS id_producto BIGINT;

ALTER TABLE mensajes ALTER COLUMN id_orden DROP NOT NULL;

ALTER TABLE mensajes
    ADD CONSTRAINT fk_mensajes_producto FOREIGN KEY (id_producto) REFERENCES productos (id_producto);

-- Un mensaje cuelga de una oferta (negociacion previa) o de una orden (conversacion posterior),
-- nunca de ninguna de las dos.
ALTER TABLE mensajes
    ADD CONSTRAINT ck_mensajes_contexto CHECK (id_producto IS NOT NULL OR id_orden IS NOT NULL);
