insert into bookings (booking_id, payment_id, concert_id, user_id, price, status, seat, is_deleted, created_at, created_by,
                   updated_at, updated_by, deleted_at, deleted_by)

values
    (1, 1, 1, 1, 90000, 'CONFIRMED', '3 3', false, now(), 1, now(), 1, null, null),
    (2, 1, 1, 1, 90000, 'CONFIRMED', '3 4', false, now(), 1, now(), 1, null, null),
    (3, 2, 2, 2, 100000, 'CANCELLED', '5 5', false, now(), 1, now(), 1, null, null),
    (4, 2, 2, 2, 100000, 'CANCELLED', '5 6', false, now(), 1, now(), 1, null, null);