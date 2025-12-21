-- Add auditing columns to role table
ALTER TABLE role
ADD COLUMN created_by UUID,
ADD COLUMN created_date TIMESTAMP,
ADD COLUMN last_modified_by UUID,
ADD COLUMN last_modified_date TIMESTAMP;
