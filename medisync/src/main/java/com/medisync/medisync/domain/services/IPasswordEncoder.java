        package com.medisync.medisync.domain.services;

        public interface IPasswordEncoder {
            String encode(String rawPassword);
            boolean matches(String rawPassword, String encodedPassword);
        }
