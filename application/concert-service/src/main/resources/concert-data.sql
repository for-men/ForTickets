insert into concerts (concert_id, stage_id, user_id, concert_name, runtime, start_date, end_date, price, concert_image, is_deleted, created_at, created_by,
                      updated_at, updated_by, deleted_at, deleted_by)

values
    (1, 1, 3, '공연제목1', 120, '2024-09-01', '2024-11-01', 90000, 'https~', false, now(), 3, now(), 3, null, null),
    (2, 2, 4, '공연제목2', 150, '2024-09-02', '2024-11-02', 100000, 'https~', false, now(), 4, now(), 3, null, null);
