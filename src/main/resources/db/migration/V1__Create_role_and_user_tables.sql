-- Create role table
CREATE TABLE role (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT uk_role_name UNIQUE (name)
);

-- Create role_permission junction table
CREATE TABLE role_permission (
    role_id UUID NOT NULL,
    permission VARCHAR(255) NOT NULL,
    CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
);

-- Create users table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id UUID,
    created_by UUID,
    created_date TIMESTAMP,
    last_modified_by UUID,
    last_modified_date TIMESTAMP,
    deleted_by UUID,
    deleted_at TIMESTAMP,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT fk_users_roles FOREIGN KEY (role_id) REFERENCES role(id)
);

-- Create index on email for faster lookups
CREATE INDEX idx_users_email ON users(email);

-- Create index on role_id in users table
CREATE INDEX idx_users_role_id ON users(role_id);
