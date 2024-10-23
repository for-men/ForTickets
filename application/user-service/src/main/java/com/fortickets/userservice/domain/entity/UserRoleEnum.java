package com.fortickets.userservice.domain.entity;

public enum UserRoleEnum {

    USER(Authority.USER),
    SELLER(Authority.SELLER),  // 사용자 권한
    MANAGER(Authority.MANAGER);  // 사업주 권한

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {

        public static final String USER = "ROLE_USER";
        public static final String SELLER = "ROLE_SELLER";
        public static final String MANAGER = "ROLE_MANAGER";

    }
}
