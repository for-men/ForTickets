insert into schedule (schedule_id, concert_id, stage_id, concert_date, concert_time, is_deleted, created_at, created_by,
                    updated_at, updated_by, deleted_at, deleted_by)

values
    (1, 1, 1, '2024-09-30', '14:00:00', false, now(), 3, now(), 3, null, null),
    (2, 1, 1, '2024-09-30', '17:00:00', false, now(), 3, now(), 3, null, null),
    (3, 2, 2, '2024-10-01', '15:00:00', false, now(), 4, now(), 4, null, null),
    (4, 2, 2, '2024-10-01', '20:00:00', false, now(), 4, now(), 4, null, null);
