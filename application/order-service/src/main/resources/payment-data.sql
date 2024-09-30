insert into payments (payment_id, user_id, concert_id, total_price, status, card, refund_price, is_deleted, created_at, created_by,
                      updated_at, updated_by, deleted_at, deleted_by)

values
    (1, 1, 1, 180000, 'COMPLETED', /*암호화된 카드 정보*/, null, false, false, now(), 1, now(), 1, null, null),
    (2, 2, 2, 200000, 'CANCELED', /*암호화된 카드 정보*/, 200000, false, false, now(), 1, now(), 1, null, null);
