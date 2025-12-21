-- Add soft delete columns to role table
ALTER TABLE role
ADD COLUMN deleted_by UUID,
ADD COLUMN deleted_at TIMESTAMP;
