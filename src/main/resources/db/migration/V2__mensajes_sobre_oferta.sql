-- =============================================================================================
-- V2 - Mensajes sobre una oferta sin orden previa (habilita HU-05)
--
-- La tabla mensajes exigia id_orden NOT NULL y no referenciaba al producto, de modo que un
-- comprador no podia escribirle al agricultor antes de emitir una orden.
--
-- Se escribe de forma idempotente porque este cambio pudo aplicarse a mano en Supabase antes de
-- adoptar Flyway: si ya esta, la migracion no hace nada y queda registrada igual.
-- =============================================================================================

ALTER TABLE mensajes ADD COLUMN IF NOT EXISTS id_producto BIGINT;

ALTER TABLE mensajes ALTER COLUMN id_orden DROP NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_mensajes_producto') THEN
        ALTER TABLE mensajes
            ADD CONSTRAINT fk_mensajes_producto FOREIGN KEY (id_producto) REFERENCES productos (id_producto);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_mensajes_contexto') THEN
        ALTER TABLE mensajes
            ADD CONSTRAINT ck_mensajes_contexto CHECK (id_producto IS NOT NULL OR id_orden IS NOT NULL);
    END IF;
END $$;
