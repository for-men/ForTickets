insert into stages (stage_id, name, location, row, col, is_deleted, created_at, created_by,
                      updated_at, updated_by, deleted_at, deleted_by)

values
    (1, '공연장이름1', '공연장위치1', 10, 100, false, now(), 5, now(), 5, null, null),
    (2, '공연장이름2', '공연장위치2', 20, 200, false, now(), 6, now(), 6, null, null);
