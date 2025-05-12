-- Migration to fix Data too long for column 'feedback' error
-- Changes the feedback column to LONGTEXT to allow much longer values

ALTER TABLE ai_validation_results MODIFY feedback LONGTEXT;
