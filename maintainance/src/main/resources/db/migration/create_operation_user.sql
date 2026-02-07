-- SQL script to create OPERATION user for testing
-- Email: haitham.soliman94@gmail.com
-- Password: 12345 (BCrypt hash)
-- Note: You need to have at least one company in the database first
-- If no company exists, create one first or set company_id to NULL if allowed

-- First, ensure you have a company (if company_id cannot be NULL)
-- If you need to create a company first, uncomment and modify:
-- INSERT INTO companies (name, email, phone, address, company_code, created_at)
-- VALUES ('Test Company', 'test@company.com', '1234567890', 'Test Address', 'COMP001', NOW());

-- Insert OPERATION user
-- The password hash below is BCrypt hash for "12345"
-- You can generate a new one using: BCryptPasswordEncoder().encode("12345")
INSERT INTO users (name, email, phone, password_hash, role, status, company_id, created_at)
VALUES (
    'Haitham Soliman',
    'haitham.soliman94@gmail.com',
    '1234567890', -- You may need to adjust this to be unique
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- BCrypt hash for "12345"
    'OPERATION',
    'ACTIVE',
    (SELECT id FROM companies LIMIT 1), -- Uses first company, or set to NULL if allowed
    NOW()
)
ON CONFLICT (email) DO NOTHING;



