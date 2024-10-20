insert into users (nickname, email, phone, password, role, profile_image, is_deleted, created_at, created_by,
                   updated_at, updated_by, deleted_at, deleted_by)
values ('user1', 'user1@email.com', '01011111111', '$2a$10$CnNR2DDSkw4szC3GkBbeo.wswmyqdcIyDmiNMjcJ/5EVFELCbSbty', 'USER',
        'http://user.com/profile1.jpg', false, now(), 1, now(), 1, null, null),
       ('user2', 'user2@email.com', '01022222222', '$2a$10$GP3pop4mFpoJfvs2JlOHa.dBoLZp7WmlMpl1omMsD0L5qfMGDVOQy', 'USER',
        'http://user.com/profile2.jpg', false, now(), 2, now(), 2, null, null),
       ('seller1', 'seller1@email.com', '01033333333', '$2a$10$LKr1CjvIyaGZJShgdTMDruq.mThC61cTcMsDOLaBSuFH73C5zw6I6', 'SELLER',
        'http://seller.com/profile3.jpg', false, now(), 3, now(), 3, null, null),
       ('seller2', 'seller2@email.com', '01044444444', '$2a$10$BQz5M/WHnj0skq.2vMUxeeFHJkmOgyoHxWDHidkx0NhO4lR9UACj6', 'SELLER',
        'http://seller.com/profile4.jpg', false, now(), 4, now(), 4, null, null),
       ('manager1', 'manager1@email.com', '01055555555', '$2a$10$ckAJvYegkzTwJ2ACPsGNoecTy3DBVN4ld84510xlRtZOLYfWs2E12', 'MANAGER',
        'http://seller.com/profile5.jpg', false, now(), 5, now(), 5, null, null),
       ('manager2', 'manager2@email.com', '01066666666', '$2a$10$SMgy0oe09knNzcdZR4pKq.JwsGzA2y.qmuv4GfyBWSF/RQnlZg5li', 'MANAGER',
        'http://seller.com/profile6.jpg', false, now(), 6, now(), 6, null, null);
