insert into users (user_id, nickname, email, phone, password, role, profile_image, is_deleted, created_at, created_by,
                     updated_at, updated_by, deleted_at, deleted_by)

values
    (1, user1, user1@email.com, 010-1111-1111, /*인코딩된 패스워드값 넣어야됨*/, 'USER', 'http~', false, now(), 1, now(), 1, null, null),
    (2, user2, user2@email.com, 010-2222-2222, /*인코딩된 패스워드값 넣어야됨*/, 'USER', 'http~', false, now(), 2, now(), 2, null, null),
    (3, seller1, seller1@email.com, 010-3333-3333, /*인코딩된 패스워드값 넣어야됨*/, 'SELLER', 'http~', false, now(), 3, now(), 3, null, null),
    (4, seller2, seller2@email.com, 010-4444-4444, /*인코딩된 패스워드값 넣어야됨*/, 'SELLER', 'http~', false, now(), 4, now(), 4, null, null),
    (5, master1, master1@email.com, 010-5555-5555, /*인코딩된 패스워드값 넣어야됨*/, 'MASTER', 'http~', false, now(), 5, now(), 5, null, null),
    (6, master2, master2@email.com, 010-6666-6666, /*인코딩된 패스워드값 넣어야됨*/, 'MASTER', 'http~', false, now(), 6, now(), 6, null, null);
