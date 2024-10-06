insert into booking (payment_id, concert_id, user_id, price, status, seat, is_deleted, created_at, created_by,
                   updated_at, updated_by, deleted_at, deleted_by)

values
    (1, 1, 1, 90000, 'CONFIRMED', '3 3', false, now(), 'admin@admin.com', now(), 'admin@admin.com', null, null),
    (1, 1, 1, 90000, 'CONFIRMED', '3 4', false, now(), 'admin@admin.com', now(), 'admin@admin.com', null, null),
    (2, 2, 2, 100000, 'CANCELED', '5 5', false, now(), 'admin@admin.com', now(), 'admin@admin.com', null, null),
    (2, 2, 2, 100000, 'CANCELED', '5 6', false, now(), 'admin@admin.com', now(), 'admin@admin.com', null, null);
