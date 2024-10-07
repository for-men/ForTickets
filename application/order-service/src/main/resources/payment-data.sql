insert into payment (user_id, concert_id, total_price, status, card, is_deleted, created_at, created_by,
                      updated_at, updated_by, deleted_at, deleted_by)

values
    (1, 1, 180000, 'COMPLETED', '암호화된 카드 정보', false, now(), 'admin@admin.com', now(), 'admin@admin.com', null, null),
    (2, 2, 200000, 'CANCELED', '암호화된 카드 정보',  false, now(), 'admin@admin.com', now(), 'admin@admin.com', null, null);
